package edu.mta.enumData;

import java.time.LocalTime;

public enum MorningTimeFrame {
	FRAME1("06:45:00"), FRAME2("07:35:00"), FRAME3("08:20:00"), FRAME4("09:20:00"), FRAME5("10:15:00"), FRAME6("11:05:00");

	private final LocalTime value;
	
	private MorningTimeFrame(String timeToString) {
		this.value = LocalTime.parse(timeToString);
	}
	
	public LocalTime getValue() {
		return this.value;
	}
}
