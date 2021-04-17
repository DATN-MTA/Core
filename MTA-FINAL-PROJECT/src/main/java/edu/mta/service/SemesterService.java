package edu.mta.service;

import java.time.LocalDate;
import java.util.List;

import edu.mta.model.Semester;

public interface SemesterService {
	
	int getSemesterIDByDate(LocalDate currentDate);

	Semester findSemesterByName(String semesterName);

	void addNewSemester(Semester semester);

	boolean checkSemesterTimeDuplicate(LocalDate beginDate, LocalDate endDate, String semesterName);

	boolean checkSemesterDependant(String semesterName);

	void deleteSemester(String semesterName);

	Semester findSemesterById(int id);

	void updateSemesterInfo(Semester semester);

	List<Semester> findAllSemester();
}
