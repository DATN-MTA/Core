package edu.mta.model;

import java.io.Serializable;
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
@Table(name = "class_room")
public class ClassRoom implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;
	
	@Column(name = "BeginAt", nullable = false)
	private LocalTime beginAt;
	
	@Column(name = "FinishAt", nullable = false)
	private LocalTime finishAt;
	
	@Column(name = "Weekday", nullable = false)
	private int weekday;
	
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ClassID", nullable = false)
	private Class classInstance;
	
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RoomID", nullable = false)
	private Room room;

	public ClassRoom() {
		super();
	}

	public ClassRoom(int id, LocalTime beginAt, LocalTime finishAt, int weekday) {
		super();
		this.id = id;
		this.beginAt = beginAt;
		this.finishAt = finishAt;
		this.weekday = weekday;
	}

	public ClassRoom(LocalTime beginAt, LocalTime finishAt, int weekday) {
		super();
		this.beginAt = beginAt;
		this.finishAt = finishAt;
		this.weekday = weekday;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getWeekday() {
		return weekday;
	}

	public void setWeekday(int weekday) {
		this.weekday = weekday;
	}

	public LocalTime getBeginAt() {
		return beginAt;
	}

	public void setBeginAt(LocalTime beginAt) {
		this.beginAt = beginAt;
	}

	public LocalTime getFinishAt() {
		return finishAt;
	}

	public void setFinishAt(LocalTime finishAt) {
		this.finishAt = finishAt;
	}

	public Class getClassInstance() {
		return classInstance;
	}

	public void setClassInstance(Class classInstance) {
		this.classInstance = classInstance;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
	
}
