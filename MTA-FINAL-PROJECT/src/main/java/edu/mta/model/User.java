package edu.mta.model;

import java.time.LocalDate;

public class User {

	private int id;
	private String address;
	private String fullName;
	private LocalDate birthDay;
	private String phone;
	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public User(int id, String address, String fullName, LocalDate birthDay, String phone) {
		super();
		this.id = id;
		this.address = address;
		this.fullName = fullName;
		this.birthDay = birthDay;
		this.phone = phone;
	}

	public User(String address, String fullName, LocalDate birthDay, String phone) {
		super();
		this.address = address;
		this.fullName = fullName;
		this.birthDay = birthDay;
		this.phone = phone;
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

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public LocalDate getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(LocalDate birthDay) {
		this.birthDay = birthDay;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
}
