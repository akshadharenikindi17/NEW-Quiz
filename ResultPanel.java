package com.adaptivequiz.ui;

import com.adaptivequiz.dao.QuizDAO;
import com.adaptivequiz.model.QuizResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

/** Result Module: shows stored results and the previous attempts of a student. */
public class ResultPanel extends JPanel implements Refreshable {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private final QuizDAO dao = new QuizDAO();
    private final JTextField studentF = new JTextField(12);
    private final DefaultTableModel model = UiUtil.readOnlyModel(
            "Attempt", "Student", "Subject", "Date", "Attempted", "Correct", "Wrong", "Score %", "Performance");

    public ResultPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel bar = UiUtil.titled("Previous Quiz Attempts", new FlowLayout(FlowLayout.LEFT));
        bar.add(new JLabel("Student ID:"));
        bar.add(studentF);
        JButton showBtn = new JButton("Show Attempts");
        showBtn.addActionListener(e -> refresh());
        JButton allBtn = new JButton("Show All Students");
        allBtn.addActionListener(e -> { studentF.setText(""); refresh(); });
        bar.add(showBtn);
        bar.add(allBtn);

        add(bar, BorderLayout.NORTH);
        add(new JScrollPane(UiUtil.table(model)), BorderLayout.CENTER);
        refresh();
    }

    public void showFor(String studentId) {
        studentF.setText(studentId);
        refresh();
    }

    @Override
    public void refresh() {
        String id = studentF.getText().trim();
        try {
            model.setRowCount(0);
            for (QuizResult r : dao.history(id.isEmpty() ? null : id)) {
                model.addRow(new Object[]{r.getAttemptId(), r.getStudentName(), r.getSubjectName(),
                        r.getStartedAt().format(FMT), r.getTotalAttempted(), r.getCorrect(), r.getWrong(),
                        String.format("%.1f", r.getScorePercent()), r.getLevel()});
            }
        } catch (SQLException ex) {
            UiUtil.error(this, ex);
        }
    }
}
