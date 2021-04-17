package edu.mta.repository;

import java.util.List;
import java.util.Optional;

import edu.mta.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Integer>{



	Optional<Course> findByCourseName(String courseName);

	@Query("select o from Course o where o.courseID in :ids" )
	List<Course> findByCourseIds(@Param("ids") List<Integer> courseId);

}
