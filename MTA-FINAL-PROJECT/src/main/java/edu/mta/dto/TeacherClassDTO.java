package edu.mta.dto;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

@Validated
public class TeacherClassDTO {
    @ApiModelProperty(position = 0, required = true, example = "null value for create")
    private int id;

    @ApiModelProperty(position = 1, required = true, example = "abc@gmail.com")
    private String teacherEmail;

    @ApiModelProperty(position = 2, required = true, example = "1")
    private int classID;

    @ApiModelProperty(position = 3, required = true, example = "ATTT01")
    private String className;

    @ApiModelProperty(position = 4, required = true, example = "1 for teaching, 0 for not")
    private int isTeaching;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getIsTeaching() {
        return isTeaching;
    }

    public void setIsTeaching(int isTeaching) {
        this.isTeaching = isTeaching;
    }
}
