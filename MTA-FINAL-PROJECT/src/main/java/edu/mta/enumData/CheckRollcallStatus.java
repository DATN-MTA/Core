package edu.mta.enumData;

public enum CheckRollcallStatus {

	CHECKED(1), NOT_CHECK(2);
	
	private final int value;

	private CheckRollcallStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
}
