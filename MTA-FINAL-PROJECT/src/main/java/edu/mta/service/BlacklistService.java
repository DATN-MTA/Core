package edu.mta.service;

import edu.mta.model.StudentClass;

public interface BlacklistService {

	void createNewRecord(StudentClass studentClass, String fakeImei);

}
