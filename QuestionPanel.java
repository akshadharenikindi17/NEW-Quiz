package com.adaptivequiz.ui;

import com.adaptivequiz.dao.QuestionDAO;
import com.adaptivequiz.model.Difficulty;
import com.adaptivequiz.model.Question;
import com.adaptivequiz.model.Subject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/** Question Management Module: add, view all, search by subject / difficulty. */
public class QuestionPanel extends JPanel implements Refreshable {
    private final QuestionDAO dao = new QuestionDAO();

    private final JComboBox<Subject> subjectBox = new JComboBox<>();
    private final JTextField newSubjectF = new JTextField(10);
    private final JTextArea questionArea = new JTextArea(3, 22);
    private final JTextField[] optionF = { new JTextField(), new JTextField(), new JTextField(), new JTextField() };
    private final JComboBox<String> correctBox = new JComboBox<>(new String[]{"A", "B", "C", "D"});
    private final JComboBox<Difficulty> difficultyBox = new JComboBox<>(Difficulty.values());

    private final JComboBox<Subject> filterSubject = new JComboBox<>();
    private final JComboBox<Object> filterDifficulty = new JComboBox<>();

    private final DefaultTableModel model = UiUtil.readOnlyModel(
            "ID", "Subject", "Difficulty", "Question", "A", "B", "C", "D", "Answer");

    public QuestionPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        filterDifficulty.addItem("All");
        for (Difficulty d : Difficulty.values()) filterDifficulty.addItem(d);

        // ----- form (left) -----
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);

        JPanel form = UiUtil.titled("Add New Question", new GridBagLayout());
        UiUtil.addRow(form, 0, "Subject", subjectBox);

        JButton addSubjectBtn = new JButton("+ Subject");
        addSubjectBtn.addActionListener(e -> addSubject());
        JPanel subjRow = new JPanel(new BorderLayout(4, 0));
        subjRow.add(newSubjectF, BorderLayout.CENTER);
        subjRow.add(addSubjectBtn, BorderLayout.EAST);
        UiUtil.addRow(form, 1, "New subject", subjRow);

        UiUtil.addRow(form, 2, "Question", new JScrollPane(questionArea));
        String[] letters = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) UiUtil.addRow(form, 3 + i, "Option " + letters[i], optionF[i]);
        UiUtil.addRow(form, 7, "Correct answer", correctBox);
        UiUtil.addRow(form, 8, "Difficulty", difficultyBox);

        JButton addBtn = new JButton("Add Question");
        addBtn.addActionListener(e -> addQuestion());
        UiUtil.addRow(form, 9, "", addBtn);

        JPanel west = new JPanel(new BorderLayout());
        west.add(form, BorderLayout.NORTH);
        add(west, BorderLayout.WEST);

        // ----- filter bar + table (centre) -----
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bar.add(new JLabel("Subject:"));
        bar.add(filterSubject);
        bar.add(new JLabel("Difficulty:"));
        bar.add(filterDifficulty);
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> search());
        bar.add(searchBtn);

        JPanel center = new JPanel(new BorderLayout(6, 6));
        center.add(bar, BorderLayout.NORTH);
        center.add(new JScrollPane(UiUtil.table(model)), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        refresh();
    }

    private void addSubject() {
        String name = newSubjectF.getText().trim();
        if (name.isEmpty()) {
            UiUtil.warn(this, "Type a subject name first.");
            return;
        }
        try {
            Subject s = dao.addSubject(name);
            newSubjectF.setText("");
            loadSubjects();
            subjectBox.setSelectedItem(findSubject(subjectBox, s.getId()));
        } catch (SQLException ex) {
            UiUtil.error(this, ex);
        }
    }

    private void addQuestion() {
        Subject subject = (Subject) subjectBox.getSelectedItem();
        String text = questionArea.getText().trim();
        String[] opts = new String[4];
        for (int i = 0; i < 4; i++) opts[i] = optionF[i].getText().trim();

        if (subject == null) { UiUtil.warn(this, "Please add / select a subject."); return; }
        if (text.isEmpty()) { UiUtil.warn(this, "Please type the question."); return; }
        for (String o : opts) {
            if (o.isEmpty()) { UiUtil.warn(this, "Please fill in all four options."); return; }
        }
        char correct = ((String) correctBox.getSelectedItem()).charAt(0);
        Difficulty diff = (Difficulty) difficultyBox.getSelectedItem();
        try {
            dao.add(new Question(0, subject, text, opts, correct, diff));
            UiUtil.info(this, "Question added.");
            questionArea.setText("");
            for (JTextField f : optionF) f.setText("");
            search();
        } catch (SQLException ex) {
            UiUtil.error(this, ex);
        }
    }

    private void search() {
        Subject s = (Subject) filterSubject.getSelectedItem();
        Object d = filterDifficulty.getSelectedItem();
        try {
            List<Question> list = dao.search(
                    s == null ? 0 : s.getId(),
                    d instanceof Difficulty ? (Difficulty) d : null);
            model.setRowCount(0);
            for (Question q : list) {
                model.addRow(new Object[]{q.getId(), q.getSubject().getName(), q.getDifficulty(), q.getText(),
                        q.getOption(0), q.getOption(1), q.getOption(2), q.getOption(3), q.getCorrectOption()});
            }
        } catch (SQLException ex) {
            UiUtil.error(this, ex);
        }
    }

    private void loadSubjects() throws SQLException {
        Subject keepForm = (Subject) subjectBox.getSelectedItem();
        Subject keepFilter = (Subject) filterSubject.getSelectedItem();
        subjectBox.removeAllItems();
        filterSubject.removeAllItems();
        filterSubject.addItem(new Subject(0, "All"));
        for (Subject s : dao.listSubjects()) {
            subjectBox.addItem(s);
            filterSubject.addItem(s);
        }
        if (keepForm != null) subjectBox.setSelectedItem(findSubject(subjectBox, keepForm.getId()));
        if (keepFilter != null) filterSubject.setSelectedItem(findSubject(filterSubject, keepFilter.getId()));
    }

    private Subject findSubject(JComboBox<Subject> box, int id) {
        for (int i = 0; i < box.getItemCount(); i++) {
            if (box.getItemAt(i).getId() == id) return box.getItemAt(i);
        }
        return null;
    }

    @Override
    public void refresh() {
        try {
            loadSubjects();
            search();
        } catch (SQLException ex) {
            UiUtil.error(this, ex);
        }
    }
}
