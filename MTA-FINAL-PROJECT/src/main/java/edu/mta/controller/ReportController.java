package edu.mta.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mta.dto.ReportByAccountDTO;
import edu.mta.dto.ReportByClassDTO;
import edu.mta.exception.CustomException;
import edu.mta.model.Class;
import edu.mta.model.*;
import edu.mta.service.*;
import edu.mta.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private BirtRuner birtRuner;

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
        this.validationAccountData = validationAccountData;
        this.frequentlyUtils = frequentlyUtils;
        this.accountService = accountService;
        this.validationSemesterData = validationSemesterData;
        this.baseService = baseService;
        this.semesterService = semesterService;
        this.classService = classService;
        this.validationClassData = validationClassData;
    }

    @PostMapping(value = "/accountGeneralReport")
    public ResponseEntity<?> getTeacherGeneralReport(@RequestBody ReportByAccountDTO reportDTO, HttpServletRequest request) {
        ReportError report = null;
        String errorMessage;

        Map<String, Object> jsonMap = null;
        ObjectMapper objectMapper = null;
        String fileName = null;
        boolean isTeacher;

        try {
            objectMapper = new ObjectMapper();
            jsonMap = objectMapper.readValue(objectMapper.writeValueAsString(reportDTO), new TypeReference<Map<String, Object>>() {
            });

            // check request body has enough info in right JSON format
            if (!this.frequentlyUtils.checkKeysExist(jsonMap, "email", "semesterID", "beginAt", "finishAt", "fileType")) {
                report = new ReportError(1, "You have to fill all required information!");
                return ResponseEntity.badRequest().body(report);
            }

            Account account = this.accountService.whoami(request);
            if (account.getRoles().contains(Role.ROLE_ADMIN)) {
                if (reportDTO.getEmail() != null) {
                    Account accountExport = this.accountService.findAccountByEmail(reportDTO.getEmail());
                    if (accountExport == null) {
                        throw new CustomException("Admin role  - Account with email supplied not found", HttpStatus.BAD_REQUEST);
                    } else {
                        if (accountExport.getRoles().contains(Role.ROLE_TEACHER)) {
                            isTeacher = true;
                            jsonMap.put("teacherName", accountExport.getUser().getFullName());
                        } else {
                            jsonMap.put("studentName", account.getUser().getFullName());
                            isTeacher = false;
                        }
                    }
                } else {
                    throw new CustomException("Admin role  - email is required Field", HttpStatus.BAD_REQUEST);
                }
            } else {
                if (account.getRoles().contains(Role.ROLE_TEACHER)) {
                    isTeacher = true;
                    jsonMap.put("teacherName", account.getUser().getFullName());
                } else {
                    jsonMap.put("studentName", account.getUser().getFullName());
                    isTeacher = false;
                }
            }

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
            if (!fileType.equalsIgnoreCase(GeneralValue.FILE_TYPE_XLS)
                    && !fileType.equalsIgnoreCase(GeneralValue.FILE_TYPE_PDF)) {
                report = new ReportError(110, "Get report failed because file type is inccorrect");
                return ResponseEntity.badRequest().body(report);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            LocalDate beginAt = LocalDate.parse(jsonMap.get("beginAt").toString(), formatter);
            LocalDate finishAt = LocalDate.parse(jsonMap.get("finishAt").toString(), formatter);

            if (beginAt.isAfter(finishAt)) {
                report = new ReportError(110, "Begin date cannot is larger than Finish date!");
                return ResponseEntity.badRequest().body(report);
            }

            if (beginAt.isBefore(semester.getBeginDate()) || beginAt.isAfter(semester.getEndDate())) {
                beginAt = semester.getBeginDate();
                jsonMap.put("beginAt", beginAt.format(formatter));
            }
            if (finishAt.isBefore(semester.getBeginDate()) || finishAt.isAfter(semester.getEndDate())) {
                finishAt = semester.getEndDate();
                jsonMap.put("finishAt", finishAt.format(formatter));
            }

            fileName = (isTeacher?GeneralValue.GENERAL_REPORT_FOR_TEACHER:GeneralValue.GENERAL_REPORT_FOR_STUDENT) + "_"
                    + LocalDateTime.now();

            ByteArrayOutputStream byteArrayOutputStream = birtRuner.runBirtReport(isTeacher?GeneralValue.GENERAL_REPORT_FOR_TEACHER_TEMPLATE:GeneralValue.GENERAL_REPORT_FOR_STUDENT_TEMPLATE, jsonMap);

            InputStreamResource fileResult = new InputStreamResource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName +"." + fileType)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(fileResult);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/classDetailReport")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    public ResponseEntity<?> getClassDetailReport( @RequestBody ReportByClassDTO reportDTO) {

        ReportOutput output = new ReportOutput();
        ReportError report = null;
        String errorMessage;

        Map<String, Object> jsonMap = null;
        ObjectMapper objectMapper = null;
        String fileName = null;

        try {
            objectMapper = new ObjectMapper();
            jsonMap = objectMapper.readValue(objectMapper.writeValueAsString(reportDTO), new TypeReference<Map<String, Object>>() {
            });

            // check request body has enough info in right JSON format
            if (!this.frequentlyUtils.checkKeysExist(jsonMap, "classID", "beginAt", "finishAt", "fileType")) {
                report = new ReportError(1, "You have to fill all required information!");
                return ResponseEntity.badRequest().body(report);
            }

            System.out.println("\n\nMile 1");

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

            if (!fileType.equalsIgnoreCase(GeneralValue.FILE_TYPE_XLS)
                    && !fileType.equalsIgnoreCase(GeneralValue.FILE_TYPE_PDF)) {
                report = new ReportError(110, "Get report failed because file type is inccorrect");
                return ResponseEntity.badRequest().body(report);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            LocalDate beginAt = LocalDate.parse(jsonMap.get("beginAt").toString(), formatter);
            LocalDate finishAt = LocalDate.parse(jsonMap.get("finishAt").toString(), formatter);

            if (beginAt.isAfter(finishAt)) {
                report = new ReportError(110, "Begin date cannot is larger than Finish date!");
                return ResponseEntity.badRequest().body(report);
            }

            Semester semester = classInstance.getSemester();
            if (beginAt.isBefore(semester.getBeginDate()) || beginAt.isAfter(semester.getEndDate())) {
                beginAt = semester.getBeginDate();
                jsonMap.put("beginAt", beginAt.format(formatter));
            }
            if (finishAt.isBefore(semester.getBeginDate()) || finishAt.isAfter(semester.getEndDate())) {
                finishAt = semester.getEndDate();
                jsonMap.put("finishAt", finishAt.format(formatter));
            }

            fileName = GeneralValue.REPORT_FOR_CLASS + "_" + LocalDateTime.now();

            ByteArrayOutputStream byteArrayOutputStream = birtRuner.runBirtReport(GeneralValue.REPORT_FOR_CLASS_TEMPLATE, jsonMap);

            InputStreamResource fileResult = new InputStreamResource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName +"." + fileType)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(fileResult);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}
