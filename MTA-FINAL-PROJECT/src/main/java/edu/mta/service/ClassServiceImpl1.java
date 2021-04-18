package edu.mta.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import edu.mta.model.Class;
import edu.mta.model.Semester;
import edu.mta.repository.ClassRepository;
import edu.mta.repository.SemesterRepository;

@Service
@Qualifier("ClassServiceImpl1")
public class ClassServiceImpl1 implements ClassService {

	private ClassRepository classRepository;
	private SemesterRepository semesterRepository;

	public ClassServiceImpl1() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Autowired
	public ClassServiceImpl1(ClassRepository classRepository, SemesterRepository semesterRepository) {
		super();
		this.classRepository = classRepository;
		this.semesterRepository = semesterRepository;
	}

	@Override
	public boolean addNewClass(Class classInstance) {
		this.classRepository.save(classInstance);
		return true;

	}

	@Override
	public Class findClassByID(int classID) {
		Optional<Class> classInstance = this.classRepository.findById(classID);
		return (classInstance.isPresent()) ? classInstance.get() : null;
	}

	@Override
	public boolean updateClassInfo(Class classInstance) {
		this.classRepository.save(classInstance);
		return true;
	}

	@Override
	public boolean deleteClassInfo(int classID) {
		try {
			this.classRepository.deleteById(classID);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean checkAddingTime(LocalDate addingDate, int semesterId) {
		// check if semesterId is valid
		Optional<Semester> semester = this.semesterRepository.findById(semesterId);
		if (semester.isEmpty()) {
			return false;
		}

		Semester instance = semester.get();
		if (addingDate.isAfter(instance.getBeginDate())) {
			return false;
		}
		return false;
	}

	@Override
	public Class findClassByClassName(String className) {
		Optional<Class> classInstance = this.classRepository.findByClassName(className);
		if (classInstance.isEmpty()) {
			return null;
		}
		return classInstance.get();
	}

	@Override
	public List<Class> getListClassByCourseID(int courseID) {
		List<Class> listClasses = this.classRepository.findByCourseID(courseID);
		if (listClasses == null || listClasses.isEmpty()) {
			return null;
		}
		return listClasses;
	}

	@Override
	public List<Class> getClassBySemesterName(String semesterName) {
		List<Class> listClasses;
		if (semesterName != null && !semesterName.isEmpty()) {
			listClasses = this.classRepository.findBySemesterName(semesterName);
		} else {
			listClasses = this.classRepository.findAll();
		}

		if (listClasses == null || listClasses.isEmpty()) {
			return null;
		}
		return listClasses;
	}

}
