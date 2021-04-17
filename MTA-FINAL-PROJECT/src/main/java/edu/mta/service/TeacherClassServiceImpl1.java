package edu.mta.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import edu.mta.enumData.IsLearning;
import edu.mta.enumData.IsTeaching;
import edu.mta.enumData.SpecialRollCall;
import edu.mta.model.Class;
import edu.mta.repository.StudentClassRepository;
import edu.mta.repository.TeacherClassRepository;
import edu.mta.utils.FrequentlyUtils;
import edu.mta.utils.GeneralValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import edu.mta.model.Account;
import edu.mta.model.ClassRoom;
import edu.mta.model.StudentClass;
import edu.mta.model.TeacherClass;
import edu.mta.repository.ClassRepository;
import edu.mta.repository.ClassRoomRepository;
import edu.mta.utils.ValidationAccountData;

@Service
@Transactional
@Qualifier("TeacherClassServiceImpl1")
public class TeacherClassServiceImpl1 implements TeacherClassService {

	private ClassRoomService classRoomService;
	private AccountService accountService;
	private StudentClassService studentClassService;
	private ClassRepository classRepository;
	private ClassRoomRepository classRoomRepository;
	private TeacherClassRepository teacherClassRepository;
	private StudentClassRepository studentClassRepository;
	private ValidationAccountData validationAccountData;
	private ClassService classService;
	private FrequentlyUtils frequentlyUtils;

	public TeacherClassServiceImpl1() {
		super();
	}

	@Autowired
	public TeacherClassServiceImpl1(@Qualifier("ClassRoomServiceImpl1") ClassRoomService classRoomService,
			@Qualifier("AccountServiceImpl1") AccountService accountService,
			@Qualifier("StudentClassServiceImpl1") StudentClassService studentClassService,
			ClassRoomRepository classRoomRepository, TeacherClassRepository teacherClassRepository,
			ClassRepository classRepository, StudentClassRepository studentClassRepository,
			@Qualifier("ClassServiceImpl1") ClassService classService,
			@Qualifier("ValidationAccountDataImpl1") ValidationAccountData validationAccountData,
			@Qualifier("FrequentlyUtilsImpl1") FrequentlyUtils frequentlyUtils) {
		super();
		this.classRoomRepository = classRoomRepository;
		this.teacherClassRepository = teacherClassRepository;
		this.classRoomService = classRoomService;
		this.studentClassService = studentClassService;
		this.classRepository = classRepository;
		this.studentClassRepository = studentClassRepository;
		this.validationAccountData = validationAccountData;
		this.accountService = accountService;
		this.classService = classService;
		this.frequentlyUtils = frequentlyUtils;
	}

