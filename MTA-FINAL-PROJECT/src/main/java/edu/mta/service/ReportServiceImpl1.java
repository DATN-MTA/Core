package edu.mta.service;

import edu.mta.enumData.IsLearning;
import edu.mta.enumData.IsTeaching;
import edu.mta.external.StudentClassDAL;
import edu.mta.model.*;
import edu.mta.utils.GeneralValue;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Service
public class ReportServiceImpl1 {
	private StudentClassDAL studentClassDAL = new StudentClassDAL();

	public ReportServiceImpl1() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<GeneralStudentRecord> getArrayOfGeneralStudentRecord(String studentEmail, String semesterID,
			String beginAtString, String finishAtString) {
		List<StudentClass> listStudentClass = this.studentClassDAL.getListClass(studentEmail,
				Integer.parseInt(semesterID), IsLearning.LEARNING.getValue());
		if (listStudentClass == null || listStudentClass.isEmpty()) {
			return null;
		}

		System.out.println("\n\n list studentClass = " + listStudentClass);

		LocalDate beginAt = LocalDate.parse(beginAtString);
		LocalDate finishAt = LocalDate.parse(finishAtString);
		List<GeneralStudentRecord> listRecord = new ArrayList<>();
		GeneralStudentRecord studentRecord = null;
		String listRollCall = null;
		String[] listDates1 = null;
		List<String> listDate2 = new ArrayList<>();
		ListIterator<String> listIterator = null;
		String tmpDayOfYearString = null;
		int tmpDayOfYear = -1;
		int tmpYear = -1;
		int indexOfYear = -1;

		int beginYear = beginAt.getYear();
		int beginDayOfYear = beginAt.getDayOfYear();
		int finishYear = finishAt.getYear();
		int finishDayOfYear = finishAt.getDayOfYear();

		int sumOfLesson = -1;
		int sumOfAbsent = -1;
		int sumOfFogettingPhone = -1;
		int sumOfSick = -1;

		for (StudentClass studentClass : listStudentClass) {
			System.out.println("\n\nBegin filet =========================");
			System.out.println("\n\n sumOfLesson = " + sumOfLesson);
			sumOfLesson = 0;
			sumOfAbsent = 0;
			sumOfFogettingPhone = 0;
			sumOfSick = 0;

			listRollCall = studentClass.getListRollCall();
			if (listRollCall == null || listRollCall.isEmpty()) {
				studentRecord = new GeneralStudentRecord();
				studentRecord.setClassName(studentClass.getClassInstance().getClassName());
				studentRecord.setCourseName(studentClass.getClassInstance().getCourse().getCourseName());
				studentRecord.setSumOfAbsent(0);
				studentRecord.setSumOfFogettingPhone(0);
				studentRecord.setSumOfSick(0);
				studentRecord.setSumOfLessons(0);

				listRecord.add(studentRecord);
				continue;
			}
			indexOfYear = listRollCall.indexOf("" + beginYear);

			// indexOfYear should never be -1
			if (indexOfYear == -1) {
				studentRecord = new GeneralStudentRecord();
				studentRecord.setClassName(studentClass.getClassInstance().getClassName());
				studentRecord.setCourseName(studentClass.getClassInstance().getCourse().getCourseName());
				studentRecord.setSumOfAbsent(0);
				studentRecord.setSumOfFogettingPhone(0);
				studentRecord.setSumOfSick(0);
				studentRecord.setSumOfLessons(0);

				listRecord.add(studentRecord);
				continue;
			}
			listRollCall = listRollCall.substring(indexOfYear);

			if (beginYear != finishYear) {
				System.out.println("\n\n beginYear != finishYear");
				indexOfYear = listRollCall.indexOf("" + (finishYear + 1));
				if (indexOfYear != -1) {
					listRollCall = listRollCall.substring(0, indexOfYear);
				}
			}

			listDates1 = listRollCall.split(GeneralValue.regexForSplitListRollCall);

			// format of a date: xxxx (year) - x(?) (day of year) - xxxxx(seconds in day)
			// take the head of listRollCall (cut all elements that is before beginDate)
			for (int i = 0; i < listDates1.length; i++) {
				if (listDates1[i].contains(GeneralValue.markForMissingRollCall)
						|| listDates1[i].contains(GeneralValue.markForTeacherMissing)) {
					tmpDayOfYearString = listDates1[i].substring(5, listDates1[i].length() - 2);
				} else if (listDates1[i].contains(GeneralValue.markForPermission)
						|| listDates1[i].contains(GeneralValue.markForNotBringPhone)) {
					tmpDayOfYearString = listDates1[i].substring(5, listDates1[i].length() - 7);
				} else {
					tmpDayOfYearString = listDates1[i].substring(5, listDates1[i].length() - 6);
				}
				
				System.out.println("\n\ntmpDayOfYearString = " + tmpDayOfYearString);
				tmpDayOfYear = Integer.parseInt(tmpDayOfYearString);

				tmpYear = Integer.parseInt(listDates1[i].substring(0, 4));
				System.out.println("\n\n Begin Year of record = " + tmpYear);
				
				if (tmpYear == beginYear && tmpDayOfYear < beginDayOfYear) {
					continue;
				} else {
					for (int n = i; n < listDates1.length; n++) {
						listDate2.add(listDates1[n]);
					}
					break;
				}
			}

			// take the tail of listRollCall (cut all elements that is after finishDate)
			listIterator = listDate2.listIterator(listDate2.size());
			while (listIterator.hasPrevious()) {
				listRollCall = listIterator.previous();
				tmpYear = Integer.parseInt(listRollCall.substring(0, 4));

				if (tmpYear < finishYear) {
					break;
				}

				if (listRollCall.contains(GeneralValue.markForMissingRollCall)
						|| listRollCall.contains(GeneralValue.markForTeacherMissing)) {
					tmpDayOfYearString = listRollCall.substring(5, listRollCall.length() - 2);
				} else if (listRollCall.contains(GeneralValue.markForPermission)
						|| listRollCall.contains(GeneralValue.markForNotBringPhone)) {
					tmpDayOfYearString = listRollCall.substring(5, listRollCall.length() - 7);
				} else {
					tmpDayOfYearString = listRollCall.substring(5, listRollCall.length() - 6);
				}
				
				tmpDayOfYear = Integer.parseInt(tmpDayOfYearString);

				if (tmpYear == finishYear) {
					if (tmpDayOfYear > finishDayOfYear) {
						listIterator.remove();
					} else {
						break;
					}
				} else {
					listIterator.remove();
				}
			}

			sumOfLesson = listDate2.size();
			System.out.println("\n\n listDate2 = " + listDate2);

			for (String rollCallRecord : listDate2) {
				if (rollCallRecord.contains(GeneralValue.markForMissingRollCall)) {
					sumOfAbsent++;
				} else if (rollCallRecord.contains(GeneralValue.markForNotBringPhone)) {
					sumOfFogettingPhone++;
				} else if (rollCallRecord.contains(GeneralValue.markForPermission)) {
					sumOfSick++;
				}
			}

			studentRecord = new GeneralStudentRecord();
			studentRecord.setClassName(studentClass.getClassInstance().getClassName());
			studentRecord.setCourseName(studentClass.getClassInstance().getCourse().getCourseName());
			studentRecord.setSumOfAbsent(sumOfAbsent);
			studentRecord.setSumOfFogettingPhone(sumOfFogettingPhone);
			studentRecord.setSumOfSick(sumOfSick);
			studentRecord.setSumOfLessons(sumOfLesson);

			listRecord.add(studentRecord);
			listDate2.clear();
		}

		return listRecord;
	}
	
