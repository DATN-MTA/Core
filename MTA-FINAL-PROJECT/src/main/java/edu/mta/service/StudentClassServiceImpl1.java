package edu.mta.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import edu.mta.enumData.IsLearning;
import edu.mta.enumData.SpecialRollCall;
import edu.mta.model.Class;
import edu.mta.repository.StudentClassRepository;
import edu.mta.utils.FrequentlyUtils;
import edu.mta.utils.GeneralValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import edu.mta.model.Account;
import edu.mta.model.ClassRoom;
import edu.mta.model.ReportError;
import edu.mta.model.StudentClass;
import edu.mta.repository.ClassRoomRepository;
import edu.mta.utils.ValidationAccountData;

@Service
@Qualifier("StudentClassServiceImpl1")
public class StudentClassServiceImpl1 implements StudentClassService {

	private ClassRoomService classRoomService;
	private FrequentlyUtils frequentlyUtils;
	private ClassService classService;
	private AccountService accountService;
	private BlacklistService blacklistService;
	private ClassRoomRepository classRoomRepository;
	private StudentClassRepository studentClassRepository;
	private ValidationAccountData validationAccountData;

	public StudentClassServiceImpl1() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Autowired
	public StudentClassServiceImpl1(@Qualifier("ClassRoomServiceImpl1") ClassRoomService classRoomService,
			@Qualifier("BlacklistServiceImpl1") BlacklistService blacklistService,
			@Qualifier("AccountServiceImpl1") AccountService accountService, ClassRoomRepository classRoomRepository,
			StudentClassRepository studentClassRepository,
			@Qualifier("ValidationAccountDataImpl1") ValidationAccountData validationAccountData,
			@Qualifier("ClassServiceImpl1") ClassService classService,
			@Qualifier("FrequentlyUtilsImpl1") FrequentlyUtils frequentlyUtils) {
		super();
		this.classRoomRepository = classRoomRepository;
		this.studentClassRepository = studentClassRepository;
		this.classRoomService = classRoomService;
		this.blacklistService = blacklistService;
		this.validationAccountData = validationAccountData;
		this.accountService = accountService;
		this.classService = classService;
		this.frequentlyUtils = frequentlyUtils;
	}

	@Override
	public List<ClassRoom> getTimeTable(int studentID, int semesterID) {
		List<Integer> listClassID = this.studentClassRepository.getListClass(studentID, semesterID);

		if (listClassID == null || listClassID.isEmpty()) {
			return null;
		}

		List<ClassRoom> listClassRoom = this.classRoomRepository.getListClassRoom(listClassID);
		if (listClassRoom == null || listClassRoom.isEmpty()) {
			return null;
		}
		return listClassRoom;
	}

	@Override
	public String checkStudentHasAuthority(int studentID, int classID, int roomID, String identifyString, String imei) {
		Optional<StudentClass> studentClass = this.studentClassRepository.findByStudentIDAndClassIDAndStatus(studentID,
				classID, IsLearning.LEARNING.getValue());

		if (studentClass.isEmpty()) {
			System.out.println("\n\nMile 1");
			return "Mile1";
		}

		StudentClass instance = studentClass.get();
		String classIdentifyString = instance.getClassInstance().getIdentifyString();
		// String studentImei = instance.getAccount().getImei();
		if (classIdentifyString == null) {
			return "You have to wait until teacher rollcall";
		}
		// check if identifyString is incorrect
		if (!classIdentifyString.equals(identifyString)) {
			System.out.println("\n\nMile 2");
			return "You have to scan the right QR code to rollcall";
		}

		// check if ClassRoom exists
		//// Notice: weekday of java = weekday of mySQL - 1
		int weekday = LocalDate.now().getDayOfWeek().getValue() + 1;
		LocalTime checkTime = LocalTime.now();
		ClassRoom classRoom = this.classRoomService.getInfoClassRoom(classID, roomID, weekday, checkTime);
		if (classRoom == null) {
			System.out.println("\n\nMile 3");
			return "Class is not in lesson's time!";
		}

		// check if student has already roll call
		// dateAndTime co dinh dang: Year - dayInYear - secondInDay
		String isChecked = instance.getIsChecked();

		if (!checkIsCheckedValid(isChecked, classRoom.getBeginAt(), classRoom.getFinishAt())) {
			System.out.println("\n\nMile 4");
			return "You have already rollcalled before";
		}

		return null;
	}

