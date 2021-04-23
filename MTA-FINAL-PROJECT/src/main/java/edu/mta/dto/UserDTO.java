/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class UserDTO {
    private String address;
    private String fullName;
    
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate birthDay;
    private String phone;
    private String imei;
    private int updateImeiCounter;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(LocalDate birthDay) {
        this.birthDay = birthDay;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public int getUpdateImeiCounter() {
        return updateImeiCounter;
    }

    public void setUpdateImeiCounter(int updateImeiCounter) {
        this.updateImeiCounter = updateImeiCounter;
    }
    
}
