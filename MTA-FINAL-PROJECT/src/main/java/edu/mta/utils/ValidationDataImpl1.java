package edu.mta.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import edu.mta.enumData.IsLearning;
import edu.mta.enumData.IsTeaching;

@Component
@Qualifier("ValidationDataImpl1")
public class ValidationDataImpl1 implements ValidationData {

	private ValidationAccountData validationAccountData;
	private ValidationUserData validationUserData;
	private ValidationSemesterData validationSemesterData;
	private ValidationCourseData validationCourseData;
	private ValidationRoomData validationRoomData;
	private ValidationClassData validationClassData;
	private ValidationClassRoomData validationClassRoomData;

	public ValidationDataImpl1() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Autowired
	public ValidationDataImpl1(@Qualifier("ValidationAccountDataImpl1") ValidationAccountData validationAccountData,
			@Qualifier("ValidationUserDataImpl1") ValidationUserData validationUserData,
			@Qualifier("ValidationSemesterDataImpl1") ValidationSemesterData validationSemesterData,
			@Qualifier("ValidationCourseDataImpl1") ValidationCourseData validationCourseData, 
			@Qualifier("ValidationRoomDataImpl1") ValidationRoomData validationRoomData, 
			@Qualifier("ValidationClassDataImpl1") ValidationClassData validationClassData,
			@Qualifier("ValidationClassRoomDataImpl1") ValidationClassRoomData validationClassRoomData) {
		super();
		this.validationAccountData = validationAccountData;
		this.validationUserData = validationUserData;
		this.validationSemesterData = validationSemesterData;
		this.validationCourseData = validationCourseData;
		this.validationRoomData = validationRoomData;
		this.validationClassData = validationClassData;
		this.validationClassRoomData = validationClassRoomData;
	}

	@Override
	public String validateAccountData(Map<String, Object> mapKeys) {
		String errorMessage = null;

		if (mapKeys.containsKey("id")) {
			int id = Integer.parseInt(mapKeys.get("id").toString());
			errorMessage = this.validationAccountData.validateIdData(id);
		}

		if (errorMessage == null && mapKeys.containsKey("username")) {
			errorMessage = this.validationAccountData.validateUsernameData(mapKeys.get("username").toString());
		}

		if (errorMessage == null && mapKeys.containsKey("password")) {
			errorMessage = this.validationAccountData.validatePasswordData(mapKeys.get("password").toString());
		}

		if (errorMessage == null && mapKeys.containsKey("imei")) {
			errorMessage = this.validationAccountData.validateImeiData(mapKeys.get("imei").toString());
		}

		if (errorMessage == null && mapKeys.containsKey("email")) {
			errorMessage = this.validationAccountData.validateEmailData(mapKeys.get("email").toString());
		}

		if (errorMessage == null && mapKeys.containsKey("role")) {
			int role = Integer.parseInt(mapKeys.get("role").toString());
			errorMessage = this.validationAccountData.validateRoleData(role);
		}

		return errorMessage;
	}

	@Override
	public String validateUserData(Map<String, Object> mapKeys) {
		String errorMessage = null;

		if (mapKeys.containsKey("id")) {
			int id = Integer.parseInt(mapKeys.get("id").toString());
			errorMessage = this.validationUserData.validateIdData(id);
		}

		if (errorMessage == null && mapKeys.containsKey("address")) {
			errorMessage = this.validationUserData.validateAddressData(mapKeys.get("address").toString());
		}
		
		if (errorMessage == null && mapKeys.containsKey("fullName")) {
			errorMessage = this.validationUserData.validateFullNameData(mapKeys.get("fullName").toString());
		}

		if (errorMessage == null && mapKeys.containsKey("birthday")) {
			LocalDate birthday = LocalDate.parse(mapKeys.get("birthday").toString());
			errorMessage = this.validationUserData.validateBirthdayData(birthday);
		}

		if (errorMessage == null && mapKeys.containsKey("phone")) {
			errorMessage = this.validationUserData.validatePhoneData(mapKeys.get("phone").toString());
		}

		return errorMessage;
	}
	
	@Override
	public String validateSemesterData(Map<String, Object> mapKeys) {
		String errorMessage = null;
		String semesterName = null;
		LocalDate beginDate = null;
		LocalDate endDate = null;
		
		if (mapKeys.containsKey("id")) {
			int id = Integer.parseInt(mapKeys.get("id").toString());
			errorMessage = this.validationSemesterData.validateIdData(id);
		}
		
		if (errorMessage == null) {
			try {
				semesterName = mapKeys.get("semesterName").toString();
				beginDate = LocalDate.parse(mapKeys.get("beginDate").toString());
				endDate = LocalDate.parse(mapKeys.get("endDate").toString());
				
				errorMessage = this.validationSemesterData.validateSemesterNameData(semesterName);
				int validYear = Integer.parseInt(semesterName.substring(0, 4));
				if (errorMessage == null) {
					System.out.println("================ valid Year = " + validYear);
					errorMessage = this.validationSemesterData.validateBeginDateData(beginDate, validYear);
				}

				if (errorMessage == null) {
					errorMessage = this.validationSemesterData.validateEndDateData(endDate, beginDate, validYear);
				}	
			} catch (DateTimeParseException e) {
				e.printStackTrace();
				return "Cannot parse Date info!";
			}
		}
		
		return errorMessage;
	}
	
