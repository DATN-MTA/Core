
package edu.mta.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import edu.mta.model.ClassRoom;
import edu.mta.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRoomRepository extends JpaRepository<ClassRoom, Integer> {

	@Query("SELECT cr FROM ClassRoom cr WHERE cr.weekday = ?2 AND cr.classInstance.id = ?1 AND ((?3 BETWEEN cr.beginAt AND cr.finishAt)"
			+ " OR (?4 BETWEEN cr.beginAt AND cr.finishAt) OR (?3 < cr.beginAt AND cr.finishAt > ?4) OR (?3 > cr.beginAt AND cr.finishAt < ?4))")
	List<ClassRoom> findClassesByIdAndWeekdayAndDuration(int classID, int weekday, LocalTime beginAt, LocalTime finishAt);

	@Query("SELECT cr FROM ClassRoom cr WHERE cr.weekday = ?2 AND cr.room.id = ?1 AND ((?3 BETWEEN cr.beginAt AND cr.finishAt)"
			+ " OR (?4 BETWEEN cr.beginAt AND cr.finishAt) OR (?3 < cr.beginAt AND cr.finishAt > ?4) OR (?3 > cr.beginAt AND cr.finishAt < ?4))")
	List<ClassRoom> findRoomByIdAndWeekdayAndDuration(int roomID, int weekday, LocalTime beginAt, LocalTime finishAt);

	List<ClassRoom> findByWeekday(int weekday);

	@Query("SELECT cr FROM ClassRoom cr WHERE cr.classInstance.id = ?1")
	List<ClassRoom> findByClassID(int classID);

	@Query("SELECT DISTINCT cr.room FROM ClassRoom cr WHERE cr.classInstance.id = ?1")
	List<Room> findListRoomByClassID(int classID);
	
	@Query("SELECT cr FROM ClassRoom cr WHERE cr.classInstance.id = ?1 AND cr.room.id = ?2 AND cr.weekday = ?3 AND (CAST(?4 AS time) BETWEEN cr.beginAt AND cr.finishAt)")
	Optional<ClassRoom> findByClassIDAndRoomIDAndWeekday(int classID, int roomID, int weekday, LocalTime checkTime);
	
	@Query("SELECT cr FROM ClassRoom cr WHERE cr.classInstance.id IN :ids")
	List<ClassRoom> getListClassRoom(@Param("ids") List<Integer> listClassID);
	
	@Query("SELECT cr FROM ClassRoom cr WHERE cr.classInstance.id = ?1 AND cr.room.id = ?2")
	List<ClassRoom> findByClassIDAndRoomID(int classID, int roomID);
	
	@Query("SELECT cr FROM ClassRoom cr WHERE cr.room.id = ?1")
	List<ClassRoom> findAllByRoomID(int roomID);

}
