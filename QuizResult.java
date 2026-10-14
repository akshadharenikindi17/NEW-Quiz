package com.adaptivequiz.model;

import java.time.LocalDateTime;

/** Summary of one quiz attempt. Score and performance level are derived, never set by hand. */
public class QuizResult {
    private final int attemptId;
    private final String studentName;
    private final String subjectName;
    private final LocalDateTime startedAt;
    private final int totalAttempted;
    private final int correct;
    private final int wrong;

    public QuizResult(int attemptId, String studentName, String subjectName,
                      LocalDateTime startedAt, int totalAttempted, int correct, int wrong) {
        this.attemptId = attemptId;
        this.studentName = studentName;
        this.subjectName = subjectName;
        this.startedAt = startedAt;
        this.totalAttempted = totalAttempted;
        this.correct = correct;
        this.wrong = wrong;
    }

    public int getAttemptId() { return attemptId; }
    public String getStudentName() { return studentName; }
    public String getSubjectName() { return subjectName; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public int getTotalAttempted() { return totalAttempted; }
    public int getCorrect() { return correct; }
    public int getWrong() { return wrong; }

    public double getScorePercent() {
        return totalAttempted == 0 ? 0.0 : Math.round(correct * 10000.0 / totalAttempted) / 100.0;
    }

    public PerformanceLevel getLevel() { return PerformanceLevel.fromPercent(getScorePercent()); }

    public String toSummary() {
        return "Student Name      : " + studentName + "\n"
             + "Subject           : " + subjectName + "\n"
             + "Questions Attempted: " + totalAttempted + "\n"
             + "Correct Answers   : " + correct + "\n"
             + "Wrong Answers     : " + wrong + "\n"
             + "Final Score       : " + String.format("%.1f%%", getScorePercent()) + "\n"
             + "Performance Level : " + getLevel();
    }
}
