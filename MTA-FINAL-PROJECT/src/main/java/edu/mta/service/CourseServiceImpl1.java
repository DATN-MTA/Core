package edu.mta.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import edu.mta.model.Class;
import edu.mta.model.Course;
import edu.mta.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("CourseServiceImpl1")
public class CourseServiceImpl1 implements CourseService {

	private CourseRepository courseRepository;

	@Autowired
	private SemesterService semesterService;
	@Autowired
	private ClassService classService;
	// private ClassRepository classRepository;

	public CourseServiceImpl1() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Autowired
	public CourseServiceImpl1(CourseRepository courseRepository) {
		super();
		this.courseRepository = courseRepository;
		// this.classRepository = classRepository;
	}

	@Override
	public boolean addNewCourse(Course course) {
		// check if another course has use this name
		if (this.courseRepository.findByCourseName(course.getCourseName()).isPresent()) {
			return false;
		}
		this.courseRepository.save(course);
		return true;
	}

	@Override
	public Course findCourseById(int id) {
		return this.courseRepository.findById(id).get();
	}

	@Override
	public Course getCourseInfo(int id) {
		Optional<Course> course = this.courseRepository.findById(id);
		if (course.isPresent()) {
			return course.get();
		}
		return null;
	}

	@Override
	public boolean updateCourseInfo(Course course) {
		Optional<Course> courseInfo = this.courseRepository.findById(course.getCourseID());
		if (courseInfo.isPresent()) {
			// course name must be unique
			Optional<Course> duplicateCourse = this.courseRepository.findByCourseName(course.getCourseName());
			if (duplicateCourse.isPresent()) {
				return false;
			}

			this.courseRepository.save(course);
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteCourse(int id) {
		try {
			this.courseRepository.deleteById(id);
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<Course> findAllCourse() {
		List<Course> listCourse = this.courseRepository.findAll();
		if (listCourse == null || listCourse.isEmpty()) {
			return null;
		}
		return listCourse;
	}

	@Override
	public List<Course> findBySemester(String semester) {
		List<Class> classList = classService.getClassBySemesterName(semester);
		List<Course> listCourses = new ArrayList<>();
		for (Class classObject: classList) {
			listCourses.add(classObject.getCourse());
			System.out.println("CourseId = " + classObject.getCourse().getCourseID());

		}
		if (listCourses == null || listCourses.isEmpty()) {
			return null;
		}
		return listCourses;
	}
}
