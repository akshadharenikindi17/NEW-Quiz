package com.adaptivequiz.model;

public class Student extends Person {
    private final String studentId;
    private final String className;
    private final String villageOrSchool;

    public Student(String studentId, String name, int age, String className, String villageOrSchool) {
        super(name, age);
        this.studentId = studentId;
        this.className = className;
        this.villageOrSchool = villageOrSchool;
    }

    public String getStudentId() { return studentId; }
    public String getClassName() { return className; }
    public String getVillageOrSchool() { return villageOrSchool; }

    @Override
    public String describe() {
        return studentId + " - " + getName() + " (Class " + className + ", " + villageOrSchool + ")";
    }
}
