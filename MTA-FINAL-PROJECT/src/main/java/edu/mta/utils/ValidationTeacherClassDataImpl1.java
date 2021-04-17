package edu.mta.utils;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ValidationTeacherClassDataImpl1")
public class ValidationTeacherClassDataImpl1 implements ValidationTeacherClassData {

	@Override
	public String validateIdData(int id) {
		if (id < 1) {
			return "Id can not be less than 1!";
		}
		return null;
	}

}
