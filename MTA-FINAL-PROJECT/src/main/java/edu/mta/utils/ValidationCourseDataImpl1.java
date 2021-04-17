package edu.mta.utils;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ValidationCourseDataImpl1")
public class ValidationCourseDataImpl1 implements ValidationCourseData {

	@Override
	public String validateIdData(int id) {
		if (id < 1) {
			return "Id can not be less than 1!";
		}
		return null;
	}

	@Override
	public String validateCourseNameData(String courseName) {
		if (courseName == null || courseName.isBlank()) {
			return "Missing semesterName data!";
		}
		return null;
	}

}