	@Override
	public String rollCall(int classID, int studentID, LocalDateTime rollCallAt, String imei) {
		Optional<StudentClass> studentClass = this.studentClassRepository.findByStudentIDAndClassIDAndStatus(studentID,
				classID, IsLearning.LEARNING.getValue());
		StudentClass instance = null;
		String newValue = null;
		String listRollCall = null;
		String isChecked = null;

		if (studentClass.isEmpty()) {
			return "Not found student-class";
		}

		instance = studentClass.get();
		listRollCall = instance.getListRollCall();
		// create a blacklist's record if imei is different
		if (!instance.getAccount().getImei().equals(imei)) {
			// this.blacklistService.createNewRecord(instance, imei);
			// return "Warning: System has created a record in blacklist for your incorrect
			// IMEI!";

			return "Sorry! It seem like you are using other's device!";
		}

		newValue = this.frequentlyUtils.makeRollcallRecord(rollCallAt) + GeneralValue.regexForSplitListRollCall;

		if (listRollCall == null) {
			listRollCall = newValue;
		} else {
			listRollCall += newValue;
		}

		instance.setListRollCall(listRollCall);

		isChecked = newValue;
		instance.setIsChecked(isChecked);
		this.studentClassRepository.save(instance);

		return null;

	}

	@Override
	public List<StudentClass> findByClassID(int id) {
		List<StudentClass> listInstance = this.studentClassRepository.getListStudentClass(id);
		if (listInstance == null || listInstance.isEmpty()) {
			return null;
		}
		return listInstance;
	}

	@Override
	public boolean updateStudentClassInfo(StudentClass studentClass) {
		this.studentClassRepository.save(studentClass);
		return true;
	}

	@Override
	public List<StudentClass> findCurrentStudentsByClassID(int classID) {
		List<StudentClass> listInstance = this.studentClassRepository.getListCurrentStudentClass(classID,
				IsLearning.LEARNING.getValue());
		if (listInstance == null || listInstance.isEmpty()) {
			return null;
		}
		return listInstance;
	}

	@Override
	public boolean checkStudentIsLearning(int studentID, int classID) {
		Optional<StudentClass> studentClass = this.studentClassRepository.findByStudentIDAndClassIDAndStatus(studentID,
				classID, IsLearning.LEARNING.getValue());
		if (studentClass.isEmpty()) {
			return false;
		}

		return true;
	}

	@Override
	public boolean checkIsCheckedValid(String isChecked, LocalTime beginAt, LocalTime finishAt) {
		if (isChecked != null) {
			String[] dateAndTime = isChecked.split(GeneralValue.regexForSplitDate);
			int year = Integer.parseInt(dateAndTime[0]);
			int dayOfYear = Integer.parseInt(dateAndTime[1]);
			int secondOfDay = Integer.parseInt(dateAndTime[2]);

			LocalDate checkedDate = LocalDate.ofYearDay(year, dayOfYear);
			LocalTime checkedTime = LocalTime.ofSecondOfDay(secondOfDay);

			// check if a day has more than one lesson of a class
			if (LocalDate.now().isEqual(checkedDate) && checkedTime.isAfter(beginAt)
					&& checkedTime.isBefore(finishAt)) {
				return false;
			}

			return true;
		}

		return true;
	}

	@Override
	public boolean checkStudentIsLearning(String studentEmail, int classID) {
		Optional<StudentClass> studentClass = this.studentClassRepository
				.findByStudentEmailAndClassIDAndStatus(studentEmail, classID, IsLearning.LEARNING.getValue());
		if (studentClass.isEmpty()) {
			return false;
		}

		return true;
	}

