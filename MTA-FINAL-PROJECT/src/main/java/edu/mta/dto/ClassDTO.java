package edu.mta.dto;

public class ClassDTO {
    private int classId;
    private String semester;
    private String course;
    private int numberOfLession;
    private int numberOfStudents;
    private String className;
    private int currentLession;

    public ClassDTO() {
    }

    public ClassDTO(String semester, String course, int numberOfLession, int numberOfStudents, String className, int currentLession) {
        this.semester = semester;
        this.course = course;
        this.numberOfLession = numberOfLession;
        this.numberOfStudents = numberOfStudents;
        this.className = className;
        this.currentLession = currentLession;
    }

    @Override
    public String toString() {
        return "ClassDTO{" +
                "semester='" + semester + '\'' +
                ", course='" + course + '\'' +
                ", numberOfLession=" + numberOfLession +
                ", numberOfStudents=" + numberOfStudents +
                ", className='" + className + '\'' +
                ", currentLession=" + currentLession +
                '}';
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getNumberOfLession() {
        return numberOfLession;
    }

    public void setNumberOfLession(int numberOfLession) {
        this.numberOfLession = numberOfLession;
    }

    public int getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(int numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getCurrentLession() {
        return currentLession;
    }

    public void setCurrentLession(int currentLession) {
        this.currentLession = currentLession;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }
}
