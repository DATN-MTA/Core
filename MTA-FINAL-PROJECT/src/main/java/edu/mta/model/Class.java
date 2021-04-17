package edu.mta.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "class")
public class Class implements Serializable {
	
	public static int numberOfEvents = 0;
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;

	@Column(name = "ClassName", nullable = false, length = 30)
	private String className;

	@Column(name = "MaxStudent", nullable = false)
	private int maxStudent;
	
	@Column(name = "IdentifyString", nullable = true, length = 10)
	private String identifyString;
	
	@Column(name = "NumberOfLessons", nullable = false)
	private int numberOfLessons;
	
	@Column(name = "CurrentLesson", nullable = false)
	private int currentLesson;
	
	@Column(name = "IsChecked", nullable = true, length = 16)
	private String isChecked;

	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CourseID", nullable = false)
	private Course course;

	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SemesterID", nullable = false)
	private Semester semester;

	public Class() {
		super();
		this.isChecked = null;
	}

	public Class(String className, int maxStudent, int numberOfLessons) {
		super();
		this.className = className;
		this.maxStudent = maxStudent;
		this.numberOfLessons = numberOfLessons;
	}

	public Class(String name, int maxStudent) {
		super();
		this.className = name;
		this.maxStudent = maxStudent;
		this.isChecked = null;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getMaxStudent() {
		return maxStudent;
	}

	public void setMaxStudent(int maxStudent) {
		this.maxStudent = maxStudent;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Semester getSemester() {
		return semester;
	}

	public void setSemester(Semester semester) {
		this.semester = semester;
	}

	public String getIdentifyString() {
		return identifyString;
	}

	public void setIdentifyString(String identifyString) {
		this.identifyString = identifyString;
	}

	public int getCurrentLesson() {
		return currentLesson;
	}

	public void setCurrentLesson(int currentLesson) {
		this.currentLesson = currentLesson;
	}

	public String getIsChecked() {
		return isChecked;
	}

	public void setIsChecked(String isChecked) {
		this.isChecked = isChecked;
	}

	public int getNumberOfLessons() {
		return numberOfLessons;
	}

	public void setNumberOfLessons(int numberOfLessons) {
		this.numberOfLessons = numberOfLessons;
	}

	
}
