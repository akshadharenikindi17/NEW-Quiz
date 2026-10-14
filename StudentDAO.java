package com.adaptivequiz.dao;

import com.adaptivequiz.model.Student;
import com.adaptivequiz.util.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public void add(Student s) throws SQLException {
        String sql = "INSERT INTO student(student_id, name, age, class_name, village_school) VALUES (?,?,?,?,?)";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getStudentId());
            ps.setString(2, s.getName());
            ps.setInt(3, s.getAge());
            ps.setString(4, s.getClassName());
            ps.setString(5, s.getVillageOrSchool());
            ps.executeUpdate();
        }
    }

    public Student findById(String id) throws SQLException {
        String sql = "SELECT * FROM student WHERE student_id = ?";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Student> findAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        try (Connection c = Db.get(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM student ORDER BY student_id")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    private Student map(ResultSet rs) throws SQLException {
        return new Student(rs.getString("student_id"), rs.getString("name"), rs.getInt("age"),
                rs.getString("class_name"), rs.getString("village_school"));
    }
}
