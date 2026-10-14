package com.adaptivequiz.service;

import com.adaptivequiz.dao.QuestionDAO;
import com.adaptivequiz.dao.QuizDAO;
import com.adaptivequiz.model.Difficulty;
import com.adaptivequiz.model.Question;
import com.adaptivequiz.model.QuizResult;
import com.adaptivequiz.model.Student;
import com.adaptivequiz.model.Subject;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Application logic for one quiz attempt. The UI only calls these methods. */
public class QuizEngine {
    public static final int MAX_QUESTIONS = 10;

    private final Student student;
    private final Subject subject;
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final QuizDAO quizDAO = new QuizDAO();
    private final DifficultyStrategy strategy;
    private final Set<Integer> askedIds = new HashSet<>();

    private int attemptId;
    private int correct;
    private int wrong;
    private Difficulty target = Difficulty.EASY;   // the quiz always starts with an Easy question
    private Question current;

    public QuizEngine(Student student, Subject subject) {
        this(student, subject, new AdaptiveStrategy());
    }

    public QuizEngine(Student student, Subject subject, DifficultyStrategy strategy) {
        this.student = student;
        this.subject = subject;
        this.strategy = strategy;
    }

    public void start() throws SQLException {
        attemptId = quizDAO.startAttempt(student.getStudentId(), subject.getId());
    }

    /** Next question, or null when the quiz is over (limit reached or no questions left). */
    public Question nextQuestion() throws SQLException {
        if (askedIds.size() >= MAX_QUESTIONS) return null;

        // Prefer the target level; if that level has no unused questions, use the closest level.
        List<Difficulty> order = new ArrayList<>(List.of(Difficulty.values()));
        order.sort(Comparator.comparingInt(d -> Math.abs(d.ordinal() - target.ordinal())));

        for (Difficulty d : order) {
            Question q = questionDAO.pickRandom(subject.getId(), d, askedIds);
            if (q != null) {
                askedIds.add(q.getId());
                current = q;
                return q;
            }
        }
        return null;
    }

    /** Checks the answer, stores it in the database and adjusts the next difficulty. */
    public boolean submit(char option) throws SQLException {
        boolean ok = Character.toUpperCase(option) == current.getCorrectOption();
        quizDAO.saveResponse(attemptId, current.getId(), Character.toUpperCase(option), ok, current.getDifficulty());
        if (ok) correct++; else wrong++;
        target = strategy.nextDifficulty(current.getDifficulty(), ok);
        return ok;
    }

    /** Calculates the result and stores it (only if at least one question was answered). */
    public QuizResult finish() throws SQLException {
        QuizResult r = new QuizResult(attemptId, student.getName(), subject.getName(),
                LocalDateTime.now(), correct + wrong, correct, wrong);
        if (r.getTotalAttempted() > 0) quizDAO.saveResult(attemptId, r);
        return r;
    }

    public int getQuestionNumber() { return askedIds.size(); }
    public Question getCurrentQuestion() { return current; }
}
