package edu.mta.utils;

public interface ValidationAccountData {

	String validateEmailData(String email);
	
	String validatePasswordData(String password);
	
	String validateUsernameData(String username);
	
	String validateRoleData(int role);
	
	String validateIdData(int id);
	
	String validateImeiData(String imei);
	
}
