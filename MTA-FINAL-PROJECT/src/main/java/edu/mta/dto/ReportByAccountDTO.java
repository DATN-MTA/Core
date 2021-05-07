package edu.mta.dto;

import io.swagger.annotations.ApiModelProperty;

public class ReportByAccountDTO extends ReportDTO{
    @ApiModelProperty(position = 0, example = "if admin export report then pass email, if students or teacher pass null")
    private String email;

    @ApiModelProperty(position = 1, required = true)
    private String semesterID;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getSemesterID() {
        return semesterID;
    }

    public void setSemesterID(String semesterID) {
        this.semesterID = semesterID;
    }
}
