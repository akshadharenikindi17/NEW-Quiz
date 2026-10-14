package com.adaptivequiz.ui;

import com.adaptivequiz.dao.StudentDAO;
import com.adaptivequiz.model.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/** Student Registration Module: add, view all, search by Student ID. */
public class StudentPanel extends JPanel implements Refreshable {
    private final StudentDAO dao = new StudentDAO();

    private final JTextField idF = new JTextField(14);
    private final JTextField nameF = new JTextField(14);
    private final JTextField ageF = new JTextField(14);
    private final JTextField classF = new JTextField(14);
    private final JTextField villageF = new JTextField(14);
    private final JTextField searchF = new JTextField(12);
    private final DefaultTableModel model =
            UiUtil.readOnlyModel("Student ID", "Name", "Age", "Class", "Village / School");

    public StudentPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        // ----- form (left) -----
        JPanel form = UiUtil.titled("Register New Student", new GridBagLayout());
        UiUtil.addRow(form, 0, "Student ID", idF);
        UiUtil.addRow(form, 1, "Student Name", nameF);
        UiUtil.addRow(form, 2, "Age", ageF);
        UiUtil.addRow(form, 3, "Class", classF);
        UiUtil.addRow(form, 4, "Village / School", villageF);
        JButton addBtn = new JButton("Add Student");
        addBtn.addActionListener(e -> addStudent());
        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> clearForm());
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btns.add(addBtn);
        btns.add(clearBtn);
        UiUtil.addRow(form, 5, "", btns);

        JPanel west = new JPanel(new BorderLayout());
        west.add(form, BorderLayout.NORTH);
        add(west, BorderLayout.WEST);

        // ----- search bar + table (centre) -----
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bar.add(new JLabel("Search by Student ID:"));
        bar.add(searchF);
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> search());
        JButton allBtn = new JButton("View All Students");
        allBtn.addActionListener(e -> refresh());
        bar.add(searchBtn);
        bar.add(allBtn);

        JPanel center = new JPanel(new BorderLayout(6, 6));
        center.add(bar, BorderLayout.NORTH);
        center.add(new JScrollPane(UiUtil.table(model)), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        refresh();
    }

    private void addStudent() {
        String id = idF.getText().trim();
        String name = nameF.getText().trim();
        String cls = classF.getText().trim();
        String village = villageF.getText().trim();
        if (id.isEmpty() || name.isEmpty() || cls.isEmpty() || village.isEmpty()) {
            UiUtil.warn(this, "Please fill in every field.");
            return;
        }
        int age;
        try {
            age = Integer.parseInt(ageF.getText().trim());
        } catch (NumberFormatException ex) {
            UiUtil.warn(this, "Age must be a number.");
            return;
        }
        if (age < 3 || age > 25) {
            UiUtil.warn(this, "Age must be between 3 and 25.");
            return;
        }
        try {
            if (dao.findById(id) != null) {
                UiUtil.warn(this, "A student with ID '" + id + "' already exists.");
                return;
            }
            dao.add(new Student(id, name, age, cls, village));
            UiUtil.info(this, "Student registered successfully.");
            clearForm();
            refresh();
        } catch (SQLException ex) {
            UiUtil.error(this, ex);
        }
    }

    private void search() {
        String id = searchF.getText().trim();
        if (id.isEmpty()) {
            refresh();
            return;
        }
        try {
            Student s = dao.findById(id);
            model.setRowCount(0);
            if (s == null) {
                UiUtil.info(this, "No student found with ID '" + id + "'.");
            } else {
                addRow(s);
            }
        } catch (SQLException ex) {
            UiUtil.error(this, ex);
        }
    }

    private void addRow(Student s) {
        model.addRow(new Object[]{s.getStudentId(), s.getName(), s.getAge(), s.getClassName(), s.getVillageOrSchool()});
    }

    private void clearForm() {
        idF.setText(""); nameF.setText(""); ageF.setText(""); classF.setText(""); villageF.setText("");
    }

    @Override
    public void refresh() {
        try {
            List<Student> all = dao.findAll();
            model.setRowCount(0);
            for (Student s : all) addRow(s);
        } catch (SQLException ex) {
            UiUtil.error(this, ex);
        }
    }
}
