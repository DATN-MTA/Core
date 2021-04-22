package edu.mta.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mta.model.Class;
import edu.mta.model.*;
import edu.mta.service.*;
import edu.mta.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class ReportController {

	// private ReportService reportService;
	private AccountService accountService;
	private ClassService classService;
	private SemesterService semesterService;
	private ValidationAccountData validationAccountData;
	private ValidationClassData validationClassData;
	private ValidationSemesterData validationSemesterData;
	private FrequentlyUtils frequentlyUtils;
	private BaseService baseService;

	@Autowired
	private ReportServiceImpl1 reportServiceImpl1;

	public ReportController() {
		super();
	}

	@Autowired
	public ReportController(@Qualifier("ValidationAccountDataImpl1") ValidationAccountData validationAccountData,
			@Qualifier("FrequentlyUtilsImpl1") FrequentlyUtils frequentlyUtils,
			@Qualifier("SemesterServiceImpl1") SemesterService semesterService,
			@Qualifier("AccountServiceImpl1") AccountService accountService,
			@Qualifier("ValidationSemesterDataImpl1") ValidationSemesterData validationSemesterData,
			BaseService baseService,
			@Qualifier("ClassServiceImpl1") ClassService classService,
			@Qualifier("ValidationClassDataImpl1") ValidationClassData validationClassData) {
		super();
		// this.reportService = reportService;
		this.validationAccountData = validationAccountData;
		this.frequentlyUtils = frequentlyUtils;
		this.accountService = accountService;
		this.validationSemesterData = validationSemesterData;
		this.baseService = baseService;
		this.semesterService = semesterService;
		this.classService = classService;
		this.validationClassData = validationClassData;
	}

	@RequestMapping(value = "/teacherGeneralReport", method = RequestMethod.POST)
	@PreAuthorize("hasRoles('ROLE_ADMIN', 'ROLE_TEACHER')")
	public ResponseEntity<?> getTeacherGeneralReport(@RequestParam(value = "adminID", required = true) int adminID,
			@RequestBody String reportParams) {
		ReportOutput output = new ReportOutput();
		String errorMessage = this.validationAccountData.validateIdData(adminID);
		ReportError report = null;
		if (errorMessage != null) {
			report = new ReportError(110, "Get report failed because " + errorMessage);
			return ResponseEntity.badRequest().body(report);
		}

		Map<String, Object> jsonMap = null;
		ObjectMapper objectMapper = null;
		String fileName = null;

		try {
			objectMapper = new ObjectMapper();
			jsonMap = objectMapper.readValue(reportParams, new TypeReference<Map<String, Object>>() {
			});

			// check request body has enough info in right JSON format
			if (!this.frequentlyUtils.checkKeysExist(jsonMap, "email", "semesterID", "beginAt", "finishAt", "fileType")) {
				report = new ReportError(1, "You have to fill all required information!");
				return ResponseEntity.badRequest().body(report);
			}

			System.out.println("\n\nMile 1");
			Account tmpAccount = this.accountService.findAccountByID(adminID);
//			if (tmpAccount == null || (tmpAccount.getRole() != AccountRole.ADMIN.getValue()
//					&& tmpAccount.getRole() != AccountRole.TEACHER.getValue())) {
//				report = new ReportError(111, "Only admin and teacher has authority to use this API!");
//				return ResponseEntity.badRequest().body(report);
//			}

			System.out.println("\n\nMile 2");
			String teacherEmail = jsonMap.get("email").toString();
			errorMessage = this.validationAccountData.validateEmailData(teacherEmail);
			if (errorMessage != null) {
				report = new ReportError(110, "Get report failed because " + errorMessage);
				return ResponseEntity.badRequest().body(report);
			}

			System.out.println("\n\nMile 3");
			Account account = this.accountService.findAccountByEmail(teacherEmail);
			if (account == null || !account.getRoles().contains(Role.ROLE_TEACHER)) {
				report = new ReportError(112, "This email address is not valid!");
				return ResponseEntity.badRequest().body(report);
			}

			System.out.println("\n\nMile 4");
			//need_change System.out.println("\n\n user info = " + account.getUserInfo());
			//need_change String[] teacherInfo = account.getUserInfo().split(GeneralValue.regexForSplitUserInfo);
			//need_change System.out.println("\n\n full name = " + teacherInfo[0]);

			//need_change jsonMap.put("teacherName", teacherInfo[0]);

			System.out.println("\n\nMile 5");
			int semesterID = Integer.parseInt(jsonMap.get("semesterID").toString());
			errorMessage = this.validationSemesterData.validateIdData(semesterID);
			if (errorMessage != null) {
				report = new ReportError(110, "Get report failed because " + errorMessage);
				return ResponseEntity.badRequest().body(report);
			}
			
			Semester semester = this.semesterService.findSemesterById(semesterID);
			if (semester == null) {
				report = new ReportError(110, "Get report failed because no semester exists! ");
				return ResponseEntity.badRequest().body(report);
			}

			String fileType = jsonMap.get("fileType").toString();
			System.out.println("\n\n file type = " + fileType);
			if (!fileType.equalsIgnoreCase(GeneralValue.FILE_TYPE_XLSX) && !fileType.equalsIgnoreCase(GeneralValue.FILE_TYPE_XLS)
					&& !fileType.equalsIgnoreCase(GeneralValue.FILE_TYPE_PDF)) {
				report = new ReportError(110, "Get report failed because file type is inccorrect");
				return ResponseEntity.badRequest().body(report);
			}

			LocalDate beginAt = LocalDate.parse(jsonMap.get("beginAt").toString());
			LocalDate finishAt = LocalDate.parse(jsonMap.get("finishAt").toString());
			
			if (beginAt.isAfter(finishAt)) {
				report = new ReportError(110, "Begin date cannot is larger than Finish date!");
				return ResponseEntity.badRequest().body(report);
			}
			
			if (beginAt.isBefore(semester.getBeginDate()) || beginAt.isAfter(semester.getEndDate())) {
				beginAt = semester.getBeginDate();
			}
			if (finishAt.isBefore(semester.getBeginDate()) || finishAt.isAfter(semester.getEndDate())) {
				finishAt = semester.getEndDate();
			}

			fileName = GeneralValue.GENERAL_REPORT_FOR_TEACHER + "_" + account.getEmail() + "_"
					+ LocalDate.now();

			String symlinkInServer = this.baseService.getFolderSymLink(fileName);

			String reportPath = BirtRuner.runBirtReport(GeneralValue.GENERAL_REPORT_FOR_TEACHER_TEMPLATE, jsonMap,
					fileName, symlinkInServer);

			System.out.println("\n\n reportPath = " + reportPath);
			//String symbolicLink = BaseService.genSymLink(reportPath);
			String symbolicLink = null;

			if (symbolicLink == null || symbolicLink.isBlank()) {
				// logger.info("GEN SYMBOLICLINK ERROR");
				output.setErrorCode(GeneralValue.GEN_SYMBOLIC_FAIL);
				output.setDescription("Generate symbolic link failed!");
			}
//			} else {
//			// logger.info("link file : " + symbolicLink);
//				output.setLinkFile(symbolicLink);
//			}

		} catch (Exception e) {
			e.printStackTrace();
			report = new ReportError(2, "Error happened when jackson deserialization info!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, report.toString());
		}
		
		output.setDescription(fileName);
		return ResponseEntity.status(200).body(output);
	}
	
	@RequestMapping(value = "/studentGeneralReport", method = RequestMethod.POST)
	@PreAuthorize("hasRoles('ROLE_ADMIN', 'ROLE_TEACHER')")
	public ResponseEntity<?> getStudentGeneralReport(@RequestParam(value = "adminID", required = true) int adminID,
			@RequestBody String reportParams) {
		ReportOutput output = new ReportOutput();
		String errorMessage = this.validationAccountData.validateIdData(adminID);
		ReportError report = null;
		if (errorMessage != null) {
			report = new ReportError(110, "Get report failed because " + errorMessage);
			return ResponseEntity.badRequest().body(report);
		}

		Map<String, Object> jsonMap = null;
		ObjectMapper objectMapper = null;
		String fileName = null;

		try {
			objectMapper = new ObjectMapper();
			jsonMap = objectMapper.readValue(reportParams, new TypeReference<Map<String, Object>>() {
			});

			// check request body has enough info in right JSON format
			if (!this.frequentlyUtils.checkKeysExist(jsonMap, "email", "semesterID", "beginAt", "finishAt", "fileType")) {
				report = new ReportError(1, "You have to fill all required information!");
				return ResponseEntity.badRequest().body(report);
			}

			System.out.println("\n\nMile 1");
			Account tmpAccount = this.accountService.findAccountByID(adminID);
//			if (tmpAccount == null || (tmpAccount.getRole() != AccountRole.ADMIN.getValue()
//					&& tmpAccount.getRole() != AccountRole.TEACHER.getValue())) {
//				report = new ReportError(111, "Only admin and teacher has authority to use this API!");
//				return ResponseEntity.badRequest().body(report);
//			}

			System.out.println("\n\nMile 2");
			String studentEmail = jsonMap.get("email").toString();
			errorMessage = this.validationAccountData.validateEmailData(studentEmail);
			if (errorMessage != null) {
				report = new ReportError(110, "Get report failed because " + errorMessage);
				return ResponseEntity.badRequest().body(report);
			}

			System.out.println("\n\nMile 3");
			Account account = this.accountService.findAccountByEmail(studentEmail);
			if (account == null || !account.getRoles().contains(Role.ROLE_STUDENT)) {
				report = new ReportError(112, "This email address is not valid!");
				return ResponseEntity.badRequest().body(report);
			}

			System.out.println("\n\nMile 4");
			//need_changeSystem.out.println("\n\n user info = " + account.getUserInfo());
			//need_change String[] studentInfo = account.getUserInfo().split(GeneralValue.regexForSplitUserInfo);
			//need_change System.out.println("\n\n full name = " + studentInfo[0]);

			//need_change jsonMap.put("studentName", studentInfo[0]);

			System.out.println("\n\nMile 5");
			int semesterID = Integer.parseInt(jsonMap.get("semesterID").toString());
			errorMessage = this.validationSemesterData.validateIdData(semesterID);
			if (errorMessage != null) {
				report = new ReportError(110, "Get report failed because " + errorMessage);
				return ResponseEntity.badRequest().body(report);
			}
			
			Semester semester = this.semesterService.findSemesterById(semesterID);
			if (semester == null) {
				report = new ReportError(110, "Get report failed because no semester exists! ");
				return ResponseEntity.badRequest().body(report);
			}

			String fileType = jsonMap.get("fileType").toString();
			System.out.println("\n\n file type = " + fileType);
			if (!fileType.equalsIgnoreCase(GeneralValue.FILE_TYPE_XLSX) && !fileType.equalsIgnoreCase(GeneralValue.FILE_TYPE_XLS)
					&& !fileType.equalsIgnoreCase(GeneralValue.FILE_TYPE_PDF)) {
				report = new ReportError(110, "Get report failed because file type is inccorrect");
				return ResponseEntity.badRequest().body(report);
			}

			LocalDate beginAt = LocalDate.parse(jsonMap.get("beginAt").toString());
			LocalDate finishAt = LocalDate.parse(jsonMap.get("finishAt").toString());
			
			if (beginAt.isAfter(finishAt)) {
				report = new ReportError(110, "Begin date cannot is larger than Finish date!");
				return ResponseEntity.badRequest().body(report);
			}
			
			if (beginAt.isBefore(semester.getBeginDate()) || beginAt.isAfter(semester.getEndDate())) {
				beginAt = semester.getBeginDate();
			}
			if (finishAt.isBefore(semester.getBeginDate()) || finishAt.isAfter(semester.getEndDate())) {
				finishAt = semester.getEndDate();
			}

			fileName = GeneralValue.GENERAL_REPORT_FOR_STUDENT + "_" + account.getEmail() + "_"
					+ LocalDate.now();

			String symlinkInServer = this.baseService.getFolderSymLink(fileName);

			String reportPath = BirtRuner.runBirtReport(GeneralValue.GENERAL_REPORT_FOR_STUDENT_TEMPLATE, jsonMap,
					fileName, symlinkInServer);

			System.out.println("\n\n reportPath = " + reportPath);
			String symbolicLink = BaseService.genSymLink(reportPath);

			if (symbolicLink == null || symbolicLink.isBlank()) {
				// logger.info("GEN SYMBOLICLINK ERROR");
				output.setErrorCode(GeneralValue.GEN_SYMBOLIC_FAIL);
				output.setDescription("Generate symbolic link failed!");
			} else {
			// logger.info("link file : " + symbolicLink);
				output.setLinkFile(symbolicLink);
			}

		} catch (Exception e) {
			e.printStackTrace();
			report = new ReportError(2, "Error happened when jackson deserialization info!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, report.toString());
		}
		
		output.setDescription(fileName);
		return ResponseEntity.status(200).body(output);
	}
	
	@RequestMapping(value = "/classDetailReport", method = RequestMethod.POST)
	@PreAuthorize("hasRoles('ROLE_ADMIN', 'ROLE_TEACHER')")
	public ResponseEntity<?> getClassDetailReport(@RequestParam(value = "adminID", required = true) int adminID,
			@RequestBody String reportParams) {
		
		ReportOutput output = new ReportOutput();
		String errorMessage = this.validationAccountData.validateIdData(adminID);
		ReportError report = null;
		if (errorMessage != null) {
			report = new ReportError(110, "Get report failed because " + errorMessage);
			return ResponseEntity.badRequest().body(report);
		}

		Map<String, Object> jsonMap = null;
		ObjectMapper objectMapper = null;
		String fileName = null;

		try {
			objectMapper = new ObjectMapper();
			jsonMap = objectMapper.readValue(reportParams, new TypeReference<Map<String, Object>>() {
			});

			// check request body has enough info in right JSON format
			if (!this.frequentlyUtils.checkKeysExist(jsonMap, "classID", "beginAt", "finishAt", "fileType")) {
				report = new ReportError(1, "You have to fill all required information!");
				return ResponseEntity.badRequest().body(report);
			}

			System.out.println("\n\nMile 1");
			Account tmpAccount = this.accountService.findAccountByID(adminID);
//			if (tmpAccount == null || (tmpAccount.getRole() != AccountRole.ADMIN.getValue()
//					&& tmpAccount.getRole() != AccountRole.TEACHER.getValue())) {
//				report = new ReportError(111, "Only admin and teacher has authority to use this API!");
//				return ResponseEntity.badRequest().body(report);
//			}
			
			int classID = Integer.parseInt(jsonMap.get("classID").toString());
			errorMessage = this.validationClassData.validateIdData(classID);
			if (errorMessage != null) {
				report = new ReportError(110, "Get report failed because " + errorMessage);
				return ResponseEntity.badRequest().body(report);
			}
			
			Class classInstance = this.classService.findClassByID(classID);
			if (classInstance == null) {
				report = new ReportError(110, "Get report failed because this class do not exist! ");
				return ResponseEntity.badRequest().body(report);
			}
			
			jsonMap.put("className", classInstance.getClassName());
			jsonMap.put("courseName", classInstance.getCourse().getCourseName());

			String fileType = jsonMap.get("fileType").toString();
			System.out.println("\n\n file type = " + fileType);
			
			if (!fileType.equalsIgnoreCase(GeneralValue.FILE_TYPE_XLSX) && !fileType.equalsIgnoreCase(GeneralValue.FILE_TYPE_XLS)
					&& !fileType.equalsIgnoreCase(GeneralValue.FILE_TYPE_PDF)) {
				report = new ReportError(110, "Get report failed because file type is inccorrect");
				return ResponseEntity.badRequest().body(report);
			}

			LocalDate beginAt = LocalDate.parse(jsonMap.get("beginAt").toString());
			LocalDate finishAt = LocalDate.parse(jsonMap.get("finishAt").toString());
			
			if (beginAt.isAfter(finishAt)) {
				report = new ReportError(110, "Begin date cannot is larger than Finish date!");
				return ResponseEntity.badRequest().body(report);
			}
			
			Semester semester = classInstance.getSemester();
			if (beginAt.isBefore(semester.getBeginDate()) || beginAt.isAfter(semester.getEndDate())) {
				beginAt = semester.getBeginDate();
			}
			if (finishAt.isBefore(semester.getBeginDate()) || finishAt.isAfter(semester.getEndDate())) {
				finishAt = semester.getEndDate();
			}

			fileName = GeneralValue.GENERAL_REPORT_FOR_STUDENT + "_" + classInstance.getClassName() + "_"
					+ LocalDate.now();
			
			String symlinkInServer = this.baseService.getFolderSymLink(fileName);

			String reportPath = BirtRuner.runBirtReport(GeneralValue.REPORT_FOR_CLASS_TEMPLATE, jsonMap,
					fileName, symlinkInServer);

			System.out.println("\n\n reportPath = " + reportPath);
			String symbolicLink = BaseService.genSymLink(reportPath);

			if (symbolicLink == null || symbolicLink.isBlank()) {
				// logger.info("GEN SYMBOLICLINK ERROR");
				output.setErrorCode(GeneralValue.GEN_SYMBOLIC_FAIL);
				output.setDescription("Generate symbolic link failed!");
			} else {
			// logger.info("link file : " + symbolicLink);
				output.setLinkFile(symbolicLink);
			}

		} catch (Exception e) {
			e.printStackTrace();
			report = new ReportError(2, "Error happened when jackson deserialization info!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, report.toString());
		}
		
		output.setDescription(fileName);
		return ResponseEntity.status(200).body(output);
	}
	
	
	

	/**
	 * This method for testing with postman, not use in app
	 * @param adminID
	 * @param reportParams
	 * @return
	 */
	@RequestMapping(value = "/studentGeneralReport", method = RequestMethod.PUT)
	public ResponseEntity<?> updateStudentGeneralReport(@RequestParam(value = "adminID", required = true) int adminID,
			@RequestBody String reportParams) {
		Map<String, Object> jsonMap = null;
		ObjectMapper objectMapper = null;

		List<GeneralStudentRecord> listRecord = new ArrayList<>();

		try {
			objectMapper = new ObjectMapper();
			jsonMap = objectMapper.readValue(reportParams, new TypeReference<Map<String, Object>>() {
			});

			String studentEmail = jsonMap.get("email").toString();
			String semesterID = jsonMap.get("semesterID").toString();
			String beginAtString = jsonMap.get("beginAt").toString();
			String finishAtString = jsonMap.get("finishAt").toString();
			listRecord = this.reportServiceImpl1.getArrayOfGeneralStudentRecord(studentEmail, semesterID, beginAtString,
					finishAtString);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "error");
		}

		return ResponseEntity.ok(listRecord);

	}
	
	/**
	 * This method for testing with postman, not use in app
	 * @param adminID
	 * @param reportParams
	 * @return
	 */
	@RequestMapping(value = "/reportForClass", method = RequestMethod.PUT)
	public ResponseEntity<?> updateReportForClass(@RequestParam(value = "adminID", required = true) int adminID,
			@RequestBody String reportParams) {
		Map<String, Object> jsonMap = null;
		ObjectMapper objectMapper = null;

		List<DetailRecordForClass> listRecord = new ArrayList<>();

		try {
			objectMapper = new ObjectMapper();
			jsonMap = objectMapper.readValue(reportParams, new TypeReference<Map<String, Object>>() {
			});

			int classID = Integer.parseInt(jsonMap.get("classID").toString());
			String beginAtString = jsonMap.get("beginAt").toString();
			String finishAtString = jsonMap.get("finishAt").toString();
			listRecord = this.reportServiceImpl1.getListOfClassRecord(classID, beginAtString, finishAtString);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "error");
		}

		return ResponseEntity.ok(listRecord);

	}

	
	/**
	 * This method for testing with postman, not use in app
	 * @param adminID
	 * @param reportParams
	 * @return
	 */
	@RequestMapping(value = "/reportForTeacher", method = RequestMethod.PUT)
	public ResponseEntity<?> updateReportForTeacher(@RequestParam(value = "adminID", required = true) int adminID,
			@RequestBody String reportParams) {
		Map<String, Object> jsonMap = null;
		ObjectMapper objectMapper = null;

		List<GeneralTeacherRecord> listRecord = new ArrayList<>();

		try {
			objectMapper = new ObjectMapper();
			jsonMap = objectMapper.readValue(reportParams, new TypeReference<Map<String, Object>>() {
			});

			String studentEmail = jsonMap.get("email").toString();
			String semesterID = jsonMap.get("semesterID").toString();
			String beginAtString = jsonMap.get("beginAt").toString();
			String finishAtString = jsonMap.get("finishAt").toString();
			listRecord = this.reportServiceImpl1.getListOfTeacherRecord(studentEmail, semesterID, beginAtString,
					finishAtString);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "error");
		}

		return ResponseEntity.ok(listRecord);

	}
}
