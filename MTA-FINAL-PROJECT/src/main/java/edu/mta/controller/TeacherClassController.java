package edu.mta.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mta.dto.AccountDataDTO;
import edu.mta.dto.ClassDTO;
import edu.mta.dto.TeacherClassDTO;
import edu.mta.dto.TeacherClassResponseDTO;
import edu.mta.exception.CustomException;
import edu.mta.helper.AccountExcelHelper;
import edu.mta.helper.TeacherClassExcelHelper;
import edu.mta.model.ReportError;
import edu.mta.model.TeacherClass;
import edu.mta.service.*;
import edu.mta.service.exel.TeacherClassExcelService;
import edu.mta.utils.FrequentlyUtils;
import edu.mta.utils.ValidationAccountData;
import edu.mta.utils.ValidationRoomData;
import edu.mta.utils.ValidationTeacherClassData;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@CrossOrigin
@RestController
public class TeacherClassController {

    private TeacherClassService teacherClassService;
    private StudentClassService studentClassService;
    private AccountService accountService;
    private ValidationTeacherClassData validationTeacherClassData;
    private ClassService classService;
    private ValidationAccountData validationAccountData;
    private ValidationRoomData validationRoomData;
    private FrequentlyUtils frequentlyUtils;

    public TeacherClassController() {
        super();
    }

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TeacherClassExcelHelper teacherClassExcelHelper;

    @Autowired
    private TeacherClassExcelService teacherClassExcelService;

    @Autowired
    public TeacherClassController(@Qualifier("TeacherClassServiceImpl1") TeacherClassService teacherClassService,
                                  @Qualifier("StudentClassServiceImpl1") StudentClassService studentClassService,
                                  @Qualifier("AccountServiceImpl1") AccountService accountService,
                                  @Qualifier("FrequentlyUtilsImpl1") FrequentlyUtils frequentlyUtils,
                                  @Qualifier("ValidationTeacherClassDataImpl1") ValidationTeacherClassData validationTeacherClassData,
                                  @Qualifier("ValidationRoomDataImpl1") ValidationRoomData validationRoomData,
                                  @Qualifier("ClassServiceImpl1") ClassService classService,
                                  // @Qualifier("ValidationStudentClassDataImpl1") ValidationStudentClassData
                                  // validationStudentClassData,
                                  @Qualifier("ValidationAccountDataImpl1") ValidationAccountData validationAccountData,
                                  @Qualifier("RoomServiceImpl1") RoomService roomService) {
        super();
        this.teacherClassService = teacherClassService;
        this.studentClassService = studentClassService;
        this.accountService = accountService;
        //this.roomService = roomService;
        this.validationRoomData = validationRoomData;
        this.validationTeacherClassData = validationTeacherClassData;
        this.classService = classService;
        // this.validationStudentClassData = validationStudentClassData;
        this.validationAccountData = validationAccountData;
        this.frequentlyUtils = frequentlyUtils;

    }

