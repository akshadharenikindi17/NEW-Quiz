package com.adaptivequiz.dao;

import com.adaptivequiz.model.Difficulty;
import com.adaptivequiz.model.QuizResult;
import com.adaptivequiz.util.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO {

    public int startAttempt(String studentId, int subjectId) throws SQLException {
        String sql = "INSERT INTO quiz_attempt(student_id, subject_id) VALUES (?,?)";
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, studentId);
            ps.setInt(2, subjectId);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    /** Every answer is stored the moment it is submitted. */
    public void saveResponse(int attemptId, int questionId, char selected, boolean correct,
                             Difficulty difficulty) throws SQLException {
        String sql = "INSERT INTO quiz_response(attempt_id, question_id, selected_option, is_correct, difficulty_level) "
                   + "VALUES (?,?,?,?,?)";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, attemptId);
            ps.setInt(2, questionId);
            ps.setString(3, String.valueOf(selected));
            ps.setBoolean(4, correct);
            ps.setString(5, difficulty.name());
            ps.executeUpdate();
        }
    }

    public void saveResult(int attemptId, QuizResult r) throws SQLException {
        String sql = "INSERT INTO quiz_result(attempt_id, total_attempted, correct_answers, wrong_answers, "
                   + "score_percent, performance_level) VALUES (?,?,?,?,?,?)";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, attemptId);
            ps.setInt(2, r.getTotalAttempted());
            ps.setInt(3, r.getCorrect());
            ps.setInt(4, r.getWrong());
            ps.setDouble(5, r.getScorePercent());
            ps.setString(6, r.getLevel().toString());
            ps.executeUpdate();
        }
    }

    /** Previous attempts. Pass null to list every student's attempts. */
    public List<QuizResult> history(String studentId) throws SQLException {
        String sql = "SELECT a.attempt_id, st.name, sub.subject_name, a.started_at, "
                   + "r.total_attempted, r.correct_answers, r.wrong_answers "
                   + "FROM quiz_result r "
                   + "JOIN quiz_attempt a ON r.attempt_id = a.attempt_id "
                   + "JOIN student st ON a.student_id = st.student_id "
                   + "JOIN subject sub ON a.subject_id = sub.subject_id "
                   + (studentId != null ? "WHERE a.student_id = ? " : "")
                   + "ORDER BY a.attempt_id DESC";
        List<QuizResult> list = new ArrayList<>();
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (studentId != null) ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new QuizResult(rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getTimestamp(4).toLocalDateTime(), rs.getInt(5), rs.getInt(6), rs.getInt(7)));
                }
            }
        }
        return list;
    }
}
