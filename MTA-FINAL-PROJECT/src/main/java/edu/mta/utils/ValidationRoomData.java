package edu.mta.utils;

public interface ValidationRoomData {

	String validateIdData(int id);
	
	String validateRoomNameData(String roomName);
	
	String validateAddressData(String address);
	
	String validateGpsLatitude(double gpsLa);
	
	String validateGPSLongitudeData(double gpsLong);
	
	//String validateMacAddressData(String macAddr);
}