	public List<GeneralTeacherRecord> getListOfTeacherRecord(String teacherEmail, String semesterID,
			String beginAtString, String finishAtString) {
		List<TeacherClass> listTeacherClass = this.studentClassDAL.getListClassOfTeacher(teacherEmail,
				Integer.parseInt(semesterID), IsTeaching.TEACHING.getValue());
		if (listTeacherClass == null || listTeacherClass.isEmpty()) {
			return null;
		}

		System.out.println("\n\n list teacher-class = " + listTeacherClass);

		LocalDate beginAt = LocalDate.parse(beginAtString);
		LocalDate finishAt = LocalDate.parse(finishAtString);
		List<GeneralTeacherRecord> listRecord = new ArrayList<>();
		GeneralTeacherRecord teacherRecord = null;
		String listRollCall = null;
		String[] listDates1 = null;
		List<String> listDate2 = new ArrayList<>();
		ListIterator<String> listIterator = null;
		String tmpDayOfYearString = null;
		int tmpDayOfYear = -1;
		int tmpYear = -1;
		int indexOfYear = -1;

		int beginYear = beginAt.getYear();
		int beginDayOfYear = beginAt.getDayOfYear();
		int finishYear = finishAt.getYear();
		int finishDayOfYear = finishAt.getDayOfYear();

		int sumOfLesson = -1;
		int sumOfAbsent = -1;

		for (TeacherClass teacherClass : listTeacherClass) {
			System.out.println("\n\nBegin filet =========================");
			System.out.println("\n\n sumOfLesson = " + sumOfLesson);
			sumOfLesson = 0;
			sumOfAbsent = 0;

			listRollCall = teacherClass.getListRollCall();
			if (listRollCall == null || listRollCall.isEmpty()) {
				teacherRecord = new GeneralTeacherRecord();
				teacherRecord.setClassName(teacherClass.getClassInstance().getClassName());
				teacherRecord.setCourseName(teacherClass.getClassInstance().getCourse().getCourseName());
				teacherRecord.setSumOfAbsent(0);
				teacherRecord.setSumOfLessons(0);

				listRecord.add(teacherRecord);
				continue;
			}
			indexOfYear = listRollCall.indexOf("" + beginYear);

			// indexOfYear should never be -1
			if (indexOfYear == -1) {
				teacherRecord = new GeneralTeacherRecord();
				teacherRecord.setClassName(teacherClass.getClassInstance().getClassName());
				teacherRecord.setCourseName(teacherClass.getClassInstance().getCourse().getCourseName());
				teacherRecord.setSumOfAbsent(0);
				teacherRecord.setSumOfLessons(0);

				listRecord.add(teacherRecord);
				continue;
			}
			listRollCall = listRollCall.substring(indexOfYear);

			if (beginYear != finishYear) {
				System.out.println("\n\n beginYear != finishYear");
				indexOfYear = listRollCall.indexOf("" + (finishYear + 1));
				if (indexOfYear != -1) {
					listRollCall = listRollCall.substring(0, indexOfYear);
				}
			}

			listDates1 = listRollCall.split(GeneralValue.regexForSplitListRollCall);

			// format of a date: xxxx (year) - x(?) (day of year) - xxxxx(seconds in day)
			// take the head of listRollCall (cut all elements that is before beginDate)
			for (int i = 0; i < listDates1.length; i++) {
				if (listDates1[i].contains(GeneralValue.markForMissingRollCall)) {
					tmpDayOfYearString = listDates1[i].substring(5, listDates1[i].length() - 2);
				} else {
					tmpDayOfYearString = listDates1[i].substring(5, listDates1[i].length() - 6);
				}
				
				System.out.println("\n\ntmpDayOfYearString = " + tmpDayOfYearString);
				tmpDayOfYear = Integer.parseInt(tmpDayOfYearString);

				tmpYear = Integer.parseInt(listDates1[i].substring(0, 4));
				System.out.println("\n\n Begin Year of record = " + tmpYear);
				
				if (tmpYear == beginYear && tmpDayOfYear < beginDayOfYear) {
					continue;
				} else {
					for (int n = i; n < listDates1.length; n++) {
						listDate2.add(listDates1[n]);
					}
					break;
				}
			}

			// take the tail of listRollCall (cut all elements that is after finishDate)
			listIterator = listDate2.listIterator(listDate2.size());
			while (listIterator.hasPrevious()) {
				listRollCall = listIterator.previous();
				tmpYear = Integer.parseInt(listRollCall.substring(0, 4));

				if (tmpYear < finishYear) {
					break;
				}

				if (listRollCall.contains(GeneralValue.markForMissingRollCall)) {
					tmpDayOfYearString = listRollCall.substring(5, listRollCall.length() - 2);
				} else {
					tmpDayOfYearString = listRollCall.substring(5, listRollCall.length() - 6);
				}
				
				tmpDayOfYear = Integer.parseInt(tmpDayOfYearString);

				if (tmpYear == finishYear) {
					if (tmpDayOfYear > finishDayOfYear) {
						listIterator.remove();
					} else {
						break;
					}
				} else {
					listIterator.remove();
				}
			}

			sumOfLesson = listDate2.size();
			System.out.println("\n\n listDate2 = " + listDate2);

			for (String rollCallRecord : listDate2) {
				if (rollCallRecord.contains(GeneralValue.markForMissingRollCall)) {
					sumOfAbsent++;
				}
			}

			teacherRecord = new GeneralTeacherRecord();
			teacherRecord.setClassName(teacherClass.getClassInstance().getClassName());
			teacherRecord.setCourseName(teacherClass.getClassInstance().getCourse().getCourseName());
			teacherRecord.setSumOfAbsent(sumOfAbsent);
			teacherRecord.setSumOfLessons(sumOfLesson);

			listRecord.add(teacherRecord);
			listDate2.clear();
		}

		return listRecord;
	}
	
