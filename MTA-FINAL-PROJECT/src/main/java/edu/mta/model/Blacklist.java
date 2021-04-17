package edu.mta.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

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
@Table(name = "Blacklist")
public class Blacklist implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", length = 1)
	private int id;

	@Column(name = "CommitDate", nullable = false)
	private LocalDate commitDate;
	
	@Column(name = "CommitTime", nullable = false)
	private LocalTime commitTime;

	@Column(name = "FakeIMEI", nullable = true, length = 20)
	private String fakeImei;
	
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ClassID", nullable = false)
	private Class classInstance;
	
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "StudentID", nullable = false)
	private Account account;

	public Blacklist() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Blacklist(LocalDate commitDate, LocalTime commitTime, String fakeImei) {
		super();
		this.commitDate = commitDate;
		this.commitTime = commitTime;
		this.fakeImei = fakeImei;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDate getCommitDate() {
		return commitDate;
	}

	public void setCommitDate(LocalDate commitDate) {
		this.commitDate = commitDate;
	}

	public LocalTime getCommitTime() {
		return commitTime;
	}

	public void setCommitTime(LocalTime commitTime) {
		this.commitTime = commitTime;
	}

	public String getFakeImei() {
		return fakeImei;
	}

	public void setFakeImei(String fakeImei) {
		this.fakeImei = fakeImei;
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
}
