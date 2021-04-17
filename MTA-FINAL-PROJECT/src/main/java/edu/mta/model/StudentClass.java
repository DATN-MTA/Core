package edu.mta.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "student_class")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class StudentClass implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", length = 1)
	private int id;
	
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ClassID", nullable = false)
	private Class classInstance;
	
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "StudentID", nullable = false)
	private Account account;
	
	@Column(name = "IsLearning", length = 1, nullable = false)
	private int isLearning;
	
	@Column(name = "IsChecked", nullable = true, length = 16)
	private String isChecked;
	
	@Column(name = "listRollCall", length = 500)
	private String listRollCall;

	public StudentClass() {
		super();
		this.listRollCall = "";
	}

	public StudentClass(int isLearning) {
		super();
		this.isLearning = isLearning;
	}

	public StudentClass(int isLearning, String listRollCall) {
		super();
		this.isLearning = isLearning;
		this.listRollCall = listRollCall;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Class getClassInstance() {
		return classInstance;
	}

	public void setClassInstance(Class classInstance) {
		this.classInstance = classInstance;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public int getIsLearning() {
		return isLearning;
	}

	public void setIsLearning(int isLearning) {
		this.isLearning = isLearning;
	}

	public String getListRollCall() {
		return listRollCall;
	}

	public void setListRollCall(String listRollCall) {
		this.listRollCall = listRollCall;
	}

	public String getIsChecked() {
		return isChecked;
	}

	public void setIsChecked(String isChecked) {
		this.isChecked = isChecked;
	}
}
