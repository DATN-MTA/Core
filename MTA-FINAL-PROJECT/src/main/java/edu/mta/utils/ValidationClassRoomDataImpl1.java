package edu.mta.utils;

import edu.mta.enumData.AfternoonTimeFrame;
import edu.mta.enumData.MorningTimeFrame;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@Qualifier("ValidationClassRoomDataImpl1")
public class ValidationClassRoomDataImpl1 implements ValidationClassRoomData {

	@Override
	public String validateIdData(int id) {
		if (id < 1) {
			return "Id can not be less than 1!";
		}
		return null;
	}

	/*
	 * @Override public String validateBeginDate(LocalDate beginAt) { // TODO
	 * Auto-generated method stub return null; }
	 */

	@Override
	public String validateFinishDate(LocalTime finishAt, LocalTime beginAt) {
		if (finishAt.isBefore(beginAt)) {
			return "finishAt cannot be before beginAt";
		}
		
		LocalTime finishMorning = LocalTime.of(11, 50);

		// finishAt must be in the same morning/afternoon with beginAt
		boolean flag = false;
		if (beginAt.isBefore(finishMorning)) {
			for (MorningTimeFrame instance : MorningTimeFrame.values()) {
				if (finishAt.equals(instance.getValue().plusMinutes(45))) {
					flag = true;
					break;
				}
			}

		} else {
			for (AfternoonTimeFrame instance : AfternoonTimeFrame.values()) {
				if (finishAt.equals(instance.getValue().plusMinutes(45))) {
					flag = true;
					break;
				}
			}
		}

		if (flag == false) {
			return "FinishAt value is not in valid list!" + "Morning: " + "07:00:00, 07:50:00, 08:40:00, 09:35:00, 10:25:00, 11:10:00" +
			" Afternoon: " + "12:30:00, 13:20:00, 14:10:00, 15:15:00, 16:05:00, 16:55:00" + " 45m";
		}

		return null;
	}

	@Override
	public String validateWeekday(int weekday) {
		if (weekday < 2 || weekday > 6) {
			return "weekday is out of valid range";
		}
		return null;
	}

	@Override
	public String validateBeginDate(LocalTime beginAt) {
		boolean flag = false;
		LocalTime finishMorning = LocalTime.of(11, 50);
		if (beginAt.isBefore(finishMorning)) {
			for (MorningTimeFrame instance : MorningTimeFrame.values()) {
				if (beginAt.equals(instance.getValue())) {
					flag = true;
					break;
				}
			}
		} else {
			for (AfternoonTimeFrame instance : AfternoonTimeFrame.values()) {
				if (beginAt.equals(instance.getValue())) {
					flag = true;
					break;
				}
			}
		}

		if (flag == false) {
			return "BeginAt value is not in valid list! " + "Morning: " + "07:00:00, 07:50:00, 08:40:00, 09:35:00, 10:25:00, 11:10:00" +
					" Afternoon: " + "12:30:00, 13:20:00, 14:10:00, 15:15:00, 16:05:00, 16:55:00" ;

		}
		return null;
	}

}
