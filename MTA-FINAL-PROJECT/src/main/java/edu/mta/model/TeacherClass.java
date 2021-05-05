package edu.mta.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;

@Entity

@Table(name = "teacher_class", uniqueConstraints=
@UniqueConstraint(columnNames={"TeacherID", "ClassID"}))
public class TeacherClass implements Serializable {
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
	@JoinColumn(name = "TeacherID", nullable = false)
	private Account account;
	
	@Column(name = "ListRollCall", length = 500)
	private String listRollCall;
	
	@Column(name = "IsTeaching", length = 1, nullable = false)
	private int isTeaching;

	public TeacherClass() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TeacherClass(String listRollCall, int isTeaching) {
		super();
		this.listRollCall = listRollCall;
		this.isTeaching = isTeaching;
	}

	public TeacherClass(Class classInstance, Account account) {
		super();
		this.classInstance = classInstance;
		this.account = account;
	}

	public TeacherClass(int isTeaching) {
		super();
		this.isTeaching = isTeaching;
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

	public int getIsTeaching() {
		return isTeaching;
	}

	public void setIsTeaching(int isTeaching) {
		this.isTeaching = isTeaching;
	}

	public String getListRollCall() {
		return listRollCall;
	}

	public void setListRollCall(String listRollCall) {
		this.listRollCall = listRollCall;
	}
}
