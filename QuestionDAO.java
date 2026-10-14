package com.adaptivequiz.dao;

import com.adaptivequiz.model.Difficulty;
import com.adaptivequiz.model.Question;
import com.adaptivequiz.model.Subject;
import com.adaptivequiz.util.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class QuestionDAO {

    private static final String BASE =
        "SELECT q.question_id, q.subject_id, s.subject_name, q.question_text, "
      + "q.option_a, q.option_b, q.option_c, q.option_d, q.correct_option, q.difficulty "
      + "FROM question q JOIN subject s ON q.subject_id = s.subject_id";

    // ---------- subjects ----------
    public List<Subject> listSubjects() throws SQLException {
        List<Subject> list = new ArrayList<>();
        try (Connection c = Db.get(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT subject_id, subject_name FROM subject ORDER BY subject_name")) {
            while (rs.next()) list.add(new Subject(rs.getInt(1), rs.getString(2)));
        }
        return list;
    }

    /** Adds a subject if it does not already exist and returns it. */
    public Subject addSubject(String name) throws SQLException {
        try (Connection c = Db.get()) {
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT subject_id, subject_name FROM subject WHERE LOWER(subject_name) = LOWER(?)")) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return new Subject(rs.getInt(1), rs.getString(2));
                }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO subject(subject_name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    return new Subject(keys.getInt(1), name);
                }
            }
        }
    }

    // ---------- questions ----------
    public void add(Question q) throws SQLException {
        String sql = "INSERT INTO question(subject_id, question_text, option_a, option_b, option_c, option_d, "
                   + "correct_option, difficulty) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, q.getSubject().getId());
            ps.setString(2, q.getText());
            for (int i = 0; i < 4; i++) ps.setString(3 + i, q.getOption(i));
            ps.setString(7, String.valueOf(q.getCorrectOption()));
            ps.setString(8, q.getDifficulty().name());
            ps.executeUpdate();
        }
    }

    /** @param subjectId 0 = any subject   @param difficulty null = any difficulty */
    public List<Question> search(int subjectId, Difficulty difficulty) throws SQLException {
        StringBuilder sql = new StringBuilder(BASE).append(" WHERE 1=1");
        if (subjectId > 0) sql.append(" AND q.subject_id = ?");
        if (difficulty != null) sql.append(" AND q.difficulty = ?");
        sql.append(" ORDER BY q.question_id");

        List<Question> list = new ArrayList<>();
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            int i = 1;
            if (subjectId > 0) ps.setInt(i++, subjectId);
            if (difficulty != null) ps.setString(i, difficulty.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    /** Random question of the given subject and difficulty that has not been asked yet; null if none left. */
    public Question pickRandom(int subjectId, Difficulty difficulty, Set<Integer> alreadyAsked) throws SQLException {
        List<Question> pool = new ArrayList<>();
        for (Question q : search(subjectId, difficulty)) {
            if (!alreadyAsked.contains(q.getId())) pool.add(q);
        }
        if (pool.isEmpty()) return null;
        return pool.get(ThreadLocalRandom.current().nextInt(pool.size()));
    }

    private Question map(ResultSet rs) throws SQLException {
        Subject subject = new Subject(rs.getInt("subject_id"), rs.getString("subject_name"));
        String[] options = { rs.getString("option_a"), rs.getString("option_b"),
                             rs.getString("option_c"), rs.getString("option_d") };
        return new Question(rs.getInt("question_id"), subject, rs.getString("question_text"), options,
                rs.getString("correct_option").charAt(0), Difficulty.valueOf(rs.getString("difficulty")));
    }
}
