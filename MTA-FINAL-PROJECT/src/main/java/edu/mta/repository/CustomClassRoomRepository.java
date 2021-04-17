
package edu.mta.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import edu.mta.model.ClassRoom;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomClassRoomRepository {

	@Query("SELECT cr FROM ClassRoom cr WHERE cr.classInstance.id = ?1 AND cr.room.id = ?2 AND cr.weekday = ?3 AND (CAST(?4 AS time) BETWEEN cr.beginAt AND cr.finishAt)")
	Optional<ClassRoom> findByClassIDAndRoomIDAndWeekday(int classID, int roomID, int weekday, LocalTime checkTime);
	
	@Query("SELECT cr FROM ClassRoom cr WHERE cr.classInstance.id IN :ids")
	List<ClassRoom> getListClassRoom(@Param("ids") List<Integer> listClassID);
	
	@Query("SELECT cr FROM ClassRoom cr WHERE cr.classInstance.id = ?1 AND cr.room.id = ?2")
	List<ClassRoom> findByClassIDAndRoomID(int classID, int roomID);
}
