package edu.mta.utils;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ValidationStudentClassDataImpl1")
public class ValidationStudentClassDataImpl1 implements ValidationStudentClassData{

	@Override
	public String validateIdData(int id) {
		if (id < 1) {
			return "Id can not be less than 1!";
		}
		return null;
	}

}
