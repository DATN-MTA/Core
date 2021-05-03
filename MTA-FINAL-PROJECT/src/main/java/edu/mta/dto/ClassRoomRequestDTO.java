package edu.mta.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Valid
public class ClassRoomRequestDTO {

    @ApiModelProperty(position = 0, required = true, example = "null value for create")
    @Valid
    private Integer id;

    @ApiModelProperty(position = 1, required = true, example = "06::00")
    @Valid
    private String beginAt;

    @ApiModelProperty(position = 2, required = true, example = "00:00:00")
    @Valid
    @NotNull(message = "finishAt can't be NULL")
    private String finishAt;

    @ApiModelProperty(position = 3, required = true, example = "2")
    @Valid
    private Integer weekday;

    @ApiModelProperty(position = 4, required = true)
    @Valid
    private Integer classID;

    @ApiModelProperty(position = 5, required = true)
    @Valid
    private Integer roomID;

    public ClassRoomRequestDTO(Integer id, String beginAt, String finishAt, Integer weekday, Integer classID, Integer roomID) {
        this.id = id;
        this.beginAt = beginAt;
        this.finishAt = finishAt;
        this.weekday = weekday;
        this.classID = classID;
        this.roomID = roomID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Integer getWeekday() {
        return weekday;
    }

    public void setWeekday(Integer weekday) {
        this.weekday = weekday;
    }

    public Integer getClassID() {
        return classID;
    }

    public void setClassID(Integer classID) {
        this.classID = classID;
    }

    public Integer getRoomID() {
        return roomID;
    }

    public void setRoomID(Integer roomID) {
        this.roomID = roomID;
    }
}
