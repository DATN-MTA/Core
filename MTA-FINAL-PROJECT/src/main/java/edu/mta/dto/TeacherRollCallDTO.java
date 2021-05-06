package edu.mta.dto;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

@Validated
public class TeacherRollCallDTO {

    @ApiModelProperty(position = 0, required = true)
    private int teacherID;

    @ApiModelProperty(position = 1, required = true)
    private int classID;

    @ApiModelProperty(position = 2, required = true)
    private int roomID;

    public int getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(int teacherID) {
        this.teacherID = teacherID;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }
}
