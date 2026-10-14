package com.adaptivequiz.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

final class UiUtil {
    private UiUtil() { }

    static void info(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    static void warn(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Please check", JOptionPane.WARNING_MESSAGE);
    }

    static void error(Component parent, Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(parent, "Something went wrong:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    static DefaultTableModel readOnlyModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
    }

    static JTable table(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(26);
        t.setAutoCreateRowSorter(true);
        t.setFillsViewportHeight(true);
        return t;
    }

    /** Adds "label : field" as one row of a GridBagLayout form. */
    static void addRow(JPanel p, int row, String label, JComponent field) {
        GridBagConstraints l = new GridBagConstraints();
        l.gridx = 0; l.gridy = row; l.anchor = GridBagConstraints.NORTHWEST; l.insets = new Insets(4, 4, 4, 8);
        p.add(new JLabel(label), l);
        GridBagConstraints f = new GridBagConstraints();
        f.gridx = 1; f.gridy = row; f.fill = GridBagConstraints.HORIZONTAL; f.weightx = 1; f.insets = new Insets(4, 0, 4, 4);
        p.add(field, f);
    }

    static JPanel titled(String title, LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBorder(BorderFactory.createTitledBorder(title));
        return p;
    }
}
