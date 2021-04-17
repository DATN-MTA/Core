package edu.mta.service;

import java.time.LocalDate;
import java.time.LocalTime;

import edu.mta.model.Blacklist;
import edu.mta.model.StudentClass;
import edu.mta.repository.BlacklistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("BlacklistServiceImpl1")
public class BlacklistServiceImpl1 implements BlacklistService {

	private BlacklistRepository blacklistRepository;
	//private AccountService accountService;
	//private ClassService classService;

	public BlacklistServiceImpl1() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * @Autowired public BlacklistServiceImpl1(BlacklistRepository
	 * blacklistRepository,
	 * 
	 * @Qualifier("AccountServiceImpl1") AccountService accountService,
	 * 
	 * @Qualifier("ClassServiceImpl1") ClassService classService) { super();
	 * this.blacklistRepository = blacklistRepository; this.accountService =
	 * accountService; this.classService = classService; }
	 */
	
	@Autowired
	public BlacklistServiceImpl1(BlacklistRepository blacklistRepository) {
		super();
		this.blacklistRepository = blacklistRepository;
	}
	

	@Override
	public void createNewRecord(StudentClass studentClass, String fakeImei) {
		LocalDate commitDate = LocalDate.now();
		LocalTime commitTime = LocalTime.now();
		Blacklist record = new Blacklist(commitDate, commitTime, fakeImei);

		record.setAccount(studentClass.getAccount());
		record.setClassInstance(studentClass.getClassInstance());
		
		this.blacklistRepository.save(record);
	}

}
