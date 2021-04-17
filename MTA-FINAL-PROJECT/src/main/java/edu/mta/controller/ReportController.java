package edu.mta.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.mta.enumData.AccountRole;
import edu.mta.model.Class;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.mta.model.Account;
import edu.mta.model.GeneralStudentRecord;
import edu.mta.model.GeneralTeacherRecord;
import edu.mta.model.ReportError;
import edu.mta.model.ReportOutput;
import edu.mta.model.Semester;
import edu.mta.model.DetailRecordForClass;
import edu.mta.service.AccountService;
import edu.mta.service.BaseService;
import edu.mta.service.BirtRuner;
import edu.mta.service.ClassService;
import edu.mta.service.ReportServiceImpl1;
import edu.mta.service.SemesterService;
import edu.mta.utils.GeneralValue;
import edu.mta.utils.FrequentlyUtils;
import edu.mta.utils.ValidationAccountData;
import edu.mta.utils.ValidationClassData;
import edu.mta.utils.ValidationSemesterData;

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
			if (tmpAccount == null || (tmpAccount.getRole() != AccountRole.ADMIN.getValue()
					&& tmpAccount.getRole() != AccountRole.TEACHER.getValue())) {
				report = new ReportError(111, "Only admin and teacher has authority to use this API!");
				return ResponseEntity.badRequest().body(report);
			}

			System.out.println("\n\nMile 2");
			String teacherEmail = jsonMap.get("email").toString();
			errorMessage = this.validationAccountData.validateEmailData(teacherEmail);
			if (errorMessage != null) {
				report = new ReportError(110, "Get report failed because " + errorMessage);
				return ResponseEntity.badRequest().body(report);
			}

			System.out.println("\n\nMile 3");
			Account account = this.accountService.findAccountByEmail(teacherEmail);
			if (account == null || account.getRole() != AccountRole.TEACHER.getValue()) {
				report = new ReportError(112, "This email address is not valid!");
				return ResponseEntity.badRequest().body(report);
			}

			System.out.println("\n\nMile 4");
			System.out.println("\n\n user info = " + account.getUserInfo());
			String[] teacherInfo = account.getUserInfo().split(GeneralValue.regexForSplitUserInfo);
			System.out.println("\n\n full name = " + teacherInfo[0]);

			jsonMap.put("teacherName", teacherInfo[0]);

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
			if (tmpAccount == null || (tmpAccount.getRole() != AccountRole.ADMIN.getValue()
					&& tmpAccount.getRole() != AccountRole.TEACHER.getValue())) {
				report = new ReportError(111, "Only admin and teacher has authority to use this API!");
				return ResponseEntity.badRequest().body(report);
			}

			System.out.println("\n\nMile 2");
			String studentEmail = jsonMap.get("email").toString();
			errorMessage = this.validationAccountData.validateEmailData(studentEmail);
			if (errorMessage != null) {
				report = new ReportError(110, "Get report failed because " + errorMessage);
				return ResponseEntity.badRequest().body(report);
			}

			System.out.println("\n\nMile 3");
			Account account = this.accountService.findAccountByEmail(studentEmail);
			if (account == null || account.getRole() != AccountRole.STUDENT.getValue()) {
				report = new ReportError(112, "This email address is not valid!");
				return ResponseEntity.badRequest().body(report);
			}

			System.out.println("\n\nMile 4");
			System.out.println("\n\n user info = " + account.getUserInfo());
			String[] studentInfo = account.getUserInfo().split(GeneralValue.regexForSplitUserInfo);
			System.out.println("\n\n full name = " + studentInfo[0]);

			jsonMap.put("studentName", studentInfo[0]);

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
			if (tmpAccount == null || (tmpAccount.getRole() != AccountRole.ADMIN.getValue()
					&& tmpAccount.getRole() != AccountRole.TEACHER.getValue())) {
				report = new ReportError(111, "Only admin and teacher has authority to use this API!");
				return ResponseEntity.badRequest().body(report);
			}
			
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
