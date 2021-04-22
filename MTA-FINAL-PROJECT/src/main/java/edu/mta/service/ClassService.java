package edu.mta.service;

import java.time.LocalDate;
import java.util.List;

import edu.mta.model.Class;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClassService {

	boolean addNewClass(Class classInstance);

	Class findClassByID(int classID);

	boolean updateClassInfo(Class classInstance);

	boolean deleteClassInfo(int classID);

	boolean checkAddingTime(LocalDate addingDate, int semesterId);

	Class findClassByClassName(String className);

	List<Class> getListClassByCourseID(int courseID);

	List<Class> getClassBySemesterName(String semesterName);

	Page<Class> getAllClasses(Pageable pageable);

	Page<Class> getClassesBySemesterAndCourse(Pageable pageable, Integer semesterId, Integer courseId);

}
