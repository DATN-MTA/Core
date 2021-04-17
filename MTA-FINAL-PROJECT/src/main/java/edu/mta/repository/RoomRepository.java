package edu.mta.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.mta.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer>{

	List<Room> findAll();

	boolean findByAddress(String address);

	Optional<Room> findByIdAndMacAddress(int roomID, String macAddress);

	Optional<Room> findByRoomName(String roomName);

	Optional<Room> findByMacAddress(String macAddress);
	
}
