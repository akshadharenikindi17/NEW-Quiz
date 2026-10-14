package com.adaptivequiz.model;

public class Question {
    private final int id;
    private final Subject subject;
    private final String text;
    private final String[] options;      // A, B, C, D
    private final char correctOption;    // 'A'..'D'
    private final Difficulty difficulty;

    public Question(int id, Subject subject, String text, String[] options,
                    char correctOption, Difficulty difficulty) {
        if (options == null || options.length != 4) {
            throw new IllegalArgumentException("A question needs exactly four options");
        }
        this.id = id;
        this.subject = subject;
        this.text = text;
        this.options = options.clone();
        this.correctOption = Character.toUpperCase(correctOption);
        this.difficulty = difficulty;
    }

    public int getId() { return id; }
    public Subject getSubject() { return subject; }
    public String getText() { return text; }
    public String getOption(int index) { return options[index]; }
    public char getCorrectOption() { return correctOption; }
    public Difficulty getDifficulty() { return difficulty; }
}