	@Override
	public String validateRoomData(Map<String, Object> mapKeys) {
		String errorMessage = null;
		
		if (mapKeys.containsKey("id")) {
			int id = Integer.parseInt(mapKeys.get("id").toString());
			errorMessage = this.validationRoomData.validateIdData(id);
		}
		
		if (errorMessage == null && mapKeys.containsKey("roomName")) {
			errorMessage = this.validationRoomData.validateRoomNameData(mapKeys.get("roomName").toString());
		}
		
		if (errorMessage == null && mapKeys.containsKey("address")) {
			errorMessage = this.validationRoomData.validateAddressData(mapKeys.get("address").toString());
		}
		
		/*
		 * if (errorMessage == null && mapKeys.containsKey("macAddress")) { errorMessage
		 * = this.validationRoomData.validateMacAddressData(mapKeys.get("macAddress").
		 * toString()); }
		 */
		
		if (errorMessage == null && mapKeys.containsKey("gpsLa")) {
			double gpsLa = Double.parseDouble(mapKeys.get("gpsLa").toString());
			errorMessage = this.validationRoomData.validateGpsLatitude(gpsLa);
		}
		
		if (errorMessage == null && mapKeys.containsKey("gpsLong")) {
			double gpsLong = Double.parseDouble(mapKeys.get("gpsLong").toString());
			errorMessage = this.validationRoomData.validateGPSLongitudeData(gpsLong);
		}
		
		return errorMessage;
	}
	
	@Override
	public String validateCourseData(Map<String, Object> mapKeys) {
		String errorMessage = null;

		if (mapKeys.containsKey("id")) {
			int id = Integer.parseInt(mapKeys.get("id").toString());
			errorMessage = this.validationCourseData.validateIdData(id);
		}

		if (errorMessage == null && mapKeys.containsKey("courseName")) {
			errorMessage = this.validationCourseData.validateCourseNameData(mapKeys.get("courseName").toString());
		}
		
		return errorMessage;
	}

	@Override
	public String validateClassData(Map<String, Object> mapKeys) {
		String errorMessage = null;
		int tmpNumber = -1;
		
		if (mapKeys.containsKey("ID")) {
			tmpNumber = Integer.parseInt(mapKeys.get("ID").toString());
			errorMessage = this.validationClassData.validateIdData(tmpNumber);
		}
		
		if (errorMessage == null && mapKeys.containsKey("CourseID")) {
			tmpNumber = Integer.parseInt(mapKeys.get("CourseID").toString());
			errorMessage = this.validationClassData.validateIdData(tmpNumber);
		}
		
		if (errorMessage == null && mapKeys.containsKey("SemesterID")) {
			tmpNumber = Integer.parseInt(mapKeys.get("SemesterID").toString());
			errorMessage = this.validationClassData.validateIdData(tmpNumber);
		}
		
		if (errorMessage == null && mapKeys.containsKey("ClassName")) {
			errorMessage = this.validationClassData.validateClassNameData(mapKeys.get("ClassName").toString());
		}
		
		if (errorMessage == null && mapKeys.containsKey("MaxStudent")) {
			tmpNumber = Integer.parseInt(mapKeys.get("MaxStudent").toString());
			errorMessage = this.validationClassData.validateMaxStudent(tmpNumber);
		}
		
		if (errorMessage == null && mapKeys.containsKey("NumberOfLessons")) {
			tmpNumber = Integer.parseInt(mapKeys.get("NumberOfLessons").toString());
			errorMessage = this.validationClassData.validateMaxStudent(tmpNumber);
		}
		
		return errorMessage;
	}

	@Override
	public String validateClassRoomData(Map<String, Object> mapKeys) {
		String errorMessage = null;
		int tmpNumber = -1;
		
		if (mapKeys.containsKey("id")) {
			tmpNumber = Integer.parseInt(mapKeys.get("id").toString());
			errorMessage = this.validationClassRoomData.validateIdData(tmpNumber);
		}
		
		if (errorMessage == null && mapKeys.containsKey("classID")) {
			tmpNumber = Integer.parseInt(mapKeys.get("classID").toString());
			errorMessage = this.validationClassRoomData.validateIdData(tmpNumber);
		}
		
		if (errorMessage == null && mapKeys.containsKey("roomID")) {
			tmpNumber = Integer.parseInt(mapKeys.get("roomID").toString());
			errorMessage = this.validationClassRoomData.validateIdData(tmpNumber);
		}
		
		if (errorMessage == null && mapKeys.containsKey("weekday")) {
			tmpNumber = Integer.parseInt(mapKeys.get("weekday").toString());
			errorMessage = this.validationClassRoomData.validateWeekday(tmpNumber);
		}

		if (errorMessage == null && mapKeys.containsKey("beginAt") && mapKeys.containsKey("finishAt")) {
			LocalTime beginAt = LocalTime.parse(mapKeys.get("beginAt").toString());
			errorMessage = this.validationClassRoomData.validateBeginDate(beginAt);
			
			if (errorMessage == null) {
				LocalTime finishAt = LocalTime.parse(mapKeys.get("finishAt").toString());
				errorMessage = this.validationClassRoomData.validateFinishDate(finishAt, beginAt);
			}
		}
		
		return errorMessage;
	}

	@Override
	public boolean validateTeacherClassData(int teacherID, int classID, int isTeaching) {
		if (teacherID < 1 || classID < 1) {
			return false;
		}
		boolean validFlag = false;
		for (IsTeaching value : IsTeaching.values()) {
			if (isTeaching == value.getValue()) {
				validFlag = true;
				break;
			}
		}

		if (validFlag == true) {
			return true;
		}
		return false;
	}

	@Override
	public boolean validateStudentClassData(int studentID, int classID, int isLearning) {
		if (studentID < 1 || classID < 1) {
			return false;
		}

		boolean validFlag = false;
		for (IsLearning value : IsLearning.values()) {
			if (isLearning == value.getValue()) {
				validFlag = true;
				break;
			}
		}

		if (validFlag == true) {
			return true;
		}

		return false;
	}
}
