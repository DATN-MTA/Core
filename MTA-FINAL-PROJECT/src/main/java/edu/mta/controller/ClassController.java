package edu.mta.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mta.dto.ClassDTO;
import edu.mta.model.Class;
import edu.mta.model.Course;
import edu.mta.model.ReportError;
import edu.mta.model.Semester;
import edu.mta.service.ClassService;
import edu.mta.service.CourseService;
import edu.mta.service.SemesterService;
import edu.mta.utils.FrequentlyUtils;
import edu.mta.utils.ValidationClassData;
import edu.mta.utils.ValidationCourseData;
import edu.mta.utils.ValidationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin
@RestController
public class ClassController {

	private ClassService classService;
	private ValidationData validationData;
	private ValidationCourseData validationCourseData;
	private ValidationClassData validationClassData;
	private FrequentlyUtils frequentlyUtils;
	private SemesterService semesterService;
	private CourseService courseService;

	public ClassController() {
		super();
	}

	@Autowired
	public ClassController(@Qualifier("ClassServiceImpl1") ClassService classService,
			@Qualifier("ValidationDataImpl1") ValidationData validationData,
			@Qualifier("FrequentlyUtilsImpl1") FrequentlyUtils frequentlyUtils,
			@Qualifier("SemesterServiceImpl1") SemesterService semesterService,
			@Qualifier("CourseServiceImpl1") CourseService courseService,
			@Qualifier("ValidationClassDataImpl1") ValidationClassData validationClassData,
			@Qualifier("ValidationCourseDataImpl1") ValidationCourseData validationCourseData) {
		super();
		this.classService = classService;
		this.validationData = validationData;
		this.frequentlyUtils = frequentlyUtils;
		this.semesterService = semesterService;
		this.courseService = courseService;
		this.validationClassData = validationClassData;
		this.validationCourseData = validationCourseData;
	}

