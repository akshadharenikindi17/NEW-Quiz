package com.adaptivequiz;

import com.adaptivequiz.dao.QuestionDAO;
import com.adaptivequiz.dao.StudentDAO;
import com.adaptivequiz.model.Question;
import com.adaptivequiz.model.QuizResult;
import com.adaptivequiz.model.Student;
import com.adaptivequiz.model.Subject;
import com.adaptivequiz.service.QuizEngine;

/** Headless demo (no window needed) used by GitHub Actions: java -jar adaptive-quiz.jar --demo */
public final class DemoRunner {
    private DemoRunner() { }

    public static void run() throws Exception {
        StudentDAO students = new StudentDAO();
        Student s = students.findById("DEMO1");
        if (s == null) {
            s = new Student("DEMO1", "Asha", 9, "4", "Rampur Village School");
            students.add(s);
            System.out.println("Registered student: " + s.describe());
        }

        Subject maths = null;
        for (Subject sub : new QuestionDAO().listSubjects()) {
            if (sub.getName().equals("Mathematics")) maths = sub;
        }
        if (maths == null) throw new IllegalStateException("Sample subject 'Mathematics' not found");

        System.out.println("\n--- Starting quiz for " + s.getName() + " in " + maths + " ---");
        QuizEngine engine = new QuizEngine(s, maths);
        engine.start();

        Question q;
        int n = 0;
        while ((q = engine.nextQuestion()) != null) {
            n++;
            // Simulated student: answers 2 of every 3 questions correctly.
            boolean answerCorrectly = (n % 3 != 0);
            char pick = answerCorrectly ? q.getCorrectOption() : (q.getCorrectOption() == 'A' ? 'B' : 'A');
            boolean ok = engine.submit(pick);
            System.out.printf("Q%-2d [%-6s] %-50s answered %c -> %s%n",
                    n, q.getDifficulty(), q.getText(), pick, ok ? "CORRECT" : "WRONG");
        }

        QuizResult r = engine.finish();
        System.out.println("\n===== RESULT =====");
        System.out.println(r.toSummary());

        System.out.println("\n===== PREVIOUS ATTEMPTS OF DEMO1 =====");
        new com.adaptivequiz.dao.QuizDAO().history("DEMO1").forEach(h ->
                System.out.printf("Attempt %d | %s | %d/%d correct | %.1f%% | %s%n",
                        h.getAttemptId(), h.getSubjectName(), h.getCorrect(), h.getTotalAttempted(),
                        h.getScorePercent(), h.getLevel()));
    }
}
