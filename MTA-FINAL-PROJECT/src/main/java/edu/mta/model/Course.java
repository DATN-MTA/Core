package edu.mta.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "course")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int courseID;

	@Column(name = "CourseName", nullable = false, length = 30, unique = true)
	private String courseName;

	@Column(name = "Description", nullable = true)
	private String description;

	public Course() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Course(String courseName, String description) {
		super();
		this.courseName = courseName;
		this.description = description;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCourseID() {
		return courseID;
	}

	public void setCourseID(int courseID) {
		this.courseID = courseID;
	}

	/*
	 * public List<Class> getClasses() { return classes; }
	 * 
	 * public void setClasses(List<Class> classes) { this.classes = classes; }
	 */

	/*
	 * public void addClass(Class classInstance) { this.classes.add(classInstance);
	 * classInstance.setCourse(this); }
	 * 
	 * public void removeClass(Class classInstance) {
	 * this.classes.remove(classInstance); classInstance.setCourse(null); }
	 */
}
