package edu.mta.enumData;

public enum AccountStatus {
	
	INACTIVE(1), ACTIVE(2), DISABLE(3);
	
	private final int value;

	private AccountStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
}
