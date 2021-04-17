package edu.mta.utils;

import java.time.LocalDate;

public interface ValidationUserData {

	String validateIdData(int id);
	
	String validateAddressData(String address);
	
	String validateBirthdayData(LocalDate birthday);
	
	String validatePhoneData(String phone);
	
	String validateFullNameData(String fullName);
}
