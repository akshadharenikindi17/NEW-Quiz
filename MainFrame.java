package com.adaptivequiz.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("Adaptive Quiz System for Children");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 640);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        ResultPanel results = new ResultPanel();
        QuizPanel quiz = new QuizPanel(r -> {
            // when a quiz ends, jump to the Results tab and show that student's attempts
            results.showFor(quizStudentId(tabs));
            tabs.setSelectedComponent(results);
        });
        tabs.addTab("1. Students", new StudentPanel());
        tabs.addTab("2. Questions", new QuestionPanel());
        tabs.addTab("3. Take Quiz", quiz);
        tabs.addTab("4. Results", results);

        // reload data whenever a tab is opened
        tabs.addChangeListener(e -> {
            Component c = tabs.getSelectedComponent();
            if (c instanceof Refreshable) ((Refreshable) c).refresh();
        });

        JLabel title = new JLabel("  Adaptive Quiz System for Children", SwingConstants.LEFT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        setLayout(new BorderLayout());
        add(title, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private static String quizStudentId(JTabbedPane tabs) {
        Component c = tabs.getComponentAt(2);
        return c instanceof QuizPanel ? ((QuizPanel) c).getStudentId() : "";
    }
}
