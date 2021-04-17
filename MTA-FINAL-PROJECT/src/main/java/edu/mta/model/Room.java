package edu.mta.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import edu.mta.utils.GeneralValue;

@Entity
@Table(name = "room")
public class Room implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;
	
	@Column(name = "RoomName", nullable = false, unique = true)
	private String roomName;
	
	@Column(name = "Address", nullable = false, length = 60)
	private String address;
	
	@Column(name = "GPSLatitude", nullable = false)
	private double gpsLatitude;
	
	@Column(name = "GPSLongitude", nullable = false)
	private double gpsLongitude;
	
	//Mac address of wifi access point of this room
	@Column(name = "MacAddress", nullable = true)
	private String macAddress;

	public Room() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Room(String name, String address, double gpsLatitude, double gpsLongitude, String macAddress) {
		super();
		this.roomName = name;
		this.address = address;
		
		if (GeneralValue.minLatitude <= gpsLatitude && gpsLatitude <= GeneralValue.maxLatitude) {
			this.gpsLatitude = gpsLatitude;
		} else {
			this.gpsLatitude = 200;		//not a valid value
		}
		
		if (GeneralValue.minLongitude <= gpsLongitude && gpsLongitude <= GeneralValue.maxLongitude) {
			this.gpsLongitude = gpsLongitude;
		} else {
			this.gpsLongitude = 200;	//not a valid value
		}
		
		this.macAddress = macAddress;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getGpsLatitude() {
		return gpsLatitude;
	}

	public void setGpsLatitude(double gpsLatitude) {
		if (GeneralValue.minLatitude <= gpsLatitude && gpsLatitude <= GeneralValue.maxLatitude) {
			this.gpsLatitude = gpsLatitude;
		} else {
			this.gpsLatitude = 200;		//not a valid value
		}
	}

	public double getGpsLongitude() {
		return gpsLongitude;
	}

	public void setGpsLongitude(double gpsLongitude) {
		if (GeneralValue.minLongitude <= gpsLongitude && gpsLongitude <= GeneralValue.maxLongitude) {
			this.gpsLongitude = gpsLongitude;
		} else {
			this.gpsLongitude = 200;	//not a valid value
		}
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
}
