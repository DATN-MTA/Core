package edu.mta.service;

import edu.mta.dto.ClassRoomRequestDTO;
import edu.mta.model.ClassRoom;
import edu.mta.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.util.List;

public interface ClassRoomService {

	String addNewClassRoom(ClassRoomRequestDTO classRoomRequestDTO);

	List<ClassRoom> getListClassRoom(int classID, int roomID);
	
	ClassRoom getInfoClassRoom(int classID, int roomID, int weekday, LocalTime checkTime);

	//boolean updateClassRoomInfo(int roomID, int classID, ClassRoom classRoom);

	List<ClassRoom> checkClassAvailable(int classID, int weekday, LocalTime beginAt, LocalTime finishAt);

	List<ClassRoom> checkRoomAvailable(int roomID, int weekday, LocalTime beginAt, LocalTime finishAt);

	ClassRoom findClassRoomByID(int id);

	void updateClassRoomInfo(ClassRoom classRoom);

	boolean deleteClassRoom(ClassRoom classRoom);

	List<ClassRoom> findClassRoomByWeekday(int currentDay);

	List<ClassRoom> getListClassRoomByClassID(int classID);

	List<Room> getListRoomByClassID(int classID);

	List<ClassRoom> checkListClassRoom(List<ClassRoom> listClassRoom, int roomID);

	boolean addNewClassRoom(ClassRoom classRoom, int roomID);

	Page<ClassRoom> getClassRoomBySemesterAndCourseAndClass (Integer courseId, Integer classId, Integer roomId, Pageable pageable);

	
}
