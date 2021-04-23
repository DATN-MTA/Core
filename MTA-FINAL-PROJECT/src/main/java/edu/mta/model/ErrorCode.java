/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mta.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "error_code")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ErrorCode implements java.io.Serializable {

    // Fields]
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Size(min = 1, max = 1000, message = "Minimum username length: 4 characters")
    private String errorType;
    
    @Column(unique = true, nullable = false)
    private String errorCode;
    
    @Size(min = 4, max = 1000, message = "Minimum username length: 4 characters")
    private String description;
    
    @Size(min = 4, max = 1000, message = "Minimum username length: 4 characters")
    private String descriptionVi;
    
    private Long status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionVi() {
        return descriptionVi;
    }

    public void setDescriptionVi(String descriptionVi) {
        this.descriptionVi = descriptionVi;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
    
    
    
}

