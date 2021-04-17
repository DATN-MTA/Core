package edu.mta.enumData;

public enum IsTeaching {

	TEACHING(1), TAUGHT(2);
	
	private final int value;

	private IsTeaching(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
}
