package edu.mta.enumData;

import java.time.LocalTime;

public enum AfternoonTimeFrame {

	FRAME7("12:30:00"), FRAME8("13:20:00"), FRAME9("14:10:00"), FRAME10("15:15:00"), FRAME11("16:05:00"), FRAME12("16:55:00");

	private final LocalTime value;
	
	private AfternoonTimeFrame(String timeToString) {
		this.value = LocalTime.parse(timeToString);
	}
	
	public LocalTime getValue() {
		return this.value;
	}
}
