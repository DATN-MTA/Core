package edu.mta.enumData;

public enum SpecialRollCall {

	SICK(1), FORGOT_PHONE(2);
	
	private final int value;

	private SpecialRollCall(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
}
