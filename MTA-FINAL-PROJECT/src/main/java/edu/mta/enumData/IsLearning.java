package edu.mta.enumData;

public enum IsLearning {

	LEARNING(1), DROP_OUT(2);
	
	private final int value;

	private IsLearning(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
}
