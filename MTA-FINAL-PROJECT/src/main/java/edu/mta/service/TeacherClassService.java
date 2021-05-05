package edu.mta.service;

import edu.mta.dto.TeacherClassDTO;
import edu.mta.model.Account;
import edu.mta.model.ClassRoom;
import edu.mta.model.TeacherClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface TeacherClassService {

	List<ClassRoom> getTimeTable(int teacherID, int semesterID);
	
	boolean checkTeacherHasAuthority(int teacherID, int classID);
	
	/**
	 * @param weekday - an integer for a day in week (Monday = 2)
	 * @param generateTime - when server create variable
	 * @param classID
	 * @param roomID
	 * @return true if server check generateTime and weekday are valid
	 */
	boolean checkGenerateTimeValid(int weekday, LocalTime generateTime, int classID, int roomID);
	
	/**
	 * @param classID
	 * @param roomID
	 * @param weekday - an integer for a day in week (Monday = 2)
	 * @param inputMd5 - a String for MD5 encode
	 * @return true if server can generate identifyString
	 */
	String generateIdentifyString(int classID, int roomID, int weekday, String inputMd5);
	
	boolean rollCall(LocalDateTime rollCallAt, int teacherID, int classID);

	/**
	 * Find all teacher has involved the class, regardless status
	 * @param id
	 * @return a list of TeacherClass if records are found; null if none exists
	 */
	List<TeacherClass> findByClassID(int id);

	
	/**
	 * Find teacher who is teaching the class
	 * @param classID
	 * @return an instance of TeacherClass if record is found; null if none exists
	 */
	TeacherClass findCurrentTeacherByClassID(int classID);
	
	boolean updateTeacherClass(TeacherClass teacherClass);

	boolean rollCallStudentWithPermission(String studentEmail, int classID, int roomID, int reason);

	boolean checkTimetableConflict(int teacherID, int classID);

	String addNewTeacherClass(TeacherClassDTO teacherClassDTO);

	String checkReasonValid(int reason);
	
	String createRollcallDate(LocalDateTime rollCallAt);

	List<TeacherClass> findByCurrentTeacherID(int teacherID);

	String addNewTeacherClass(String teacherEmail, int classID);
	
	boolean checkTeacherIsTeaching(String teacherEmail, int classID);
	
	String checkTimetableConflict(Account account, int classID);

	Page<TeacherClass> getTeacherClassByTeacherEmailOrClassName(String teacherEmail, String className, Pageable pageable);
	
}