	@Override
	public String checkTimetableConflict(Account account, int classID) {
		List<StudentClass> listClasses = this.studentClassRepository.findByStudentIDAndStatus(account.getId(),
				IsLearning.LEARNING.getValue());
		if (listClasses == null || listClasses.isEmpty()) {
			// no conflict can happen
			return null;
		}

		// check if student has already studied in this class
		for (StudentClass target : listClasses) {
			if (target.getClassInstance().getId() == classID) {
				return "Student has already studied in this class";
			}
		}

		List<ClassRoom> listClassRoom = this.classRoomRepository.findByClassID(classID);
		if (listClassRoom == null || listClassRoom.isEmpty()) {
			// this class has no lesson => no conflict
			// this situation is for special class
			return null;
		}

		// a week has 5 weekdays from Monday to Friday; int[0] and int[1] are not used
		// all weekdays have lessons are marked as 1
		int[] arrayOfWeekdayLearning = { -1, -1, 0, 0, 0, 0, 0 };
		int tmpValue = -1;
		for (ClassRoom tmpClassRoom : listClassRoom) {
			tmpValue = tmpClassRoom.getWeekday();
			if (arrayOfWeekdayLearning[tmpValue] == 0) {
				arrayOfWeekdayLearning[tmpValue] = 1;
			}
		}

		List<ClassRoom> listClassRoomNeedCheck = null;
		int tmpClassID = -1;
		int tmpWeekday = -1;
		for (StudentClass tmpStudentClass : listClasses) {
			tmpClassID = tmpStudentClass.getClassInstance().getId();
			listClassRoomNeedCheck = this.classRoomRepository.findByClassID(tmpClassID);

			if (listClassRoomNeedCheck == null || listClassRoomNeedCheck.isEmpty()) {
				continue;
			}

			for (ClassRoom tmpClassRoom : listClassRoomNeedCheck) {
				tmpWeekday = tmpClassRoom.getWeekday();
				if (arrayOfWeekdayLearning[tmpValue] == 0) {
					// if not be overlapped day => no need to check
					continue;
				}

				for (ClassRoom target : listClassRoom) {
					if (target.getWeekday() != tmpWeekday) {
						continue;
					}

					// when 2 classroom is learned in the same day
					if (!this.frequentlyUtils.checkTwoTimeConflict(target.getBeginAt(), tmpClassRoom.getBeginAt(),
							target.getFinishAt(), tmpClassRoom.getFinishAt())) {
						return "Conflict happened";
					}
				}
			}
		}

		return null;
	}

	@Override
	public void saveNewStudentClass(StudentClass studentClass) {
		this.studentClassRepository.save(studentClass);
		return;

	}

