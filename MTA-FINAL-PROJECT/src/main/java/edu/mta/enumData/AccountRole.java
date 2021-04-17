package edu.mta.enumData;

public enum AccountRole {
	ADMIN(1), TEACHER(2), STUDENT(3);
	
	private final int value;

	private AccountRole(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
}
