package edu.mta.utils;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ValidationRoomDataImpl1")
public class ValidationRoomDataImpl1 implements ValidationRoomData{

	@Override
	public String validateIdData(int id) {
		if (id < 1) {
			return "Id can not be less than 1!";
		}
		return null;
	}

	@Override
	public String validateRoomNameData(String roomName) {
		if (roomName == null || roomName.isBlank()) {
			return "Missing roomName data!";
		}
		return null;
	}

	@Override
	public String validateAddressData(String address) {
		if (address == null || address.isBlank()) {
			return "Missing address data!";
		}
		return null;
	}

	@Override
	public String validateGpsLatitude(double gpsLa) {
		if (gpsLa < GeneralValue.minLatitude || gpsLa > GeneralValue.maxLatitude) {
			return "GPS Latitude is out of valid range";
		}
		return null;
	}

	@Override
	public String validateGPSLongitudeData(double gpsLong) {
		if (gpsLong < GeneralValue.minLongitude || gpsLong > GeneralValue.maxLongitude) {
			return "GPS Longitude is out of valid range";
		}
		return null;
	}

	/*
	 * @Override public String validateMacAddressData(String macAddr) { String regex
	 * = "[0-9][0-9]:[0-9][0-9]:[0-9][0-9]:[0-9][0-9]"; if (!macAddr.matches(regex))
	 * { return "MAC address is in wrong format"; } return null; }
	 */

}
