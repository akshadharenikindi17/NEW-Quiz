package com.adaptivequiz.model;

/** Performance bands based on the final score percentage. */
public enum PerformanceLevel {
    EXCELLENT("Excellent"),
    GOOD("Good"),
    NEEDS_PRACTICE("Needs Practice"),
    NEEDS_SUPPORT("Needs Support");

    private final String label;

    PerformanceLevel(String label) { this.label = label; }

    /** 80+ Excellent, 60-79 Good, 40-59 Needs Practice, below 40 Needs Support. */
    public static PerformanceLevel fromPercent(double percent) {
        if (percent >= 80) return EXCELLENT;
        if (percent >= 60) return GOOD;
        if (percent >= 40) return NEEDS_PRACTICE;
        return NEEDS_SUPPORT;
    }

    @Override
    public String toString() { return label; }
}