    @PostMapping(value = "/teacherRollCall")
    public ResponseEntity<?> rollCall(@RequestBody String info) {
        int teacherID = 0;
        int classID = 0;
        int roomID = 0;
        int weekday = 0;
//		double gpsLong;
//		double gpsLa;
        String inputMd5 = null;
        String result = null;
        String errorMessage = null;
        LocalDateTime rollCallAt = null;
        LocalTime generateTime = null;
        ObjectMapper objectMapper = null;
        Map<String, Object> jsonMap = null;
        ReportError report = null;

        try {
            objectMapper = new ObjectMapper();
            jsonMap = objectMapper.readValue(info, new TypeReference<Map<String, Object>>() {
            });

            // check request body has enough info in right JSON format
//			if (!this.jsonMapUtil.checkKeysExist(jsonMap, "teacherID", "classID", "roomID", "gpsLong", "gpsLa")) {
//				report = new ReportError(1, "You have to fill all required information!");
//				return ResponseEntity.badRequest().body(report);
//			}

            if (!this.frequentlyUtils.checkKeysExist(jsonMap, "teacherID", "classID", "roomID")) {
                report = new ReportError(1, "You have to fill all required information!");
                return ResponseEntity.badRequest().body(report);
            }

            teacherID = Integer.parseInt(jsonMap.get("teacherID").toString());
            errorMessage = this.validationTeacherClassData.validateIdData(teacherID);
            if (errorMessage != null) {
                report = new ReportError(80, "Teacher roll call failed because " + errorMessage);
                return ResponseEntity.badRequest().body(report);
            }

            classID = Integer.parseInt(jsonMap.get("classID").toString());
            errorMessage = this.validationTeacherClassData.validateIdData(classID);
            if (errorMessage != null) {
                report = new ReportError(80, "Teacher roll call failed because " + errorMessage);
                return ResponseEntity.badRequest().body(report);
            }

            roomID = Integer.parseInt(jsonMap.get("roomID").toString());
            errorMessage = this.validationRoomData.validateIdData(roomID);
            if (errorMessage != null) {
                report = new ReportError(80, "Teacher roll call failed because " + errorMessage);
                return ResponseEntity.badRequest().body(report);
            }

//			gpsLong = Double.parseDouble(jsonMap.get("gpsLong").toString());
//			gpsLa = Double.parseDouble(jsonMap.get("gpsLa").toString());
//			if (gpsLong < -180 || gpsLong > 180 || gpsLa < -90 || gpsLa > 90) {
//				report = new ReportError(84, "Longitude/Latitude is out of practical range!");
//				return ResponseEntity.badRequest().body(report);
//			}

            // check teacher has authority to roll call this class
            if (!this.teacherClassService.checkTeacherHasAuthority(teacherID, classID)) {
                report = new ReportError(11, "Authentication has failed or has not yet been provided!");
                return new ResponseEntity<>(report, HttpStatus.UNAUTHORIZED);
            }

            // check if device is in distance limit - 50m
//			if (this.roomService.calculateDistanceBetween2GPSCoord(roomID, gpsLong, gpsLa) > 50) {
//				report = new ReportError(85, "Device is out of valid distance to classroom!");
//				return ResponseEntity.badRequest().body(report);
//			}

            // Check teacher generate time in valid limit
            // Notice: weekday of java = weekday of mySQL - 1
            generateTime = LocalTime.now();
            weekday = LocalDate.now().getDayOfWeek().getValue() + 1;
            if (!this.teacherClassService.checkGenerateTimeValid(weekday, generateTime, classID, roomID)) {
                report = new ReportError(81, "Class is not in lesson's time!");
                return ResponseEntity.badRequest().body(report);
            }

            // generate md5 code
            inputMd5 = generateTime.toString() + classID + generateTime.getMinute() + (new Random()).nextInt(1000);
            result = this.teacherClassService.generateIdentifyString(classID, roomID, weekday, inputMd5);
            if (result == null) {
                report = new ReportError(82, "Cannot generate identify string!");
                return ResponseEntity.badRequest().body(report);
            }

            // add generate time and date to teacher's listRollCall
            rollCallAt = LocalDateTime.now();
            this.teacherClassService.rollCall(rollCallAt, teacherID, classID);

            report = new ReportError(200, result);
            return ResponseEntity.ok(report);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            report = new ReportError(83, "Teacher roll call failed because an ID is not a number");
            return ResponseEntity.badRequest().body(report);
        } catch (Exception e) {
            e.printStackTrace();
            report = new ReportError(2, "Error happened when jackson deserialization info!");
            throw new CustomException(report.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/rollCallStudentWithPermission")
    public ResponseEntity<?> rollCallStudentWithPermission(@RequestBody String info) {
        int teacherID = 0;
        int classID = 0;
        int roomID = 0;
//		double gpsLong;
//		double gpsLa;
        int reason = -1;
        String studentEmail = null;
//		String studentPassword = null;
        String errorMessage = null;
        ObjectMapper objectMapper = null;
        Map<String, Object> jsonMap = null;
        ReportError report = null;

        try {
            objectMapper = new ObjectMapper();
            jsonMap = objectMapper.readValue(info, new TypeReference<Map<String, Object>>() {
            });

            if (!this.frequentlyUtils.checkKeysExist(jsonMap, "teacherID", "classID", "roomID", "studentEmail", "reason")) {
                report = new ReportError(1, "You have to fill all required information!");
                return ResponseEntity.badRequest().body(report);
            }

            reason = Integer.parseInt(jsonMap.get("reason").toString());
            errorMessage = this.teacherClassService.checkReasonValid(reason);
            if (errorMessage != null) {
                report = new ReportError(80, "Teacher roll call failed because " + errorMessage);
                return ResponseEntity.badRequest().body(report);
            }

            teacherID = Integer.parseInt(jsonMap.get("teacherID").toString());
            errorMessage = this.validationTeacherClassData.validateIdData(teacherID);
            if (errorMessage != null) {
                report = new ReportError(80, "Teacher roll call failed because " + errorMessage);
                return ResponseEntity.badRequest().body(report);
            }

            classID = Integer.parseInt(jsonMap.get("classID").toString());
            errorMessage = this.validationTeacherClassData.validateIdData(classID);
            if (errorMessage != null) {
                report = new ReportError(80, "Teacher roll call failed because " + errorMessage);
                return ResponseEntity.badRequest().body(report);
            }

            studentEmail = jsonMap.get("studentEmail").toString();
            errorMessage = this.validationAccountData.validateEmailData(studentEmail);
            if (errorMessage != null) {
                report = new ReportError(86, errorMessage);
                return ResponseEntity.badRequest().body(report);
            }

            roomID = Integer.parseInt(jsonMap.get("roomID").toString());
            errorMessage = this.validationRoomData.validateIdData(roomID);
            if (errorMessage != null) {
                report = new ReportError(80, "Teacher roll call failed because " + errorMessage);
                return ResponseEntity.badRequest().body(report);
            }

            // check student's email and password are valid
            // studentPassword = jsonMap.get("studentPassword").toString();
            if (this.accountService.findAccountByEmail(studentEmail) == null) {
                report = new ReportError(11, "This student is not existed!");
                return new ResponseEntity<>(report, HttpStatus.UNAUTHORIZED);
            }

            // check student is learning in this class
            if (!this.studentClassService.checkStudentIsLearning(studentEmail, classID)) {
                report = new ReportError(87,
                        "This student is not in class or dropped out");
                return ResponseEntity.badRequest().body(report);
            }

            // check teacher has authority to roll call this class
            if (!this.teacherClassService.checkTeacherHasAuthority(teacherID, classID)) {
                report = new ReportError(11, "Authentication has failed or has not yet been provided!");
                return new ResponseEntity<>(report, HttpStatus.UNAUTHORIZED);
            }

            if (!this.teacherClassService.rollCallStudentWithPermission(studentEmail, classID, roomID, reason)) {
                report = new ReportError(88, "This student has rolled call already!!");
                return ResponseEntity.badRequest().body(report);
            }

            report = new ReportError(200, "Roll call student with permission successes!");
            return ResponseEntity.ok(report);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            report = new ReportError(83, "Teacher roll call student failed because an ID is not a number");
            return ResponseEntity.badRequest().body(report);
        } catch (Exception e) {
            e.printStackTrace();
            report = new ReportError(2, "Error happened when jackson deserialization info!");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, report.toString());
        }
    }

    @PostMapping(value = "/addOrUpdateTeacherToClass")
    @ApiOperation(value = "Assign single teacher to class")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {//
            @ApiResponse(code = 204, message = "No data founded"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> addTeacherClass(@RequestBody TeacherClassDTO teacherClassDTO) {
        ReportError report;
        String result = this.teacherClassService.addNewTeacherClass(teacherClassDTO);
        if (result == null) {
            report = new ReportError(200, "Adding teacher to class success!");
            return ResponseEntity.ok(report);
        } else {
            throw new CustomException(result, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/getTeacherClassByTeacherId")
    public ResponseEntity<?> getTeacherClassInfoByTeacherID(
            @RequestParam(value = "teacherID", required = true) int teacherID) {

        ReportError report = null;

        if (teacherID < 1) {
            report = new ReportError(120, "Teacher id must not less be than 1!");
            return ResponseEntity.badRequest().body(report);
        }

        List<TeacherClass> listRecords = this.teacherClassService.findByCurrentTeacherID(teacherID);
        if (listRecords == null || listRecords.isEmpty()) {
            report = new ReportError(121, "No record is found!");
            return ResponseEntity.badRequest().body(report);
        }

        return ResponseEntity.ok(listRecords);
    }

    @GetMapping(value = "/getTeacherClass")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {//
            @ApiResponse(code = 204, message = "No data founded"), //
            @ApiResponse(code = 400, message = "Invalidate data request"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> getStudentInClass(@RequestParam(required = false) Integer page,
                                               @RequestParam(required = false) Integer pageSize,
                                               @RequestParam(value = "teacherEmail", required = false) String teacherEmail,
                                               @RequestParam(value = "className", required = false) String className) {

        Pageable pageRequest = PageRequest.of(page != null ? page : 0, pageSize != null ? pageSize : 5);
        Page<TeacherClass> teacherClassPage = this.teacherClassService.getTeacherClassByTeacherEmailOrClassName(teacherEmail, className, pageRequest != null ? pageRequest : null);
        List<TeacherClass> teacherClassList = teacherClassPage.getContent();
        List<TeacherClassResponseDTO> teacherClassResponseDTOList = new ArrayList<>();
        for (TeacherClass teacherClass : teacherClassList) {
            TeacherClassResponseDTO teacherClassResponseDTO = new TeacherClassResponseDTO();
            teacherClassResponseDTO.setId(teacherClass.getId());
            teacherClassResponseDTO.setTeacherEmail(teacherClass.getAccount().getEmail());
            teacherClassResponseDTO.setClassName(teacherClass.getClassInstance().getClassName());
            teacherClassResponseDTO.setClassDTO(modelMapper.map(teacherClass.getClassInstance(), ClassDTO.class));
            teacherClassResponseDTO.setAccountDataDTO(modelMapper.map(teacherClass.getAccount(), AccountDataDTO.class));
            teacherClassResponseDTO.setIsTeaching(teacherClass.getIsTeaching());
            teacherClassResponseDTOList.add(teacherClassResponseDTO);
        }

        if (!teacherClassPage.hasContent()) {
            throw new CustomException("Not found data", HttpStatus.NO_CONTENT);
        } else {
            Map<String, Object> response = new HashMap<>();
            if (!teacherClassPage.isEmpty()) {
                response.put("data", teacherClassResponseDTOList);
                response.put("totalPages", teacherClassPage.getTotalPages());
                response.put("totalItems", teacherClassPage.getTotalElements());
                response.put("currentPage", teacherClassPage.getNumber());
                return ResponseEntity.ok(response);
            }
        }
        return null;
    }

    @RequestMapping(path = "/downloadTemplateAddTeacherToClass", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Download template file template to import to add teacher to an existing class")
    @ApiResponses(value = {//
            @ApiResponse(code = 204, message = "No data founded"), //
            @ApiResponse(code = 400, message = "Invalidate data request"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<Resource> getFile() {
        String filename = "teacher_to_class.xlsx";
        InputStreamResource file = new InputStreamResource(teacherClassExcelService.load(teacherClassExcelHelper));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @RequestMapping(value = "/uploadFileToAddTeachersToClasses", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Import file to add teachers to existing classes")
    @ApiResponses(value = {//
            @ApiResponse(code = 204, message = "No data founded"), //
            @ApiResponse(code = 400, message = "Invalidate data request"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<Resource> uploadFile(@RequestParam("file") MultipartFile file) {

        if (AccountExcelHelper.hasExcelFormat(file)) {
            try {
                InputStreamResource fileResult = new InputStreamResource(teacherClassExcelService.save(file));
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "teacher_class_import_result.xlsx")
                        .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                        .body(fileResult);
            } catch (Exception e) {
                throw new CustomException(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        throw new CustomException("Please upload an excel file", HttpStatus.BAD_REQUEST);
    }

}
