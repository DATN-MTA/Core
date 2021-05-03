package edu.mta.service;

import edu.mta.model.Account;
import edu.mta.model.ClassRoom;
import edu.mta.model.ReportError;
import edu.mta.model.StudentClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * @author BePro
 *
 */
public interface StudentClassService {

	/**
	 * @param studentID
	 * @param semesterID
	 * @return a list of ClassRoom that is learned in this semester by student
	 */
	List<ClassRoom> getTimeTable(int studentID, int semesterID);

	/**
	 * @param studentID
	 * @param classID
	 * @param identifyString
	 * @return true if server can find a record with the above fields
	 */
	String checkStudentHasAuthority(int studentID, int classID, int roomID, String identifyString, String imei);

	//boolean checkGenerateTimeValid(int weekday, LocalTime generateTime, int classID, int roomID);
	
	String rollCall(int classID, int studentID, LocalDateTime rollCallAt, String imei);

	List<StudentClass> findByClassID(int id);
	
	boolean updateStudentClassInfo(StudentClass studentClass);

	List<StudentClass> findCurrentStudentsByClassID(int id);

	boolean checkStudentIsLearning(int studentID, int classID);
	
	boolean checkIsCheckedValid(String isChecked, LocalTime beginAt, LocalTime finishAt);

	boolean checkStudentIsLearning(String studentEmail, int classID);

	String checkTimetableConflict(Account account, int classID);

	void saveNewStudentClass(StudentClass studentClass);

	
	/**
	 * Get the classID which is put in last element of list email (for purpose of hacking)
	 * @param listStudentEmail
	 * @return -1 if error happen; an unsigned int if success
	 */
	int getClassIDInLastElement(List<String> listStudentEmail);

	
	/**
	 * Filter all emails which is invalid; 
	 * list of invalid rows is stored in the last element of return list
	 * @param listStudentEmail
	 * @return a List of email (String)
	 */
	List<String> filterListEmail(List<String> listStudentEmail, int classID);

	String addNewStudentClass(String studentEmail, int classID);

	/**
	 * Filter all emails which is invalid; 
	 * list of invalid rows is stored in the last element of return list
	 * @param listStudentRollcall
	 * @param classID
	 * @return
	 */
	List<ReportError> checkListRollcallEmail(List<ReportError> listStudentRollcall, int classID);

	String rollcallByEmailAndClassID(ReportError studentRollcall, int classID, int roomID);

	Page<StudentClass> findStudentByClassId (int ClassId, Pageable pageable);

}
