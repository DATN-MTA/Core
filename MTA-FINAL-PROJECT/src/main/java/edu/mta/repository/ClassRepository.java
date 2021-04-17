package edu.mta.repository;

import java.util.List;
import java.util.Optional;

import edu.mta.model.Class;
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
}
