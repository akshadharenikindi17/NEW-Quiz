package com.adaptivequiz.model;

/** Abstract base class (abstraction + inheritance). */
public abstract class Person {
    private final String name;
    private final int age;

    protected Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() { return name; }
    public int getAge() { return age; }

    /** Each subclass describes itself differently (polymorphism). */
    public abstract String describe();
}
