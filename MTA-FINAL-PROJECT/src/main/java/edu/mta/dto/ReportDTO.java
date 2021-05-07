package edu.mta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

public class ReportDTO {


    @ApiModelProperty(position = 2, required = true, example = "18-06-2021")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private String beginAt;

    @ApiModelProperty(position = 3, required = true, example = "11-12-2021")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private String finishAt;

    @ApiModelProperty(position = 4, required = true, example = "pdf")
    private String fileType;



    public String getBeginAt() {
        return beginAt;
    }

    public void setBeginAt(String beginAt) {
        this.beginAt = beginAt;
    }

    public String getFinishAt() {
        return finishAt;
    }

    public void setFinishAt(String finishAt) {
        this.finishAt = finishAt;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
