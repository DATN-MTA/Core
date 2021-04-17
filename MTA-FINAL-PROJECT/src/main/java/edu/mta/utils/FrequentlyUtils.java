package edu.mta.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

public interface FrequentlyUtils {

	/**
	 * @param jsonMap - a Map contains some keys
	 * @param args - list of keys that need check
	 * @return true if the map contains all keys; 
	 */
	boolean checkKeysExist(Map<String, Object> jsonMap, String... args);
	
	String makeRollcallRecord(LocalDateTime rollcallAt);
	
	boolean checkTwoTimeConflict(LocalTime begin1, LocalTime begin2, LocalTime finish1, LocalTime finish2);
	
	boolean checkTwoDateConflict(LocalDate begin1, LocalDate begin2, LocalDate finish1, LocalDate finish2);
}
