package edu.mta.repository;

import java.util.List;
import java.util.Optional;

import edu.mta.model.Class;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<Class, Integer>, CustomClassRepository{

	@Query("SELECT cl FROM Class cl WHERE cl.semester.semesterName = ?1")
	List<Class> findBySemesterName(String semesterName);

	Optional<Class> findByClassName(String className);

	@Query("SELECT cl FROM Class cl WHERE cl.course.courseID = ?1")
	List<Class> findByCourseID(int courseID);

	@Query("SELECT cl.id FROM Class cl WHERE cl.course.courseID = ?1")
	List<Integer> findListClassIdByCourseID(int courseID);

	@Query("SELECT cl FROM Class cl WHERE cl.semester.semesterID = ?1")
	Page<Class> findBySemesterID(Integer semesterId, Pageable pageable);

	@Query("SELECT cl FROM Class cl WHERE cl.course.courseID = ?1")
	Page<Class> findByCourseID(Integer courseId, Pageable pageable);

	@Query("SELECT cl FROM Class cl WHERE cl.semester.semesterID = ?1 and cl.course.courseID = ?2")
	Page<Class> findBySemesterIDAndCourseID(Integer semesterId, Integer courseId, Pageable pageable);
}
