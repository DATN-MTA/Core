package edu.mta.enumData;

import java.time.LocalTime;

public enum MorningTimeFrame {
	FRAME1("07:00:00"), FRAME2("07:50:00"), FRAME3("08:40:00"), FRAME4("09:35:00"), FRAME5("10:25:00"), FRAME6("11:10:00");

	private final LocalTime value;
	
	private MorningTimeFrame(String timeToString) {
		this.value = LocalTime.parse(timeToString);
	}
	
	public LocalTime getValue() {
		return this.value;
	}
}
