package com.adaptivequiz.ui;

import com.adaptivequiz.dao.QuestionDAO;
import com.adaptivequiz.dao.StudentDAO;
import com.adaptivequiz.model.Question;
import com.adaptivequiz.model.QuizResult;
import com.adaptivequiz.model.Student;
import com.adaptivequiz.model.Subject;
import com.adaptivequiz.service.QuizEngine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.function.Consumer;

/** Quiz Module: shows one question at a time and shows the Result when the quiz ends. */
public class QuizPanel extends JPanel implements Refreshable {
    private final StudentDAO studentDAO = new StudentDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();

    private final JTextField studentF = new JTextField(10);
    private final JComboBox<Subject> subjectBox = new JComboBox<>();
    private final JButton startBtn = new JButton("Start Quiz");
    private final JButton submitBtn = new JButton("Submit Answer");
    private final JButton endBtn = new JButton("End Quiz");

    private final JLabel infoL = new JLabel("Select a student and subject, then press Start Quiz.");
    private final JLabel feedbackL = new JLabel(" ");
    private final JTextArea questionArea = new JTextArea(4, 40);
    private final JRadioButton[] optionBtn = new JRadioButton[4];
    private final ButtonGroup group = new ButtonGroup();

    private final Consumer<QuizResult> onFinished;
    private QuizEngine engine;
    private Student student;

    public QuizPanel(Consumer<QuizResult> onFinished) {
        this.onFinished = onFinished;
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        // ----- top: choose student + subject -----
        JPanel top = UiUtil.titled("Start a Quiz", new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Student ID:"));
        top.add(studentF);
        top.add(new JLabel("Subject:"));
        top.add(subjectBox);
        top.add(startBtn);
        add(top, BorderLayout.NORTH);

        // ----- centre: question card -----
        questionArea.setEditable(false);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setFont(questionArea.getFont().deriveFont(Font.BOLD, 20f));
        questionArea.setBackground(getBackground());

        JPanel options = new JPanel(new GridLayout(4, 1, 4, 4));
        for (int i = 0; i < 4; i++) {
            optionBtn[i] = new JRadioButton();
            optionBtn[i].setFont(optionBtn[i].getFont().deriveFont(16f));
            group.add(optionBtn[i]);
            options.add(optionBtn[i]);
        }

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(submitBtn);
        buttons.add(endBtn);

        JPanel card = UiUtil.titled("Question", new BorderLayout(8, 8));
        JPanel head = new JPanel(new GridLayout(2, 1));
        head.add(infoL);
        head.add(feedbackL);
        card.add(head, BorderLayout.NORTH);
        JPanel mid = new JPanel(new BorderLayout(8, 8));
        mid.add(questionArea, BorderLayout.NORTH);
        mid.add(options, BorderLayout.CENTER);
        card.add(mid, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);
        add(card, BorderLayout.CENTER);

        startBtn.addActionListener(e -> startQuiz());
        submitBtn.addActionListener(e -> submitAnswer());
        endBtn.addActionListener(e -> finishQuiz());
        setQuizActive(false);
    }

    private void setQuizActive(boolean active) {
        startBtn.setEnabled(!active);
        studentF.setEnabled(!active);
        subjectBox.setEnabled(!active);
        submitBtn.setEnabled(active);
        endBtn.setEnabled(active);
        for (JRadioButton b : optionBtn) b.setEnabled(active);
        if (!active) {
            group.clearSelection();
            questionArea.setText("");
            for (JRadioButton b : optionBtn) b.setText("");
        }
    }

    private void startQuiz() {
        String id = studentF.getText().trim();
        Subject subject = (Subject) subjectBox.getSelectedItem();
        if (id.isEmpty() || subject == null) {
            UiUtil.warn(this, "Enter a Student ID and choose a subject.");
            return;
        }
        try {
            student = studentDAO.findById(id);
            if (student == null) {
                UiUtil.warn(this, "No student found with ID '" + id + "'. Register the student first.");
                return;
            }
            if (questionDAO.search(subject.getId(), null).isEmpty()) {
                UiUtil.warn(this, "There are no questions for " + subject + " yet.");
                return;
            }
            engine = new QuizEngine(student, subject);
            engine.start();
            feedbackL.setText(" ");
            setQuizActive(true);
            showNext();
        } catch (SQLException ex) {
            UiUtil.error(this, ex);
        }
    }

    private void showNext() throws SQLException {
        Question q = engine.nextQuestion();
        if (q == null) {          // question limit reached or pool exhausted
            finishQuiz();
            return;
        }
        infoL.setText("Question " + engine.getQuestionNumber() + "  |  Level: " + q.getDifficulty()
                + "  |  Student: " + student.getName());
        questionArea.setText(q.getText());
        String[] letters = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) optionBtn[i].setText(letters[i] + ".  " + q.getOption(i));
        group.clearSelection();
    }

    private void submitAnswer() {
        int chosen = -1;
        for (int i = 0; i < 4; i++) if (optionBtn[i].isSelected()) chosen = i;
        if (chosen < 0) {
            UiUtil.warn(this, "Please choose an answer first.");
            return;
        }
        try {
            char correctLetter = engine.getCurrentQuestion().getCorrectOption();
            boolean ok = engine.submit((char) ('A' + chosen));
            if (ok) {
                feedbackL.setForeground(new Color(0, 130, 0));
                feedbackL.setText("Correct! Great job - the next question will be harder.");
            } else {
                feedbackL.setForeground(new Color(190, 0, 0));
                feedbackL.setText("Not correct. The right answer was " + correctLetter + ".");
            }
            showNext();
        } catch (SQLException ex) {
            UiUtil.error(this, ex);
        }
    }

    private void finishQuiz() {
        try {
            QuizResult r = engine.finish();
            setQuizActive(false);
            infoL.setText("Quiz finished. Select a student and subject to start another.");
            feedbackL.setText(" ");
            if (r.getTotalAttempted() == 0) {
                UiUtil.info(this, "Quiz ended before any question was answered, so no result was saved.");
            } else {
                UiUtil.info(this, "QUIZ RESULT\n\n" + r.toSummary());
                onFinished.accept(r);
            }
            engine = null;
        } catch (SQLException ex) {
            UiUtil.error(this, ex);
        }
    }

    /** Student ID the finished quiz belonged to (used to jump to the Results tab). */
    public String getStudentId() { return studentF.getText().trim(); }

    @Override
    public void refresh() {
        if (engine != null) return;      // do not disturb a running quiz
        try {
            Subject keep = (Subject) subjectBox.getSelectedItem();
            subjectBox.removeAllItems();
            for (Subject s : questionDAO.listSubjects()) {
                subjectBox.addItem(s);
                if (keep != null && keep.getId() == s.getId()) subjectBox.setSelectedItem(s);
            }
        } catch (SQLException ex) {
            UiUtil.error(this, ex);
        }
    }
}
