package edu.mta.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "semester")
public class Semester implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int semesterID;

	@Column(name = "SemesterName", nullable = false, length = 8, unique = true)
	private String semesterName;

	@Column(name = "BeginDate", nullable = false)
	private LocalDate beginDate;

	@Column(name = "EndDate", nullable = false)
	private LocalDate endDate;

	public Semester() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Semester(String semesterName, LocalDate beginDate, LocalDate endDate) {
		super();
		this.semesterName = semesterName;
		this.beginDate = beginDate;
		this.endDate = endDate;
	}

	public Semester(int semesterID, String semesterName, LocalDate beginDate, LocalDate endDate) {
		super();
		this.semesterID = semesterID;
		this.semesterName = semesterName;
		this.beginDate = beginDate;
		this.endDate = endDate;
	}

	public String getSemesterName() {
		return semesterName;
	}

	public void setSemesterName(String semesterName) {
		this.semesterName = semesterName;
	}

	public LocalDate getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(LocalDate beginDate) {
		this.beginDate = beginDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public int getSemesterID() {
		return semesterID;
	}

	public void setSemesterID(int semesterID) {
		this.semesterID = semesterID;
	}

}