	@Override
	public List<ClassRoom> getTimeTable(int teacherID, int semesterID) {
		List<Integer> listClassID = this.teacherClassRepository.getListClass(teacherID, semesterID);
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
	public boolean checkTeacherHasAuthority(int teacherID, int classID) {
		Optional<TeacherClass> teacherClass = this.teacherClassRepository.findByTeacherIDAndClassIDAndStatus(teacherID,
				classID, IsTeaching.TEACHING.getValue());

		if (teacherClass.isEmpty()) {
			return false;
		}

		return true;
	}

	@Override
	public boolean checkGenerateTimeValid(int weekday, LocalTime generateTime, int classID, int roomID) {
		LocalTime beginTime = null;
		LocalTime finishTime = null;
		String isChecked = null;
		ClassRoom classRoom = null;

		// check if this classroom exists
		classRoom = this.classRoomService.getInfoClassRoom(classID, roomID, weekday, generateTime);
		if (classRoom == null) {
			return false;
		}

		beginTime = classRoom.getBeginAt();
		finishTime = classRoom.getFinishAt();
		isChecked = classRoom.getClassInstance().getIsChecked();

		// 1 lesson has only one roll call request
		// check if this class has been request to roll call
		if (isChecked != null) {
			String[] dateAndTime = isChecked.split("-");
			int year = Integer.parseInt(dateAndTime[0]);
			int dayOfYear = Integer.parseInt(dateAndTime[1]);
			int secondOfDay = Integer.parseInt(dateAndTime[2]);

			LocalDate checkedDate = LocalDate.ofYearDay(year, dayOfYear);
			LocalTime checkedTime = LocalTime.ofSecondOfDay(secondOfDay);

			// check if a day has more than one lesson of a class
			if (LocalDate.now().isEqual(checkedDate) && checkedTime.isAfter(classRoom.getBeginAt())
					&& checkedTime.isBefore(classRoom.getFinishAt())) {
				return false;
			}
		}

		// check if generateTime is in limit
		// 10 minutes is time limit to roll call; offset 5 minutes for insurance
		finishTime = finishTime.minusMinutes(15);
		if (generateTime.isBefore(beginTime) || generateTime.isAfter(finishTime)) {
			return false;
		}
		return true;
	}

	@Override
	public String generateIdentifyString(int classID, int roomID, int weekday, String inputMd5) {
		LocalTime checkTime = LocalTime.now();
		ClassRoom classRoom = this.classRoomService.getInfoClassRoom(classID, roomID, weekday, checkTime);
		Class classInstance = null;
		MessageDigest md = null;
		byte[] messageDigest;
		String identifyString = null;
		String eventDynamicName = null;
		// String timeString = null;

		// check if this classroom exists
		if (classRoom == null) {
			return null;
		}

		// check if class's identifyString has already been set or this class has been
		// roll called once in this lesson
		classInstance = this.classRepository.findById(classRoom.getClassInstance().getId()).get();
		if (classInstance.getIdentifyString() != null) {
			return null;
		}

		try {
			// generate md5 string (just take the first 10 characters)
			md = MessageDigest.getInstance("MD5");
			messageDigest = md.digest(inputMd5.getBytes());
			identifyString = messageDigest.toString().substring(0, 10);
			classInstance.setIdentifyString(identifyString);

			String isChecked = null;
			LocalDateTime rollCallAt = LocalDateTime.now();
			isChecked = rollCallAt.getYear() + "-" + rollCallAt.getDayOfYear() + "-"
					+ rollCallAt.toLocalTime().toSecondOfDay();
			classInstance.setIsChecked(isChecked);
			this.classRepository.save(classInstance);

			// create new event to set identifyString = null after 10 minutes
			// user Class.numberOfEvents to avoid conflict
			Class.numberOfEvents += 1;
			eventDynamicName = "generateIdentifyString" + Class.numberOfEvents;
			this.classRepository.setNullIdentifyString(classID, eventDynamicName, classRoom.getId());

			// this.classRepository.setIsCheckFalse(classID, timeString, eventDynamicName);
			return identifyString;

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean rollCall(LocalDateTime rollCallAt, int teacherID, int classID) {
		Optional<TeacherClass> teacherClass = this.teacherClassRepository.findByTeacherIDAndClassIDAndStatus(teacherID,
				classID, IsTeaching.TEACHING.getValue());
		TeacherClass instance = null;
		Class classInstance = null;
		String newValue = null;
		String listRollCall = null;

		if (teacherClass.isEmpty()) {
			return false;
		}

		instance = teacherClass.get();
		classInstance = instance.getClassInstance();
		listRollCall = instance.getListRollCall();

		newValue = createRollcallDate(rollCallAt);

		if (listRollCall == null) {
			listRollCall = newValue;
		} else {
			listRollCall += newValue;
		}

		instance.setListRollCall(listRollCall);
		this.teacherClassRepository.save(instance);

		classInstance.setCurrentLesson(classInstance.getCurrentLesson() + 1);
		this.classRepository.save(classInstance);

		return true;

	}

	@Override
	public List<TeacherClass> findByClassID(int id) {
		List<TeacherClass> listInstance = this.teacherClassRepository.getListTeacherClass(id);
		if (listInstance == null || listInstance.isEmpty()) {
			return null;
		}
		return listInstance;
	}

	@Override
	public TeacherClass findCurrentTeacherByClassID(int classID) {
		Optional<TeacherClass> teacherClass = this.teacherClassRepository.findByClassIDAndStatus(classID,
				IsTeaching.TEACHING.getValue());
		if (teacherClass.isEmpty()) {
			return null;
		}
		return teacherClass.get();
	}

	@Override
	public boolean updateTeacherClass(TeacherClass teacherClass) {
		this.teacherClassRepository.save(teacherClass);
		return false;
	}

	@Override
	public boolean rollCallStudentWithPermission(String studentEmail, int classID, int roomID, int reason) {
		int weekday = LocalDate.now().getDayOfWeek().getValue() + 1;
		LocalDateTime rollCallAt = LocalDateTime.now();
		StudentClass studentClassInstance = null;
		String newValue = null;
		String listRollCall = null;
		String isChecked = null;
		Optional<ClassRoom> classRoom = this.classRoomRepository.findByClassIDAndRoomIDAndWeekday(classID, roomID,
				weekday, rollCallAt.toLocalTime());
		Optional<StudentClass> studentClass = this.studentClassRepository
				.findByStudentEmailAndClassIDAndStatus(studentEmail, classID, IsLearning.LEARNING.getValue());

		if (classRoom.isEmpty() || studentClass.isEmpty()) {
			return false;
		}

		ClassRoom classRoomInstance = classRoom.get();
		studentClassInstance = studentClass.get();
		String classIsChecked = studentClassInstance.getClassInstance().getIsChecked();

		// checkedTime empty => this class is still not rollcall => not allow teacher
		if (classIsChecked == null || classIsChecked.isBlank()) {
			return false;
		}

		// check if teacher has triggered rollcall process before
		// => check isChecked of class is in this lessons's duration
		String[] partsOfTime = classIsChecked.split(GeneralValue.regexForSplitDate);
		int year = Integer.parseInt(partsOfTime[0]);
		int dayOfYear = Integer.parseInt(partsOfTime[1]);
		int secondOfDay = Integer.parseInt(partsOfTime[2]);
		LocalDate checkedDay = LocalDate.ofYearDay(year, dayOfYear);
		if (!checkedDay.isEqual(LocalDate.now())) {
			return false;
		}
		LocalTime checkedTime = LocalTime.ofSecondOfDay(secondOfDay);
		if (checkedTime.isBefore(classRoomInstance.getBeginAt())
				|| checkedTime.isAfter(classRoomInstance.getFinishAt())) {
			return false;
		}

		if (studentClassInstance.getClassInstance().getIdentifyString() == null) {
			return false;
		}

		isChecked = studentClassInstance.getIsChecked();
		if (isChecked != null && !isChecked.isBlank() && !this.studentClassService.checkIsCheckedValid(isChecked,
				classRoom.get().getBeginAt(), classRoom.get().getFinishAt())) {
			System.out.println("\n\nMile 3");
			return false;
		}

		listRollCall = studentClassInstance.getListRollCall();

		newValue = "" + rollCallAt.getYear();
		newValue += GeneralValue.regexForSplitDate + rollCallAt.getDayOfYear();
		newValue += GeneralValue.regexForSplitDate + rollCallAt.toLocalTime().toSecondOfDay();

		if (reason == SpecialRollCall.SICK.getValue()) {
			newValue += GeneralValue.markForPermission + GeneralValue.regexForSplitListRollCall;

		} else if (reason == SpecialRollCall.FORGOT_PHONE.getValue()) {
			newValue += GeneralValue.markForNotBringPhone + GeneralValue.regexForSplitListRollCall;
		}

		if (listRollCall == null) {
			listRollCall = newValue;
		} else {
			listRollCall += newValue;
		}

		studentClassInstance.setListRollCall(listRollCall);
		studentClassInstance.setIsChecked(createRollcallDate(rollCallAt));
		this.studentClassRepository.save(studentClassInstance);
		return true;
	}

	@Override
	public boolean checkTimetableConflict(int teacherID, int classID) {
		List<ClassRoom> listClassRoom1 = this.classRoomRepository.findByClassID(classID);
		if (listClassRoom1 == null || listClassRoom1.isEmpty()) {
			// there is no class-room exists to be conflicted for this class =>
			// responsibilities come when adding class-room
			return true;
		}

		// from Monday to Friday => 5 days in max
		int[] arrayWeekdays = new int[5];
		boolean flag = false;
		int tmpWeekday = -1;
		for (ClassRoom classRoom : listClassRoom1) {
			tmpWeekday = classRoom.getWeekday();
			flag = false;
			for (int i = 0; i < arrayWeekdays.length; i++) {
				if (arrayWeekdays[i] == tmpWeekday) {
					flag = true;
					break;
				}
			}
			if (flag == false) {
				arrayWeekdays[arrayWeekdays.length] = tmpWeekday;
			}
		}

		List<Class> listClass = this.teacherClassRepository.findByTeacherIDAndStatus(teacherID,
				IsTeaching.TEACHING.getValue());
		if (listClass == null || listClass.isEmpty()) {
			// teacher has no class => no conflict
			return true;
		}

		List<ClassRoom> listClassRoom2 = null;
		List<ClassRoom> listClassRoom3 = new ArrayList<>();
		for (Class classInstance : listClass) {
			listClassRoom2 = this.classRoomRepository.findByClassID(classInstance.getId());
			if (listClassRoom2 != null && !listClassRoom2.isEmpty()) {
				listClassRoom3.addAll(listClassRoom2);
			}
		}

		// These lines of code is for insurance
		if (listClassRoom3.isEmpty()) {
			return true;
		}

		// remove all classroom that is not in new class's weekdays
		for (int i = 0; i < arrayWeekdays.length; i++) {
			for (Iterator<ClassRoom> iter = listClassRoom3.listIterator(); iter.hasNext();) {
				ClassRoom a = iter.next();
				if (a.getWeekday() != arrayWeekdays[i]) {
					iter.remove();
				}
			}
		}

		LocalTime beginAt = null;
		LocalTime finishAt = null;
		LocalTime tmpBeginAt = null;
		LocalTime tmpFinishAt = null;
		for (ClassRoom classRoom1 : listClassRoom1) {
			beginAt = classRoom1.getBeginAt();
			finishAt = classRoom1.getFinishAt();

			for (ClassRoom classRoom2 : listClassRoom3) {
				if (classRoom1.getWeekday() != classRoom2.getWeekday()) {
					continue;
				}

				tmpBeginAt = classRoom2.getBeginAt();
				tmpFinishAt = classRoom2.getFinishAt();

				// check if conflict between 2 durations may happen
				// 2 type of conflict: beginAt or finishAt violate duration of other class-room
				// or duration of this class-room is contains duration of other class-room
				if ((beginAt.isAfter(tmpBeginAt) && beginAt.isBefore(tmpFinishAt))
						|| (finishAt.isAfter(tmpBeginAt) && finishAt.isBefore(tmpFinishAt))
						|| (beginAt.isBefore(tmpBeginAt) && finishAt.isAfter(tmpFinishAt))) {

					return false;
				}
			}
		}

		return true;
	}

	@Override
	public void addNewTeacherClass(TeacherClass teacherClass) {
		this.teacherClassRepository.save(teacherClass);
		return;
	}

	@Override
	public String checkReasonValid(int reason) {
		boolean validFlag = false;
		for (SpecialRollCall value : SpecialRollCall.values()) {
			if (reason == value.getValue()) {
				validFlag = true;
				break;
			}
		}

		if (validFlag == false) {
			return "the reason is not allowed!";
		}
		return null;
	}

	@Override
	public String createRollcallDate(LocalDateTime rollCallAt) {
		String rollCallDate = "" + rollCallAt.getYear();
		rollCallDate += "-" + rollCallAt.getDayOfYear();
		rollCallDate += "-" + rollCallAt.toLocalTime().toSecondOfDay() + GeneralValue.regexForSplitListRollCall;
		return rollCallDate;
	}

	@Override
	public List<TeacherClass> findByCurrentTeacherID(int teacherID) {
		List<TeacherClass> listRecords = this.teacherClassRepository.findByCurrentTeacherID(teacherID,
				IsTeaching.TEACHING.getValue());
		if (listRecords == null || listRecords.isEmpty()) {
			return null;
		}
		return listRecords;
	}

	@Override
	public String addNewTeacherClass(String teacherEmail, int classID) {
		String errorMessage = null;
		errorMessage = this.validationAccountData.validateEmailData(teacherEmail);
		if (errorMessage != null) {
			return errorMessage;
		}

		Account account = this.accountService.findAccountByEmail(teacherEmail);
		if (account == null) {
			return "This account is not existed";
		}

		Class classInstance = this.classService.findClassByID(classID);
		if (classInstance == null) {
			return "This class is not existed";
		}

		if (this.checkTeacherIsTeaching(teacherEmail, classID)) {
			return "Teacher has already taught this class";
		}

		errorMessage = this.checkTimetableConflict(account, classID);
		if (errorMessage != null) {
			return "This class is conflict with other classes!";
		}

		TeacherClass teacherClass = new TeacherClass();
		teacherClass.setAccount(account);
		teacherClass.setClassInstance(classInstance);
		teacherClass.setIsTeaching(IsTeaching.TEACHING.getValue());
		this.teacherClassRepository.save(teacherClass);

		return null;
	}

	@Override
	public boolean checkTeacherIsTeaching(String teacherEmail, int classID) {
		System.out.println("=========== classID = " + classID);
		Optional<TeacherClass> teacherClassOpt = this.teacherClassRepository.findByClassIDAndStatus(classID,
				IsTeaching.TEACHING.getValue());

		if (teacherClassOpt.isEmpty()) {
			return false;
		}

		TeacherClass teacherClass = teacherClassOpt.get();
		if (teacherClass.getAccount().getEmail().equalsIgnoreCase(teacherEmail)) {
			return true;
		}
		return false;
	}

	@Override
	public String checkTimetableConflict(Account account, int classID) {
		List<TeacherClass> listClasses = this.teacherClassRepository.findByCurrentTeacherID(account.getId(),
				IsTeaching.TEACHING.getValue());
		if (listClasses == null || listClasses.isEmpty()) {
			// no conflict can happen
			return null;
		}

		// check if teacher is teaching class
		for (TeacherClass target : listClasses) {
			if (target.getClassInstance().getId() == classID) {
				return "Teacher has already taught this class!";
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
		for (TeacherClass tmpTeacherClass : listClasses) {
			tmpClassID = tmpTeacherClass.getClassInstance().getId();
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

}
