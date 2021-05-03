package edu.mta.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mta.model.ReportError;
import edu.mta.model.Semester;
import edu.mta.service.SemesterService;
import edu.mta.utils.FrequentlyUtils;
import edu.mta.utils.ValidationData;
import edu.mta.utils.ValidationSemesterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
public class SemesterController {

	private FrequentlyUtils frequentlyUtils;
	private ValidationData validationData;
	private SemesterService semesterService;
	private ValidationSemesterData validationSemesterData;

	public SemesterController() {
		super();
		
	}

	@Autowired
	public SemesterController(@Qualifier("SemesterServiceImpl1") SemesterService semesterService,
			@Qualifier("FrequentlyUtilsImpl1") FrequentlyUtils frequentlyUtils,
			@Qualifier("ValidationDataImpl1") ValidationData validationData,
			@Qualifier("ValidationSemesterDataImpl1") ValidationSemesterData validationSemesterData) {
		super();
		this.semesterService = semesterService;
		this.frequentlyUtils = frequentlyUtils;
		this.validationData = validationData;
		this.validationSemesterData = validationSemesterData;
	}

	@RequestMapping(value = "/semesters", method = RequestMethod.POST)
	public ResponseEntity<?> addSemesterInfo(@RequestBody String infoSemester) {
		String errorMessage = null;
		ObjectMapper objectMapper = null;
		Map<String, Object> jsonMap = null;
		Semester semester = null;
		String semesterName = null;
		LocalDate beginDate = null;
		LocalDate endDate = null;
		ReportError report = null;

		objectMapper = new ObjectMapper();
		try {
			jsonMap = objectMapper.readValue(infoSemester, new TypeReference<Map<String, Object>>() {
			});

			// check request body has enough info in right JSON format
			if (!this.frequentlyUtils.checkKeysExist(jsonMap, "semesterName", "beginDate", "endDate")) {
				report = new ReportError(1, "You have to fill all required information!");
				return ResponseEntity.badRequest().body(report);
			}

			errorMessage = this.validationData.validateSemesterData(jsonMap);
			if (errorMessage != null) {
				report = new ReportError(30, "Adding semester failed because " + errorMessage);
				return ResponseEntity.badRequest().body(report);
			}

			semesterName = jsonMap.get("semesterName").toString();
			semester = this.semesterService.findSemesterByName(semesterName);
			if (semester != null) {
				report = new ReportError(31, "This semester name has already been used!");
				return ResponseEntity.badRequest().body(report);
			}

			// check if semester's time begin is duplicate
			beginDate = LocalDate.parse(jsonMap.get("beginDate").toString());
			endDate = LocalDate.parse(jsonMap.get("endDate").toString());
			
			if (beginDate.isAfter(endDate)) {
				report = new ReportError(35, "Begin data cannot be after end date!");
				return ResponseEntity.badRequest().body(report);
			}
			
			if (!this.semesterService.checkSemesterTimeDuplicate(beginDate, endDate, semesterName)) {
				report = new ReportError(32, "This semester's duration violate another semester's duration!");
				return ResponseEntity.badRequest().body(report);
			}

			semester = new Semester(semesterName, beginDate, endDate);

			this.semesterService.addNewSemester(semester);
			report = new ReportError(200, "Adding new semester success!");
			return ResponseEntity.ok(report);
		} catch (Exception e) {
			e.printStackTrace();
			report = new ReportError(2, "Error happened when jackson deserialization info!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, report.toString());
		}
	}

	@RequestMapping(value = "/semesters", method = RequestMethod.GET)
	public ResponseEntity<?> getSemesterInfo(
			@RequestParam(value = "semesterName", required = true) String semesterName) {
		String errorMessage = this.validationSemesterData.validateSemesterNameData(semesterName);
		ReportError report = null;
		
		if (errorMessage != null) {
			report = new ReportError(37, "Getting semester info failed because " + errorMessage);
			return ResponseEntity.badRequest().body(report);
		}
		
		Semester semester = this.semesterService.findSemesterByName(semesterName);
		if (semester == null) {
			report = new ReportError(33, "This semester do not exist yet!");
			return new ResponseEntity<>(report, HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(semester);
	}
	
	@RequestMapping(value = "/semesters/id", method = RequestMethod.GET)
	public ResponseEntity<?> getSemesterInfoByID(
			@RequestParam(value = "semesterID", required = true) int semesterID) {
		ReportError report;
		Semester semester = this.semesterService.findSemesterById(semesterID);
		if (semester == null) {
			report = new ReportError(33, "This semester do not exist yet!");
			return new ResponseEntity<>(report, HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(semester);
	}
	
	@RequestMapping(value = "/semesters/all", method = RequestMethod.GET)
	public ResponseEntity<?> getAllSemester(@RequestParam(required = false) Integer page,
											@RequestParam(required = false) Integer pageSize) {
		Pageable pageRequest = PageRequest.of(page != null ? page : 0, pageSize != null ? pageSize : 5);
		Page<Semester> pageSemesters = this.semesterService.findAllSemester(pageRequest);
		if (pageSemesters == null) {
			return ResponseEntity.ok().body("No data founded!");
		} else {
			Map<String, Object> response = new HashMap<>();
			if (pageSemesters != null) {
				response.put("data", pageSemesters.getContent());
				response.put("totalPages", pageSemesters.getTotalPages());
				response.put("totalItems", pageSemesters.getTotalElements());
				response.put("currentPage", pageSemesters.getNumber());
				return ResponseEntity.ok(response);
			}
		}
		return null;
	}

	@RequestMapping(value = "/semesters", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteSemester(
			@RequestParam(value = "semesterName", required = true) String semesterName) {
		String errorMessage = this.validationSemesterData.validateSemesterNameData(semesterName);
		ReportError report = null;
		if (errorMessage != null) {
			report = new ReportError(36, "Deleting semester failed because " + errorMessage);
			return ResponseEntity.badRequest().body(report);
		}
		
		if (this.semesterService.findSemesterByName(semesterName) == null) {
			report = new ReportError(33, "This semester do not exist yet!");
			return new ResponseEntity<>(report, HttpStatus.NOT_FOUND);
		}

		if (!this.semesterService.checkSemesterDependant(semesterName)) {
			report = new ReportError(34, "This semester still has dependant!");
			return ResponseEntity.badRequest().body(report);
		}

		this.semesterService.deleteSemester(semesterName);
		report = new ReportError(200, "Deleting semester success!");
		return ResponseEntity.ok(report);
	}
	
	@RequestMapping(value = "/semesters", method = RequestMethod.PUT)
	public ResponseEntity<?> updateSemesterInfo(@RequestBody String infoSemester) {
		String errorMessage = null;
		ObjectMapper objectMapper = null;
		Map<String, Object> jsonMap = null;
		Semester semester = null;
		String semesterName = null;
		LocalDate beginDate = null;
		LocalDate endDate = null;
		int id = -1;
		ReportError report = null;

		objectMapper = new ObjectMapper();
		try {
			jsonMap = objectMapper.readValue(infoSemester, new TypeReference<Map<String, Object>>() {
			});

			// check request body has enough info in right JSON format
			if (!this.frequentlyUtils.checkKeysExist(jsonMap, "id", "semesterName", "beginDate", "endDate")) {
				report = new ReportError(1, "You have to fill all required information!");
				return ResponseEntity.badRequest().body(report);
			}

			errorMessage = this.validationData.validateSemesterData(jsonMap);
			if (errorMessage != null) {
				report = new ReportError(35, "Updating semester failed because " + errorMessage);
				return ResponseEntity.badRequest().body(report);
			}

			id = Integer.parseInt(jsonMap.get("id").toString());
			semesterName = jsonMap.get("semesterName").toString();
			semester = this.semesterService.findSemesterByName(semesterName);
			if (semester != null && semester.getSemesterID() != id) {
				report = new ReportError(31, "This semester name has already been used!");
				return ResponseEntity.badRequest().body(report);
			}
			
			
			semester = this.semesterService.findSemesterById(id);
			if (semester == null) {
				report = new ReportError(33, "This semester do not exist yet!");
				return new ResponseEntity<>(report, HttpStatus.NOT_FOUND);
			}

			// check if semester's time begin is duplicate
			beginDate = LocalDate.parse(jsonMap.get("beginDate").toString());
			endDate = LocalDate.parse(jsonMap.get("endDate").toString());
			if (!this.semesterService.checkSemesterTimeDuplicate(beginDate, endDate, semesterName)) {
				report = new ReportError(32, "This semester's duration violate another semester's duration!");
				return ResponseEntity.badRequest().body(report);
			}


			semester = new Semester(id, semesterName, beginDate, endDate);
			this.semesterService.updateSemesterInfo(semester);
			report = new ReportError(200, "Updating semester success!");
			return ResponseEntity.ok(report);
		} catch (Exception e) {
			e.printStackTrace();
			report = new ReportError(2, "Error happened when jackson deserialization info!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, report.toString());
		}
	}
	
	
	
}
