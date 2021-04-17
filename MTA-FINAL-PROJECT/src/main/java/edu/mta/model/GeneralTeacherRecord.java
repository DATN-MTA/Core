package edu.mta.model;

public class GeneralTeacherRecord {

	private String className;
	private String courseName;
	private int sumOfAbsent;
	private int sumOfLessons;
	
	public GeneralTeacherRecord() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public int getSumOfAbsent() {
		return sumOfAbsent;
	}

	public void setSumOfAbsent(int sumOfAbsent) {
		this.sumOfAbsent = sumOfAbsent;
	}

	public int getSumOfLessons() {
		return sumOfLessons;
	}

	public void setSumOfLessons(int sumOfLessons) {
		this.sumOfLessons = sumOfLessons;
	}

}
