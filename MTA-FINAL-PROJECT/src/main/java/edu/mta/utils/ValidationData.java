package edu.mta.utils;

import java.util.Map;

/**
 * @author BePro
 *
 */
public interface ValidationData {
	
	String validateAccountData(Map<String, Object> mapKeys);
	
	String validateUserData(Map<String, Object> mapKeys);
	
	String validateSemesterData(Map<String, Object> mapKeys);
	
	String validateClassData(Map<String, Object> mapKeys);
	
	String validateRoomData(Map<String, Object> mapKeys);

	String validateCourseData(Map<String, Object> mapKeys);
	
	String validateClassRoomData(Map<String, Object> mapKeys);
	
	boolean validateTeacherClassData(int teacherID, int classID, int isTeaching);

	boolean validateStudentClassData(int studentID, int classID, int isLearning);
	
	

}
