package edu.mta.external;

import edu.mta.enumData.IsLearning;
import edu.mta.model.*;

import java.io.IOException;
import java.lang.Class;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class StudentClassDAL {
	private static String url = "jdbc:mysql://localhost:3306/roll_call?serverTimezone=Asia/Bangkok&allowPublicKeyRetrieval=true&useSSL=false&useUnicode=yes&characterEncoding=UTF-8";
	private static String user = "root";
	private static String password = "123456";

	public static Connection getConnecṭ() {
		Connection connection = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (connection == null) {
			try {
				throw new IOException("connection is fail!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return connection;
	}

	public List<StudentClass> getListClass(String studentEmail, int semesterID, int isLearning) {
		Connection connection = null;
		PreparedStatement ps = null;
		List<StudentClass> listOfStudent = new ArrayList<>();
		ResultSet rs = null;
		
		try {
			connection = getConnecṭ();
			ps = connection.prepareStatement("SELECT sc.ID, sc.IsChecked, sc.ListRollCall, cl.ClassName, cr.CourseName "
					+ "FROM student_class AS sc, class AS cl, account AS ac, course AS cr "
					+ "WHERE ac.Email = ? AND cl.SemesterID = ? AND sc.IsLearning = ? AND ac.ID = sc.StudentID AND sc.ClassID = cl.ID AND cl.CourseID = cr.ID" );
			ps.setString(1, studentEmail);
			ps.setInt(2, semesterID);
			ps.setInt(3, isLearning);
			rs = ps.executeQuery();
			while (rs.next()) {
				StudentClass studentClass = new StudentClass();
				edu.mta.model.Class classInstance = new edu.mta.model.Class();
				Course course = new Course();
				
				studentClass.setId(rs.getInt("ID"));
				studentClass.setIsChecked(rs.getString("IsChecked"));
				studentClass.setListRollCall(rs.getString("ListRollCall"));
				
				classInstance.setClassName(rs.getString("ClassName"));
				course.setCourseName(rs.getString("CourseName"));
				classInstance.setCourse(course);
				
				studentClass.setClassInstance(classInstance);
				
				listOfStudent.add(studentClass);
			}
			
			return listOfStudent;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public List<TeacherClass> getListClassOfTeacher(String teacherEmail, int semesterID, int isLearning) {
		Connection connection = null;
		PreparedStatement ps = null;
		List<TeacherClass> listOfClass = new ArrayList<>();
		ResultSet rs = null;
		
		try {
			connection = getConnecṭ();
			ps = connection.prepareStatement("SELECT tc.ID, tc.ListRollCall, cl.ClassName, cr.CourseName "
					+ "FROM teacher_class AS tc, class AS cl, account AS ac, course AS cr "
					+ "WHERE ac.Email = ? AND cl.SemesterID = ? AND tc.IsTeaching = ? AND ac.ID = tc.TeacherID AND tc.ClassID = cl.ID AND cl.CourseID = cr.ID" );
			ps.setString(1, teacherEmail);
			ps.setInt(2, semesterID);
			ps.setInt(3, isLearning);
			rs = ps.executeQuery();
			while (rs.next()) {
				TeacherClass teacherClass = new TeacherClass();
				edu.mta.model.Class classInstance = new edu.mta.model.Class();
				Course course = new Course();
				
				teacherClass.setId(rs.getInt("ID"));
				teacherClass.setListRollCall(rs.getString("ListRollCall"));
				
				classInstance.setClassName(rs.getString("ClassName"));
				course.setCourseName(rs.getString("CourseName"));
				classInstance.setCourse(course);
				
				teacherClass.setClassInstance(classInstance);
				
				listOfClass.add(teacherClass);
			}
			
			return listOfClass;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public List<StudentClass> getListStudent(int classID) {
		Connection connection = null;
		PreparedStatement ps = null;
		List<StudentClass> listOfStudent = new ArrayList<>();
		ResultSet rs = null;
		StudentClass studentClass = null;
		Account tmpAccount = null;
		
		try {
			connection = getConnecṭ();
			ps = connection.prepareStatement("SELECT ac.ID, ac.Email, sc.listRollCall "
					+ "FROM student_class AS sc, account AS ac "
					+ "WHERE sc.IsLearning = ? AND ac.ID = sc.StudentID AND sc.ClassID = ?" );
			ps.setInt(1, IsLearning.LEARNING.getValue());
			ps.setInt(2, classID);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				studentClass = new StudentClass();
				tmpAccount = new Account();

				tmpAccount.setUser(getUserInfo(rs.getString("ID")));
				tmpAccount.setEmail(rs.getString("Email"));
				studentClass.setAccount(tmpAccount);
				studentClass.setListRollCall(rs.getString("listRollCall"));
				
				listOfStudent.add(studentClass);
			}
			
			return listOfStudent;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public User getUserInfo(String accountId) {
		Connection connection = null;
		PreparedStatement ps = null;
		List<TeacherClass> listOfClass = new ArrayList<>();
		ResultSet rs = null;

		try {
			connection = getConnecṭ();
			ps = connection.prepareStatement("SELECT u.id, u.address, u.fullname FROM user as u WHERE account_id = ?" );
			ps.setString(1, accountId);
			rs = ps.executeQuery();
			User user = new User();
			while (rs.next()) {
				user.setId(rs.getInt("id"));
				user.setAddress(rs.getString("address"));
				user.setFullName(rs.getString("fullname"));
			}
			return user;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