	@Override
	public int getClassIDInLastElement(List<String> listStudentEmail) {
		try {
			int indexOfLastElement = listStudentEmail.size() - 1;
			return Integer.parseInt(listStudentEmail.get(indexOfLastElement));
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	@Override
	public List<String> filterListEmail(List<String> listStudentEmail, int classID) {
		Iterator<String> listIte = listStudentEmail.iterator();
		String tmpEmail = null;
		String errorMessage = null;
		String listOfInvalidRows = "";
		Account account = null;

		// the 1st row of file excel is header => list email begin from 2nd row
		int rowCounter = 1;
		while (listIte.hasNext()) {
			tmpEmail = listIte.next();
			rowCounter++;
			System.out.println("========== Tmp email = " + tmpEmail);

			errorMessage = this.validationAccountData.validateEmailData(tmpEmail);
			if (errorMessage != null) {
				System.out.println("============= email is invalid = " + errorMessage);
				listOfInvalidRows += rowCounter + ", ";
				listIte.remove();
				continue;
			}

			account = this.accountService.findAccountByEmail(tmpEmail);
			if (account == null || this.checkStudentIsLearning(account.getId(), classID)) {
				System.out.println("Account not exist or this student is learning");
				listOfInvalidRows += rowCounter + ", ";
				listIte.remove();
				continue;
			}

			errorMessage = this.checkTimetableConflict(account, classID);
			if (errorMessage != null) {
				System.out.println("=========== Timetable conflict");
				listOfInvalidRows += rowCounter + ", ";
				listIte.remove();
				continue;
			}
		}
		
		if (listStudentEmail == null || listStudentEmail.isEmpty()) {
			return null;
		}

		listStudentEmail.add(listOfInvalidRows);
		return listStudentEmail;
	}

	@Override
	public String addNewStudentClass(String studentEmail, int classID) {

		String errorMessage = null;
		errorMessage = this.validationAccountData.validateEmailData(studentEmail);
		if (errorMessage != null) {
			return errorMessage;
		}

		Account account = this.accountService.findAccountByEmail(studentEmail);
		if (account == null) {
			return "This account is not existed";
		}

		Class classInstance = this.classService.findClassByID(classID);
		if (classInstance == null) {
			return "This class is not existed";
		}

		if (this.checkStudentIsLearning(account.getId(), classID)) {
			return "Student has already assigned this class";
		}

		errorMessage = this.checkTimetableConflict(account, classID);
		if (errorMessage != null) {
			return "This class is conflict with other classes!";
		}

		StudentClass studentClass = new StudentClass();
		studentClass.setAccount(account);
		studentClass.setClassInstance(classInstance);
		studentClass.setIsLearning(IsLearning.LEARNING.getValue());
		this.studentClassRepository.save(studentClass);

		return null;
	}

	@Override
	public List<ReportError> checkListRollcallEmail(List<ReportError> listStudentRollcall, int classID) {
		Iterator<ReportError> listIte = listStudentRollcall.iterator();
		ReportError tmpReport = null;
		String errorMessage = null;
		String listOfInvalidRows = "";
		Account account = null;
		int tmpReason = -1;
		boolean isReasonValid;

		// the 1st row of file excel is header => list email begin from 2nd row
		int rowCounter = 1;
		while (listIte.hasNext()) {
			tmpReport = listIte.next();
			rowCounter++;
			System.out.println("========== Tmp email = " + tmpReport);

			errorMessage = this.validationAccountData.validateEmailData(tmpReport.getDescription());
			if (errorMessage != null) {
				System.out.println("============= email is invalid = " + errorMessage);
				listOfInvalidRows += rowCounter + ", ";
				listIte.remove();
				continue;
			}

			// check if reason is in list special rollcall
			tmpReason = tmpReport.getErrorCode();
			isReasonValid = false;
			for (SpecialRollCall value : SpecialRollCall.values()) {
				if (tmpReason == value.getValue()) {
					isReasonValid = true;
					break;
				}
			}

			if (isReasonValid == false) {
				System.out.println("============= reason is invalid = " + tmpReason);
				listOfInvalidRows += rowCounter + ", ";
				listIte.remove();
				continue;
			}

			account = this.accountService.findAccountByEmail(tmpReport.getDescription());
			if (account == null || !this.checkStudentIsLearning(account.getId(), classID)) {
				System.out.println("Account not exist or this student is not learning the class");
				listOfInvalidRows += rowCounter + ", ";
				listIte.remove();
				continue;
			}
		}

		if (listStudentRollcall == null || listStudentRollcall.isEmpty()) {
			return null;
		}

		// store list of invalid rows in the last element => controller retrieve
		listStudentRollcall.add(new ReportError(-1, listOfInvalidRows));
		return listStudentRollcall;
	}

	@Override
	public String rollcallByEmailAndClassID(ReportError studentRollcall, int classID, int roomID) {
		String newValue = null;
		String listRollCall = null;
		String isChecked = null;
		int tmpReason = studentRollcall.getErrorCode();

		Optional<StudentClass> studentClass = this.studentClassRepository.findByStudentEmailAndClassIDAndStatus(
				studentRollcall.getDescription(), classID, IsLearning.LEARNING.getValue());
		if (studentClass.isEmpty()) {
			return "Not found student-class";
		}

		// Check teacher generate time in valid limit
		// Notice: weekday of java = weekday of mySQL - 1
		LocalDateTime rollcallAt = LocalDateTime.now();
		int weekday = rollcallAt.toLocalDate().getDayOfWeek().getValue() + 1;
		LocalTime checkTime = rollcallAt.toLocalTime();
		Optional<ClassRoom> classRoomOpt = this.classRoomRepository.findByClassIDAndRoomIDAndWeekday(classID, roomID,
				weekday, checkTime);
		if (classRoomOpt.isEmpty()) {
			return "Not in lesson's duration!";
		}

		StudentClass instance = studentClass.get();
		listRollCall = instance.getListRollCall();

		newValue = this.frequentlyUtils.makeRollcallRecord(rollcallAt);
		isChecked = newValue;

		// use if-else if because in future may have other reason
		if (tmpReason == SpecialRollCall.SICK.getValue()) {
			newValue += GeneralValue.markForPermission;
		} else if (tmpReason == SpecialRollCall.FORGOT_PHONE.getValue()) {
			newValue += GeneralValue.markForNotBringPhone;
		}

		newValue += GeneralValue.regexForSplitListRollCall;

		if (listRollCall == null) {
			listRollCall = newValue;
		} else {
			listRollCall += newValue;
		}

		instance.setListRollCall(listRollCall);

		instance.setIsChecked(isChecked);
		this.studentClassRepository.save(instance);

		return null;

	}

	@Override
	public List<StudentClass> findStudentByClassId(int classId) {
		return studentClassRepository.findByClassIDAndStatus(classId, 1);
	}

}
