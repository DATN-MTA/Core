package edu.mta.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

public interface CustomClassRepository {
	@Modifying
	void setNullIdentifyString(@Param("classID") int id, @Param("eventName") String eventName, int classRoomID);
	
	@Modifying
	void setIsCheckFalse(int classID, String finishTime, String eventDynamicName);
}
