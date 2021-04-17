package edu.mta.utils;

import java.time.LocalDate;

public interface ValidationSemesterData {

	String validateIdData(int id);
	
	/**
	 * @param semesterName - should be in format "Year + sequence".Ex: 20181
	 * @return
	 */
	String validateSemesterNameData(String semesterName);
	
	String validateBeginDateData(LocalDate beginDate, int validYear);
	
	String validateEndDateData(LocalDate endDate, LocalDate beginDate, int sequenceOfSemester);
}
