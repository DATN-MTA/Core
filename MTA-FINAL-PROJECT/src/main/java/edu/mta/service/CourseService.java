package edu.mta.service;

import java.util.List;

import edu.mta.model.Course;

public interface CourseService {

	Course findCourseById(int id);

	boolean addNewCourse(Course course);

	Course getCourseInfo(int id);

	boolean updateCourseInfo(Course course);

	boolean deleteCourse(int id);

	List<Course> findAllCourse();

	List<Course> findBySemester(String semester);

}
