package com.adaptivequiz.model;

/** The three difficulty levels. Each level knows its neighbours. */
public enum Difficulty {
    EASY("Easy"), MEDIUM("Medium"), HARD("Hard");

    private final String label;

    Difficulty(String label) { this.label = label; }

    /** Next level up (Hard stays Hard). */
    public Difficulty harder() { return this == EASY ? MEDIUM : HARD; }

    /** Next level down (Easy stays Easy). */
    public Difficulty easier() { return this == HARD ? MEDIUM : EASY; }

    @Override
    public String toString() { return label; }
}
