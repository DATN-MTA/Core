package edu.mta.utils;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
@Qualifier("ValidationSemesterDataImpl1")
public class ValidationSemesterDataImpl1 implements ValidationSemesterData {

	@Override
	public String validateIdData(int id) {
		if (id < 1) {
			return "Id can not be less than 1!";
		}
		return null;
	}

	@Override
	public String validateSemesterNameData(String semesterName) {
		if (semesterName == null || semesterName.isEmpty()) {
			return "Missing semesterName data!";
		}
		
		//semesterName = year + 1( or 2 or 3)
//		String year = semesterName.substring(0, 4);
//		String sequence = semesterName.substring(4);
//		try {
//			int parseYear = Integer.parseInt(year);
//			if (parseYear < 2015) {
//				return "Info of year in semesterName is out of valid range!";
//			}
//			
//			int parseSequence = Integer.parseInt(sequence);
//			if (parseSequence < 1 || parseSequence > GeneralValue.semestersInYear) {
//				return "Sequence of this semester is out of valid range!";
//			}
//		} catch(NumberFormatException e) {
//			e.printStackTrace();
//			return "Cannot parse info in semesterName to number!";
//		}
		
		return null;
	}

	@Override
	public String validateBeginDateData(LocalDate beginDate, int validYear) {
		int year = beginDate.getYear();
		
		//year must be in range validYear - 1
		if (year == validYear || year == validYear + 1) {
			return null;
		}
		return "BeginDate is out of valid range!";
	}

	@Override
	public String validateEndDateData(LocalDate endDate, LocalDate beginDate, int sequenceOfSemester) {
		long semesterLength = ChronoUnit.DAYS.between(beginDate, endDate) + 1;

		
		if (endDate.isBefore(beginDate)) {
			return "endDate must be after beginDate";
		}
		
		if (sequenceOfSemester == GeneralValue.sequenceOfSummerSemester) {
			if (semesterLength > GeneralValue.lengthOfSummerSemester) {
				return "Length of this semester is out of valid range!";
			}
		} else {
			if (semesterLength > GeneralValue.lengthOfNormalSemester) {
				return "Length of this semester is out of valid range!";
			}
		}
		return null;
	}

}
