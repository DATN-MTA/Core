package edu.mta.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import edu.mta.model.Class;
import edu.mta.model.Semester;
import edu.mta.repository.SemesterRepository;
import edu.mta.utils.FrequentlyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import edu.mta.repository.ClassRepository;

@Service
@Qualifier("SemesterServiceImpl1")
public class SemesterServiceImpl1 implements SemesterService {

	private SemesterRepository semesterRepository;
	private ClassRepository classRepository;
	private FrequentlyUtils frequentlyUtils;

	public SemesterServiceImpl1() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Autowired
	public SemesterServiceImpl1(
			SemesterRepository semesterRepository, 
			ClassRepository classRepository,
			@Qualifier("FrequentlyUtilsImpl1") FrequentlyUtils frequentlyUtils) {
		super();
		this.semesterRepository = semesterRepository;
		this.classRepository = classRepository;
		this.frequentlyUtils = frequentlyUtils;
	}
	
	@Override
	public int getSemesterIDByDate(LocalDate currentDate) {
		Optional<Semester> semester = this.semesterRepository.getSemesterIDByTime(currentDate);
		if (semester.isPresent()) {
			return semester.get().getSemesterID();
		}
		return -1;
	}

	@Override
	public Semester findSemesterByName(String semesterName) {
		Optional<Semester> semester = this.semesterRepository.findBySemesterName(semesterName);
		if (semester.isPresent()) {
			return semester.get();
		}
		return null;
	}

	@Override
	public void addNewSemester(Semester semester) {
		this.semesterRepository.save(semester);
		return;
	}

	@Override
	public boolean checkSemesterTimeDuplicate(LocalDate beginDate, LocalDate endDate, String semesterName) {
		List<Semester> listSemester = this.semesterRepository.findAll();
		System.out.println("list semester = " + listSemester);
		if (listSemester == null || listSemester.isEmpty()) {
			// no conflict can happen
			return true;
		}
		
		//check if semester have conflict with another semester
		for (Semester semester: listSemester) {
			//you must exclude the semester itself
			if (semester.getSemesterName().equalsIgnoreCase(semesterName)) {
				continue;
			}
			if (!this.frequentlyUtils.checkTwoDateConflict(beginDate, 
					semester.getBeginDate(), endDate, semester.getEndDate())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean checkSemesterDependant(String semesterName) {
		List<Class> listClass = this.classRepository.findBySemesterName(semesterName);
		if (listClass == null || listClass.isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public void deleteSemester(String semesterName) {
		this.semesterRepository.deleteBySemesterName(semesterName);
		return;
	}

	@Override
	public Semester findSemesterById(int id) {
		Optional<Semester> semester = this.semesterRepository.findById(id);
		if (semester.isPresent()) {
			return semester.get();
		}
		return null;
	}

	@Override
	public void updateSemesterInfo(Semester semester) {
		this.semesterRepository.save(semester);
		return;
		
	}

	@Override
	public List<Semester> findAllSemester() {
		List<Semester> listSemester = this.semesterRepository.findAll();
		if (listSemester == null || listSemester.isEmpty()) {
			return null;
		}
		return listSemester;
	}

	@Override
    public Page<Semester> findAllSemester(Pageable pageable) {
        Page<Semester> pageSemester = this.semesterRepository.findAll(pageable);
        if (pageSemester == null || pageSemester.isEmpty()) {
            return null;
        }
        return pageSemester;
    }

}
