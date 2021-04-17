package edu.mta.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import edu.mta.model.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Integer> {

	Optional<Semester> findBySemesterName(String semesterName);

	@Transactional
	void deleteBySemesterName(String semesterName);

	@Query("SELECT se FROM Semester se WHERE ?1 BETWEEN se.beginDate AND se.endDate")
	Optional<Semester> getSemesterIDByTime(LocalDate date);
	
	@Query("SELECT se FROM Semester se WHERE ?1 BETWEEN se.beginDate AND se.endDate")
	List<Semester> checkSemesterDuplicate(LocalDate beginDate);
}
