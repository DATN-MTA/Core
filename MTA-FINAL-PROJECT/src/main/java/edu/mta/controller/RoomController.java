
package edu.mta.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mta.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.mta.model.ReportError;
import edu.mta.model.Room;
import edu.mta.service.RoomService;
import edu.mta.utils.ValidationData;
import edu.mta.utils.ValidationRoomData;

@CrossOrigin
@RestController
public class RoomController {

	private RoomService roomService;
	private ValidationData validationData;
	private ValidationRoomData validationRoomData;

	@Autowired
	public RoomController(@Qualifier("RoomServiceImpl1") RoomService roomService,
			@Qualifier("ValidationDataImpl1") ValidationData validationData, 
			@Qualifier("ValidationRoomDataImpl1") ValidationRoomData validationRoomData) {
		this.roomService = roomService;
		this.validationData = validationData;
		this.validationRoomData = validationRoomData;
	}

	@RequestMapping(value = "/rooms", method = RequestMethod.POST)
	public ResponseEntity<?> addNewRoom(@RequestBody String roomInfo) {
		String errorMessage = null;
		ObjectMapper objectMapper = null;
		Map<String, Object> jsonMap = null;
		Room room = null;
		ReportError report = null;
		
		try {
			objectMapper = new ObjectMapper();
			room = objectMapper.readValue(roomInfo, Room.class);
			jsonMap = new HashMap<>();
			
			jsonMap.put("roomName", room.getRoomName());
			jsonMap.put("address", room.getAddress());
			jsonMap.put("gpsLa", room.getGpsLatitude());
			jsonMap.put("gpsLong", room.getGpsLongitude());
			//jsonMap.put("macAddress", room.getMacAddress());
			
			errorMessage = this.validationData.validateRoomData(jsonMap);
			if (errorMessage != null) {
				report = new ReportError(50, "Adding room failed because " + errorMessage);
				return ResponseEntity.badRequest().body(report);
			}
			
			if (!this.roomService.checkRoomNameDuplicate(room.getRoomName())) {
				report = new ReportError(51, "Duplicate room name!");
				return ResponseEntity.badRequest().body(report);
			}
			
//			if (!this.roomService.checkMacAddrDuplicate(room.getMacAddress())) {
//				report = new ReportError(52, "Duplicate MAC address!");
//				return ResponseEntity.badRequest().body(report);
//			}
			
			this.roomService.addNewRoom(room);
			report = new ReportError(200, "Adding new room success!");
			return ResponseEntity.ok(report);
			
		} catch (IOException e) {
			e.printStackTrace();
			report = new ReportError(2, "Error happened when jackson deserialization info!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, report.toString());
		}
	}

	@RequestMapping(value = "/rooms", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteRoom(@RequestParam(value = "roomID", required = true) int id) {
		String errorMessage = this.validationRoomData.validateIdData(id);
		ReportError report = null;
		if (errorMessage != null) {
			report = new ReportError(54, "Deleting room failed because " + errorMessage);
			return ResponseEntity.badRequest().body(report);
		}
		
		Room room = this.roomService.findRoomById(id);
		if (room == null) {
			report = new ReportError(53, "This room do not exist!");
			return new ResponseEntity<>(report, HttpStatus.NOT_FOUND);
		}
		
		if (this.roomService.deleteRoom(id)) {
			report = new ReportError(200, "Deleting room success!");
			return ResponseEntity.ok(report);
		}
		
		report = new ReportError(55, "This room still has dependant!");
		return ResponseEntity.badRequest().body(report);
	}

	@RequestMapping(value = "/rooms", method = RequestMethod.GET)
	public ResponseEntity<?> readInfoRoom(@RequestParam(value = "roomID", required = true) int id) {
		String errorMessage = this.validationRoomData.validateIdData(id);
		ReportError report = null;
		if (errorMessage != null) {
			report = new ReportError(57, "Getting room failed because " + errorMessage);
			return ResponseEntity.badRequest().body(report);
		}
		
		Room room = this.roomService.findRoomById(id);
		if (room == null) {
			report = new ReportError(53, "This room do not exist!");
			return new ResponseEntity<>(report, HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok(room);
	}
	
	@RequestMapping(value = "/rooms/all", method = RequestMethod.GET)
	public ResponseEntity<?> getAllRooms(@RequestParam(required = false) Integer page,
										 @RequestParam(required = false) Integer pageSize) {
		Pageable pageRequest = PageRequest.of(page != null ? page : 0, pageSize != null ? pageSize : 5);
		Page<Room> pageRooms = this.roomService.findAllRooms(pageRequest != null ? pageRequest : null);

		if (pageRooms == null) {
			return ResponseEntity.badRequest().body("No data founded!");
		} else {
			Map<String, Object> response = new HashMap<>();
			if (pageRooms != null) {
				response.put("data", pageRooms.getContent());
				response.put("totalPages", pageRooms.getTotalPages());
				response.put("totalItems", pageRooms.getTotalElements());
				response.put("currentPage", pageRooms.getNumber());
				return ResponseEntity.ok(response);
			}
		}
		return null;
	}

	@RequestMapping(value = "/rooms", method = RequestMethod.PUT)
	public ResponseEntity<?> updateInfoRoom(@RequestBody String infoRoom) {
		String errorMessage = null;
		ObjectMapper objectMapper = null;
		Map<String, Object> jsonMap = null;
		Room room = null;
		Room tmpRoom = null;
		ReportError report = null;
		
		try {
			objectMapper = new ObjectMapper();
			room = objectMapper.readValue(infoRoom, Room.class);
			jsonMap = new HashMap<>();
			
			jsonMap.put("id", room.getId());
			jsonMap.put("roomName", room.getRoomName());
			jsonMap.put("address", room.getAddress());
			jsonMap.put("gpsLa", room.getGpsLatitude());
			jsonMap.put("gpsLong", room.getGpsLongitude());
			jsonMap.put("macAddress", room.getMacAddress());
			
			errorMessage = this.validationData.validateRoomData(jsonMap);
			if (errorMessage != null) {
				report = new ReportError(56, "Updating room failed because " + errorMessage);
				return ResponseEntity.badRequest().body(report);
			}
			
			if (this.roomService.findRoomById(room.getId()) == null) {
				report = new ReportError(53, "This room do not exist!");
				return new ResponseEntity<>(report, HttpStatus.NOT_FOUND);
			}
			
			//Check if room name is duplicate (must exclude itself)
			tmpRoom = this.roomService.findRoomByName(room.getRoomName());
			if (tmpRoom != null && room.getId() != tmpRoom.getId()) {
				report = new ReportError(51, "Duplicate room name!");
				return ResponseEntity.badRequest().body(report);
			}
			
			//Check if Mac Address is duplicate (must exclude itself)
			tmpRoom = this.roomService.findRoomByMacAddr(room.getMacAddress());
			if (tmpRoom != null && tmpRoom.getId() != room.getId()) {
				report = new ReportError(52, "Duplicate MAC address!");
				return ResponseEntity.badRequest().body(report);
			}
			
			this.roomService.updateRoom(room);
			report = new ReportError(200, "Updating room success!");
			return ResponseEntity.ok(report);
		} catch (Exception e) {
			e.printStackTrace();
			report = new ReportError(2, "Error happened when jackson deserialization info!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, report.toString());
		}
	}
	
	@RequestMapping(value = "/getRoomInfo", method = RequestMethod.GET)
	public ResponseEntity<?> getRoomInfo(@RequestParam(value = "roomName", required = true) String roomName) {
		String errorMessage = this.validationRoomData.validateRoomNameData(roomName);
		ReportError report = null;
		if (errorMessage != null) {
			report = new ReportError(57, "Getting room info failed because " + errorMessage);
			return ResponseEntity.badRequest().body(report);
		}
		
		Room room = this.roomService.findRoomByName(roomName);
		if (room == null) {
			report = new ReportError(53, "This room do not exist!");
			return new ResponseEntity<>(report, HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok(room);
	}
}
