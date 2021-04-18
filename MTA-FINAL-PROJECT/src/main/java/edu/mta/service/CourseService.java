package edu.mta.service;

import java.util.List;

import edu.mta.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseService {

	Course findCourseById(int id);

	boolean addNewCourse(Course course);

	Course getCourseInfo(int id);

	boolean updateCourseInfo(Course course);

	boolean deleteCourse(int id);

	Page<Course> findAllCourse(Pageable page);

	List<Course> findBySemester(String semester);

}
