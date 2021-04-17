package edu.mta.utils;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ValidationUserDataImpl1")
public class ValidationUserDataImpl1 implements ValidationUserData{

	@Override
	public String validateIdData(int id) {
		if (id < 1) {
			return "Id can not be less than 1!";
		}
		return null;
	}

	@Override
	public String validateAddressData(String address) {
		if (address == null || address.isEmpty()) {
			return "Missing Address info!";
		}
		return null;
	}

	@Override
	public String validateBirthdayData(LocalDate birthday) {
		if (birthday == null || birthday.getYear() < 1940 ) {
			return "Birthday info is missing or not in valid range! ";
		}
		return null;
	}

	@Override
	public String validatePhoneData(String phone) {
		if (phone.length() < 9) {
			return "User's phone number is too short";
		} else if (!phone.matches("[0-9]+")) {
			return "User's phone must contain only digit";
		}
		
		return null;
	}

	@Override
	public String validateFullNameData(String fullName) {
		if (fullName == null || fullName.isEmpty()) {
			return "Missing FullName info!";
		}
		return null;
	}

	
}
