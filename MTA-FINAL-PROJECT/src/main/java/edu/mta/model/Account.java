package edu.mta.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "account")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Account implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", length = 1)
	private int id;

	@Column(name = "Username", nullable = false, length = 20)
	private String username;

	@Column(name = "Password", nullable = false, length = 20)
	private String password;

	@Column(name = "Role", nullable = false, length = 1)
	private int role;

	@Column(name = "IsActive", nullable = false, length = 1)
	private int isActive;

	@Column(name = "Email", nullable = false, length = 30, unique = true)
	private String email;

	@Column(name = "IMEI", nullable = true, length = 20)
	private String imei;
	
	@Column(name = "UpdateImeiCounter", length = 1)
	private int updateImeiCounter;
	
	@Column(name = "UserInfo", length = 200)
	private String userInfo;

	public Account(String password, String email) {
		super();
		this.password = password;
		this.email = email;
	}

	public Account(String username, String password, int role, String email) {
		super();
		this.username = username;
		this.password = password;
		this.role = role;
		this.email = email;
	}

	public Account(String username, String password, String email, String imei) {
		super();
		this.username = username;
		this.password = password;
		this.email = email;
		this.imei = imei;
	}

	public Account() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}

	public int getUpdateImeiCounter() {
		return updateImeiCounter;
	}

	public void setUpdateImeiCounter(int updateImeiCounter) {
		this.updateImeiCounter = updateImeiCounter;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
