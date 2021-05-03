package edu.mta.dto;

import java.time.LocalTime;

public class ClassRoomResponseDTO {
    private int classRoomId;
    private String semesterName;
    private String courseName;
    private String className;
    private LocalTime beginAt;
    private LocalTime finishAt;
    private int DayOfWeek;
    private String roomName;

    public ClassRoomResponseDTO() {
    }

    public ClassRoomResponseDTO(String semesterName, String courseName, String className, LocalTime beginAt, LocalTime finishAt, int dayOfWeek, String roomName) {
        this.semesterName = semesterName;
        this.courseName = courseName;
        this.className = className;
        this.beginAt = beginAt;
        this.finishAt = finishAt;
        DayOfWeek = dayOfWeek;
        this.roomName = roomName;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public LocalTime getBeginAt() {
        return beginAt;
    }

    public void setBeginAt(LocalTime beginAt) {
        this.beginAt = beginAt;
    }

    public LocalTime getFinishAt() {
        return finishAt;
    }

    public void setFinishAt(LocalTime finishAt) {
        this.finishAt = finishAt;
    }

    public int getDayOfWeek() {
        return DayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        DayOfWeek = dayOfWeek;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getClassRoomId() {
        return classRoomId;
    }

    public void setClassRoomId(int classRoomId) {
        this.classRoomId = classRoomId;
    }
}