	public List<DetailRecordForClass> getListOfClassRecord(int classID, String beginAtString, String finishAtString) {
		List<StudentClass> listStudentClass = this.studentClassDAL.getListStudent(classID);
		if (listStudentClass == null || listStudentClass.isEmpty()) {
			return null;
		}

		System.out.println("\n\n list studentClass = " + listStudentClass);
		
		LocalDate beginAt = LocalDate.parse(beginAtString);
		LocalDate finishAt = LocalDate.parse(finishAtString);
		int beginYear = beginAt.getYear();
		int beginDayOfYear = beginAt.getDayOfYear();
		int finishYear = finishAt.getYear();
		int finishDayOfYear = finishAt.getDayOfYear();
		
		List<DetailRecordForClass> listRecords = new ArrayList<>();
		List<String> listRollCallRecord = new ArrayList<>();
		DetailRecordForClass record = null;
		String listRollCallRaw = null;
		String tmpDayOfYearString = null;
		String studentName = null;
		String[] listRollCall;
		int tmpYear = -1;
		int tmpDayOfYear = -1;
		int indexOfSubString = -1;
		
		
		for (StudentClass studentClass : listStudentClass) {
			listRollCallRecord.clear();
			listRollCallRaw = studentClass.getListRollCall();
			
			if (listRollCallRaw == null || listRollCallRaw.isEmpty()) {
				record = new DetailRecordForClass();
				//need_change studentName = studentClass.getAccount().getUserInfo().split(GeneralValue.regexForSplitUserInfo)[0]
				record.setStudentName(studentName);
				record.setSumOfMissingRollCall(0);
				record.setSumOfRollCall(0);
				record.setSumOfSpecialCase(0);
				record.setEmail(studentClass.getAccount().getEmail());
				record.setSumOfLessons(0);
				listRecords.add(record);
				continue;
			}
			
			//cut the head of list to latest record of beginYear
			indexOfSubString = listRollCallRaw.indexOf("" + beginYear);
			if (indexOfSubString != -1) {
				listRollCallRaw = listRollCallRaw.substring(indexOfSubString);
				
			} else {
				//this student  do not have any record in request's range
				if (beginYear == finishYear) {
					continue;
				}
			}

			if (beginYear != finishYear) {
				System.out.println("\n\n beginYear != finishYear");
				indexOfSubString = listRollCallRaw.indexOf("" + (finishYear + 1));
				if (indexOfSubString != -1) {
					listRollCallRaw = listRollCallRaw.substring(0, indexOfSubString);
				}
			}
			
			listRollCall = studentClass.getListRollCall().split(GeneralValue.regexForSplitListRollCall);
			for (int i = 0; i < listRollCall.length; i++) {			
				if (listRollCall[i].contains(GeneralValue.markForMissingRollCall)
						|| listRollCall[i].contains(GeneralValue.markForTeacherMissing)) {
					tmpDayOfYearString = listRollCall[i].substring(5, listRollCall[i].length() - 2);
				} else if (listRollCall[i].contains(GeneralValue.markForPermission)
						|| listRollCall[i].contains(GeneralValue.markForNotBringPhone)) {
					tmpDayOfYearString = listRollCall[i].substring(5, listRollCall[i].length() - 7);
					
				} else {
					tmpDayOfYearString = listRollCall[i].substring(5, listRollCall[i].length() - 6);
				}
				
				System.out.println("\n\ntmpDayOfYearString = " + tmpDayOfYearString);
				tmpDayOfYear = Integer.parseInt(tmpDayOfYearString);

				tmpYear = Integer.parseInt(listRollCall[i].substring(0, 4));
				System.out.println("\n\n Begin Year of record = " + tmpYear);
				
				if (tmpYear == beginYear && tmpDayOfYear < beginDayOfYear) {
					// ignore all record which has date before beginAt
					continue;
				} else if (tmpYear == finishYear && tmpDayOfYear > finishDayOfYear) {
					// end loop if meet record which has date after finishAt
					break;
				} else {
					listRollCallRecord.add(listRollCall[i]);
					System.out.println("\n\n Valid record = " + listRollCall[i]);
				}
			}
				
			int sumOfMissingRollCall = 0;
			int sumOfSpecialCase = 0;
			int sumOfRollCall = 0;
			
			for (String rollCallRecord: listRollCallRecord) {
				if (rollCallRecord.contains(GeneralValue.markForMissingRollCall)) {
					sumOfMissingRollCall ++;
					
				} else if (rollCallRecord.contains(GeneralValue.markForTeacherMissing) 
						|| rollCallRecord.contains(GeneralValue.markForNotBringPhone)
						|| rollCallRecord.contains(GeneralValue.markForPermission)) {
					sumOfSpecialCase ++;
					
				} else {
					sumOfRollCall ++;
				}
					
			}

			//need_changeSystem.out.println("\n\n user info = " + studentClass.getAccount().getUserInfo());
			//need_change studentName = studentClass.getAccount().getUserInfo().split(GeneralValue.regexForSplitUserInfo)[0];
			System.out.println("\n\n studentName = " + studentName);
			
			record = new DetailRecordForClass();
			record.setStudentName(studentName);
			record.setSumOfMissingRollCall(sumOfMissingRollCall);
			record.setSumOfRollCall(sumOfRollCall);
			record.setSumOfSpecialCase(sumOfSpecialCase);
			record.setEmail(studentClass.getAccount().getEmail());
			record.setSumOfLessons(sumOfMissingRollCall + sumOfRollCall + sumOfSpecialCase);
			listRecords.add(record);
		}
		
		return listRecords;
	}

}
