package edu.mta.repository;

import edu.mta.model.Class;
import edu.mta.model.TeacherClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherClassRepository extends JpaRepository<TeacherClass, Integer>{

	@Query("SELECT tc.classInstance.id FROM TeacherClass tc WHERE tc.account.id = ?1 AND tc.classInstance.semester.semesterID = ?2")
	List<Integer> getListClass(int teacherID, int semesterID);

	@Query("SELECT tc FROM TeacherClass tc WHERE tc.isTeaching = ?3 AND tc.account.id = ?1 AND tc.classInstance.id = ?2")
	Optional<TeacherClass> findByTeacherIDAndClassIDAndStatus(int teacherID, int classID, int isTeaching);
	
	@Query("SELECT tc FROM TeacherClass tc WHERE tc.classInstance.id = ?1")
	List<TeacherClass> getListTeacherClass(int id);
	
	@Query("SELECT tc FROM TeacherClass tc WHERE tc.classInstance.id = ?1 AND tc.isTeaching = ?2")
	Optional<TeacherClass> findByClassIDAndStatus(int classID, int value);
	
	@Query("SELECT tc.classInstance FROM TeacherClass tc WHERE tc.account.id = ?1 AND tc.isTeaching = ?2")
	List<Class> findByTeacherIDAndStatus(int teacherID, int value);

	@Query("SELECT tc FROM TeacherClass tc WHERE tc.account.id = ?1 AND tc.isTeaching = ?2")
	List<TeacherClass> findByCurrentTeacherID(int teacherID, int isLearning);

	Page<TeacherClass> getTeacherClassByAccount_EmailAndClassInstance_ClassName(String teacherEmail, String className, Pageable pageable);

}
