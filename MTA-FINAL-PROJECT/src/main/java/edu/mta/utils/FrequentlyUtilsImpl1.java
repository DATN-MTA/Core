package edu.mta.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("FrequentlyUtilsImpl1")
public class FrequentlyUtilsImpl1 implements FrequentlyUtils {

	@Override
	public boolean checkKeysExist(Map<String, Object> jsonMap, String... args) {
		List<String> listKeys = new ArrayList<>();
		for (String arg : args) {
			listKeys.add(arg);
		}

		if (!jsonMap.keySet().containsAll(listKeys)) {
			return false;
		}
		return true;
	}

	@Override
	public String makeRollcallRecord(LocalDateTime rollcallAt) {
		String newValue = "" + rollcallAt.getYear();
		newValue += GeneralValue.regexForSplitDate + rollcallAt.getDayOfYear();
		newValue += GeneralValue.regexForSplitDate + rollcallAt.toLocalTime().toSecondOfDay();

		return newValue;
	}

	@Override
	public boolean checkTwoTimeConflict(LocalTime begin1, LocalTime begin2, LocalTime finish1, LocalTime finish2) {
		// check if 2 duration are partly overlapped
		if (begin1.isAfter(begin2) && begin1.isBefore(finish2)) {
			System.out.println("============ Mile 1");
			return false;
		}

		if (finish1.isAfter(begin2) && finish1.isBefore(finish2)) {
			System.out.println("============ Mile 2");
			return false;
		}

		// check if 1 duration is totally overlapped by the other
		if (begin1.isBefore(begin2) && finish1.isAfter(finish2)) {
			System.out.println("============ Mile 3");
			return false;
		}

		if (begin2.isBefore(begin1) && finish2.isAfter(finish1)) {
			System.out.println("============ Mile 4");
			return false;
		}

		return true;
	}

	@Override
	public boolean checkTwoDateConflict(LocalDate begin1, LocalDate begin2, LocalDate finish1, LocalDate finish2) {
		// check if 2 duration are partly overlapped
		if (begin1.isAfter(begin2) && begin1.isBefore(finish2)) {
			return false;
		}

		if (finish1.isAfter(begin2) && finish1.isBefore(finish2)) {
			return false;
		}

		// check if 1 duration is totally overlapped by the other
		if (begin1.isBefore(begin2) && finish1.isAfter(finish2)) {
			return false;
		}

		if (begin2.isBefore(begin1) && finish2.isAfter(finish1)) {
			return false;
		}

		return true;
	}

}