	@RequestMapping(value = "/classes", method = RequestMethod.POST)
	public ResponseEntity<?> addNewClass(@RequestBody String infoClass) {
		Map<String, Object> jsonMap = null;
		ObjectMapper objectMapper = null;
		String className = null;
		String errorMessage = null;
		Course course = null;
		Semester semester = null;
		LocalDate addingDate = null;
		Class classInstance = null;
		int maxStudent = -1;
		int numberOfLessons = -1;
		int courseID = -1;
		int semesterId = -1;
		ReportError report = null;

		try {
			objectMapper = new ObjectMapper();
			jsonMap = objectMapper.readValue(infoClass, new TypeReference<Map<String, Object>>() {
			});

			// check request body has enough info in right JSON format
			if (!this.frequentlyUtils.checkKeysExist(jsonMap, "className", "maxStudent", "numberOfLessons", "courseID",
					"semesterID")) {
				report = new ReportError(1, "You have to fill all required information!");
				return ResponseEntity.badRequest().body(report);
			}

			errorMessage = this.validationData.validateClassData(jsonMap);
			if (errorMessage != null) {
				report = new ReportError(60, "Adding new class failed because " + errorMessage);
				return ResponseEntity.badRequest().body(report);
			}

			courseID = Integer.parseInt(jsonMap.get("courseID").toString());
			course = this.courseService.getCourseInfo(courseID);
			if (course == null) {
				report = new ReportError(43, "This course do not exist!");
				return new ResponseEntity<>(report, HttpStatus.NOT_FOUND);
			}

			semesterId = Integer.parseInt(jsonMap.get("semesterID").toString());
			semester = this.semesterService.findSemesterById(semesterId);
			if (semester == null) {
				report = new ReportError(33, "This semester do not exist!");
				return new ResponseEntity<>(report, HttpStatus.NOT_FOUND);
			}
			
			className = jsonMap.get("className").toString();
			if (this.classService.findClassByClassName(className) != null) {
				report = new ReportError(33, "Semester name is duplicate!");
				return new ResponseEntity<>(report, HttpStatus.CONFLICT);
			}

			addingDate = LocalDate.now();
			if (addingDate.isAfter(semester.getBeginDate().plusDays(14))) {
				report = new ReportError(61, "Adding new class do not work after semester begins 2 weeks " + errorMessage);
				return ResponseEntity.badRequest().body(report);
			}

			numberOfLessons = Integer.parseUnsignedInt(jsonMap.get("numberOfLessons").toString());
			maxStudent = Integer.parseUnsignedInt(jsonMap.get("maxStudent").toString());

			classInstance = new Class(className, maxStudent, numberOfLessons);
			classInstance.setCurrentLesson(0);
			classInstance.setIdentifyString(null);
			classInstance.setIsChecked(null);
			classInstance.setCourse(course);
			classInstance.setSemester(semester);

			this.classService.addNewClass(classInstance);
			report = new ReportError(200, "Adding new class successes!");
			return ResponseEntity.ok(report);

		} catch (Exception e) {
			e.printStackTrace();
			report = new ReportError(2, "Error happened when jackson deserialization info!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, report.toString());
		}
	}

	@RequestMapping(value = "/classes", method = RequestMethod.GET)
	public ResponseEntity<?> getInfoClass(@RequestParam(value = "classID", required = true) int classID) {
		String errorMessage = this.validationClassData.validateIdData(classID);
		ReportError report = null;
		if (errorMessage != null) {
			report = new ReportError(62, "Getting class info failed because " + errorMessage);
			return ResponseEntity.badRequest().body(report);
		}

		Class classInstance = this.classService.findClassByID(classID);
		if (classInstance == null) {
			report = new ReportError(63, "This class do not exist!");
			return new ResponseEntity<>(report, HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok(classInstance);
	}
	
	@DeleteMapping("/classes")
	public ResponseEntity<?> deleteInfoClass(@RequestParam(value = "classID", required = true) int classID) {
		String errorMessage = this.validationClassData.validateIdData(classID);
		ReportError report = null;
		if (errorMessage != null) {
			report = new ReportError(62, "Getting class info failed because " + errorMessage);
			return ResponseEntity.badRequest().body(report);
		}
		
		Class classInstance = this.classService.findClassByID(classID);
		if (classInstance == null) {
			report = new ReportError(63, "This class do not exist!");
			return new ResponseEntity<>(report, HttpStatus.NOT_FOUND);
		}
		
		if (this.classService.deleteClassInfo(classID)) {
			return ResponseEntity.ok("Deleting class successes!");
		}
		
		report = new ReportError(66, "This class still has dependant! ");
		return ResponseEntity.badRequest().body(report);
	}

	@PutMapping("/classes")
	public ResponseEntity<?> updateInfoClass(@RequestBody String infoClass) {
		Map<String, Object> jsonMap = null;
		ObjectMapper objectMapper = null;
		String className = null;
		String errorMessage = null;
		Course course = null;
		Semester semester = null;
		Class classInstance = null;
		int id = -1;
		int maxStudent = -1;
		int numberOfLessons = -1;
		int courseID = -1;
		int semesterId = -1;
		ReportError report = null;

		try {
			objectMapper = new ObjectMapper();
			jsonMap = objectMapper.readValue(infoClass, new TypeReference<Map<String, Object>>() {
			});

			// check request body has enough info in right JSON format
			if (!this.frequentlyUtils.checkKeysExist(jsonMap, "id", "className", "maxStudent", "numberOfLessons",
					"courseID", "semesterID")) {
				report = new ReportError(1, "You have to fill all required information!");
				return ResponseEntity.badRequest().body(report);
			}

			errorMessage = this.validationData.validateClassData(jsonMap);
			if (errorMessage != null) {
				report = new ReportError(64, "Updating class info failed because " + errorMessage);
				return ResponseEntity.badRequest().body(report);
			}

			id = Integer.parseUnsignedInt(jsonMap.get("id").toString());
			classInstance = this.classService.findClassByID(id);
			if (classInstance == null) {
				report = new ReportError(63, "This class do not exist!");
				return new ResponseEntity<>(report, HttpStatus.NOT_FOUND);
			}
			
			className = jsonMap.get("className").toString();
			Class tmpClass = this.classService.findClassByClassName(className);
			if (tmpClass != null && tmpClass.getId() != id) {
				report = new ReportError(64, "Class name is duplicate!");
				return new ResponseEntity<>(report, HttpStatus.CONFLICT);
			}
			classInstance.setClassName(className);

//			courseID = Integer.parseInt(jsonMap.get("courseID").toString());
//			if (classInstance.getCourse().getCourseID() != courseID) {
//				course = this.courseService.getCourseInfo(courseID);
//				if (course == null) {
//					report = new ReportError(43, "This course do not exist!");
//					return new ResponseEntity<>(report, HttpStatus.NOT_FOUND);
//				}
//			}

//			semesterId = Integer.parseInt(jsonMap.get("semesterID").toString());
//			if (classInstance.getSemester().getSemesterID() != semesterId) {
//				semester = this.semesterService.findSemesterById(semesterId);
//				if (semester == null) {
//					report = new ReportError(33, "This semester do not exist!");
//					return new ResponseEntity<>(report, HttpStatus.NOT_FOUND);
//				}
//			} else {
//				semester = classInstance.getSemester();
//			}
			
//			LocalDate updateDate = LocalDate.now();
//			if (updateDate.isAfter(semester.getBeginDate().plusDays(14))) {
//				report = new ReportError(65, "Updating class info do not work after semester begins 2 weeks! ");
//				return ResponseEntity.badRequest().body(report);
//			}

			numberOfLessons = Integer.parseUnsignedInt(jsonMap.get("numberOfLessons").toString());
			maxStudent = Integer.parseUnsignedInt(jsonMap.get("maxStudent").toString());
			
			classInstance.setNumberOfLessons(numberOfLessons);
			classInstance.setMaxStudent(maxStudent);

			this.classService.updateClassInfo(classInstance);
			report = new ReportError(200, "Updating class info successes!");
			return ResponseEntity.ok(report);

		} catch (Exception e) {
			e.printStackTrace();
			report = new ReportError(2, "Error happened when jackson deserialization info!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, report.toString());
		}
	}
	
	@RequestMapping(value = "/getClassInfo", method = RequestMethod.GET)
	public ResponseEntity<?> getClassInfo(@RequestParam(value = "className", required = true) String className) {
		String errorMessage = this.validationClassData.validateClassNameData(className);
		ReportError report = null;
		if (errorMessage != null) {
			report = new ReportError(62, "Getting class info failed because " + errorMessage);
			return ResponseEntity.badRequest().body(report);
		}

		Class classInstance = this.classService.findClassByClassName(className);
		if (classInstance == null) {
			report = new ReportError(63, "This class do not exist!");
			return new ResponseEntity<>(report, HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok(classInstance);
	}
	
	@GetMapping("/listClass")
	public ResponseEntity<?> getListClass(@RequestParam(value = "courseID", required = true) int courseID) {

		String errorMessage = null;
		ReportError report = null;
		errorMessage = this.validationCourseData.validateIdData(courseID);
		if (errorMessage != null) {
			report = new ReportError(73, "Getting list class failed because " + errorMessage);
			return ResponseEntity.badRequest().body(report);
		}

		List<Class> listClasses = this.classService.getListClassByCourseID(courseID);
		if (listClasses != null && !listClasses.isEmpty()) {
			
			return ResponseEntity.ok(listClasses);
		}

		report = new ReportError(74, "No record is found!");
		return new ResponseEntity<>(report, HttpStatus.NOT_FOUND);
	}

	@GetMapping("/getAllClasses")
	public ResponseEntity<?> getAllClasses(@RequestParam(required = false) Integer page,
										   @RequestParam(required = false) Integer pageSize) {
		Pageable pageRequest = PageRequest.of(page != null ? page : 0, pageSize != null ? pageSize : 5);
		Page<Class> pageClasses = this.classService.getAllClasses(pageRequest != null ? pageRequest : null);
		if (pageClasses == null) {
			return ResponseEntity.badRequest().body("No data founded!");
		} else {
			Map<String, Object> response = new HashMap<>();
			List<Class> listClass = pageClasses.getContent();
			List<ClassDTO> listClassDTO = new ArrayList<>();
			for (Class classToConvert: listClass) {
				ClassDTO classDTO = new ClassDTO();
				classDTO.setClassId(classToConvert.getId());
				classDTO.setSemester(classToConvert.getSemester().getSemesterName());
				classDTO.setCourse(classToConvert.getCourse().getCourseName());
				classDTO.setNumberOfLession(classToConvert.getNumberOfLessons());
				classDTO.setNumberOfStudents(classToConvert.getMaxStudent());
				classDTO.setClassName(classToConvert.getClassName());
				classDTO.setCurrentLession(classToConvert.getCurrentLesson());
				listClassDTO.add(classDTO);
			}
			if (pageClasses != null) {
				response.put("data", listClassDTO);
				response.put("totalPages", pageClasses.getTotalPages());
				response.put("totalItems", pageClasses.getTotalElements());
				response.put("currentPage", pageClasses.getNumber());
				return ResponseEntity.ok(response);
			}
		}
		return null;
	}

	@GetMapping("/getClassesBySemesterAndCourse")
	public ResponseEntity<?> getClassesBySemesterAndCourse(@RequestParam(value = "semesterId", required = false) Integer semesterId,
														   @RequestParam(value = "courseId", required = false) Integer courseId,
														   @RequestParam(required = false) Integer page,
										                   @RequestParam(required = false) Integer pageSize) {
		Pageable pageRequest = PageRequest.of(page != null ? page : 0, pageSize != null ? pageSize : 5);
		Page<Class> pageClasses = this.classService.getClassesBySemesterAndCourse(pageRequest, semesterId, courseId);
		if (pageClasses == null) {
			return ResponseEntity.badRequest().body("No data founded!");
		} else {
			Map<String, Object> response = new HashMap<>();
			List<Class> listClass = pageClasses.getContent();
			List<ClassDTO> listClassDTO = new ArrayList<>();
			for (Class classToConvert: listClass) {
				ClassDTO classDTO = new ClassDTO();
				classDTO.setClassId(classToConvert.getId());
				classDTO.setSemester(classToConvert.getSemester().getSemesterName());
				classDTO.setCourse(classToConvert.getCourse().getCourseName());
				classDTO.setNumberOfLession(classToConvert.getNumberOfLessons());
				classDTO.setNumberOfStudents(classToConvert.getMaxStudent());
				classDTO.setClassName(classToConvert.getClassName());
				classDTO.setCurrentLession(classToConvert.getCurrentLesson());
				listClassDTO.add(classDTO);
			}
			if (pageClasses != null) {
				response.put("data", listClassDTO);
				response.put("totalPages", pageClasses.getTotalPages());
				response.put("totalItems", pageClasses.getTotalElements());
				response.put("currentPage", pageClasses.getNumber());
				return ResponseEntity.ok(response);
			}
		}
		return null;
	}
}
