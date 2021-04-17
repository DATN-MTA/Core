package edu.mta.utils;

public interface ValidationClassData {

	String validateIdData(int id);
	
	String validateClassNameData(String className);
	
	String validateMaxStudent(int maxStudent);
	
	String validateNumberOfLessons(int numberOfLesson);
	
	//this data has some complicate businesses with listRollCall of student and teacher => set = 0
	//String validateCurrentLesson(int currentLesson, int numberOfLesson);
}
