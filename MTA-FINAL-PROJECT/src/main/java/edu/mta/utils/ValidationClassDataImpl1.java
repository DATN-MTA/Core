package edu.mta.utils;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ValidationClassDataImpl1")
public class ValidationClassDataImpl1 implements ValidationClassData {

	@Override
	public String validateIdData(int id) {
		if (id < 1) {
			return "Id can not be less than 1!";
		}
		return null;
	}

	@Override
	public String validateClassNameData(String className) {
		if (className == null || className.isBlank()) {
			return "Missing roomName data!";
		}
		return null;
	}

	@Override
	public String validateMaxStudent(int maxStudent) {
		if (maxStudent < GeneralValue.minStudents || maxStudent > GeneralValue.maxStudents) {
			return "MaxStudent is out of valid range";
		}
		return null;
	}

	@Override
	public String validateNumberOfLessons(int numberOfLesson) {
		if (numberOfLesson < 1) {
			return "Number of lessons cannot be less than 1";
		}
		return null;
	}

}
