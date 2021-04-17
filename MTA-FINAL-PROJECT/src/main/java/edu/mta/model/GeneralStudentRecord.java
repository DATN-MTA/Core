package edu.mta.model;

public class GeneralStudentRecord {

	private String className;
	private String courseName;
	private int sumOfAbsent;
	private int sumOfSick;
	private int sumOfFogettingPhone;
	private int sumOfLessons;
	
	public GeneralStudentRecord() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GeneralStudentRecord(String className, String courseName, int sumOfAbsent, int sumOfSick, int sumOfFogettingPhone,
			int sumOfLessons) {
		super();
		this.className = className;
		this.courseName = courseName;
		this.sumOfAbsent = sumOfAbsent;
		this.sumOfSick = sumOfSick;
		this.sumOfFogettingPhone = sumOfFogettingPhone;
		this.sumOfLessons = sumOfLessons;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getSumOfAbsent() {
		return sumOfAbsent;
	}

	public void setSumOfAbsent(int sumOfAbsent) {
		this.sumOfAbsent = sumOfAbsent;
	}

	public int getSumOfSick() {
		return sumOfSick;
	}

	public void setSumOfSick(int sumOfSick) {
		this.sumOfSick = sumOfSick;
	}

	public int getSumOfFogettingPhone() {
		return sumOfFogettingPhone;
	}

	public void setSumOfFogettingPhone(int sumOfFogettingPhone) {
		this.sumOfFogettingPhone = sumOfFogettingPhone;
	}

	public int getSumOfLessons() {
		return sumOfLessons;
	}

	public void setSumOfLessons(int sumOfLessons) {
		this.sumOfLessons = sumOfLessons;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	
	
}
