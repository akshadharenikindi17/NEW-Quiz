package com.adaptivequiz;

import com.adaptivequiz.ui.MainFrame;
import com.adaptivequiz.util.Db;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.Collections;

public class Main {

    public static void main(String[] args) throws Exception {
        Db.init();   // creates tables + sample data on first run

        if (Arrays.asList(args).contains("--demo")) {
            DemoRunner.run();
            return;
        }
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("No display found, so the window cannot open.");
            System.out.println("Run with --demo for a console demo, or use a machine / Codespace with a desktop.");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) { }
            Font f = new Font("SansSerif", Font.PLAIN, 14);   // slightly larger text for children
            for (Object key : Collections.list(UIManager.getDefaults().keys())) {
                if (UIManager.get(key) instanceof FontUIResource) UIManager.put(key, new FontUIResource(f));
            }
            new MainFrame().setVisible(true);
        });
    }
}
