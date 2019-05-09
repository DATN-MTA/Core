package edu.hust.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.hust.model.StudentClass;

@Repository
public interface StudentClassRepository extends JpaRepository<StudentClass, Integer>{

	@Query("SELECT sc.classInstance.id FROM StudentClass sc WHERE sc.account.id = ?1 AND sc.classInstance.semester.semesterID = ?2")
	List<Integer> getListClass(int studentID, int semesterID);
	
	@Query("SELECT sc FROM StudentClass sc WHERE sc.isLearning = ?3 AND sc.account.id = ?1 AND sc.classInstance.id = ?2")
	Optional<StudentClass> findByStudentIDAndClassIDAndStatus(int studentID, int classID, int isLearning);
	
	@Query("SELECT sc FROM StudentClass sc WHERE sc.classInstance.id = ?1")
	List<StudentClass> getListStudentClass(int id);
	
	@Query("SELECT sc FROM StudentClass sc WHERE sc.classInstance.id = ?1 AND sc.isLearning = ?2")
	List<StudentClass> getListCurrentStudentClass(int id, int isLearning);
	
	@Query("SELECT sc FROM StudentClass sc WHERE sc.isLearning = ?3 AND sc.account.email = ?1 AND sc.classInstance.id = ?2")
	Optional<StudentClass> findByStudentEmailAndClassIDAndStatus(String studentEmail, int classID, int value);
	
	@Query("SELECT sc FROM StudentClass sc WHERE sc.account.id = ?1 AND sc.classInstance.semester.semesterID = ?2 and sc.isLearning = ?3")
	List<StudentClass> getListClass(int studentID, int semesterID, int isLearning);
	
	@Query("SELECT sc FROM StudentClass sc WHERE sc.account.email = ?1 AND sc.classInstance.semester.semesterID = ?2 and sc.isLearning = ?3")
	List<StudentClass> getListClass(String studentEmail, int semesterID, int value);
}
