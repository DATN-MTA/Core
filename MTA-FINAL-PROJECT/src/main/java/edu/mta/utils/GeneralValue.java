package edu.mta.utils;

public class GeneralValue {

	public static int isCheckTeacherRollcallToday ;
	public static int isCheckStudentRollcallToday ;
	
	public static final double minLongitude = -180;
	public static final double maxLongitude = 180;
	public static final double minLatitude = -90;
	public static final double maxLatitude = 90;
	
	public static final double degreeToRadiant = (Math.PI / 180D);
	public static final double eQuatorialEarthRadius = 6378.1370D;
	
	public static final int semestersInYear = 3;
	public static final int sequenceOfSummerSemester = 3;
	public static final int lengthOfNormalSemester = 180;		//unit: day
	public static final int lengthOfSummerSemester = 30;		//unit: day
	
	public static final int minStudents = 15;			//unit: person
	public static final int maxStudents = 100;			//unit: person
	
	public static final int maxClassPerTeacher = 7;		//unit: class
	public static final int maxClassPerStudent = 7;		//unit: class
	
	public static final String regexForSplitUserInfo = ":";
	
	public static final String regexForSplitListRollCall = ";";
	public static final String regexForSplitDate = "-";
	
	public static final String markForMissingRollCall = "x";
	public static final String markForTeacherMissing = "i";
	public static final String markForPermission = "y";
	public static final String markForNotBringPhone = "p";
	
	public static final int maxTimesForUpdatingImei = 3;
	
	//Report
	public static final String GENERAL_REPORT_FOR_TEACHER="GeneralReportForTeacher";
	public static final String GENERAL_REPORT_FOR_STUDENT="GeneralReportForStudent";
	public static final String REPORT_FOR_CLASS="ReportForClass";
	public static final String GENERAL_REPORT_FOR_STUDENT_TEMPLATE = "GeneralReportForStudent.rpttemplate";
	public static final String GENERAL_REPORT_FOR_TEACHER_TEMPLATE = "GeneralReportForTeacher.rpttemplate";
	public static final String REPORT_FOR_CLASS_TEMPLATE = "ReportForClass.rpttemplate";
	
	public static final String FILE_TYPE_XLS = "xls";
    public static final String FILE_TYPE_XLSX = "xlsx";
    public static final String FILE_TYPE_XML = "xml";
    public static final String FILE_TYPE_PDF = "pdf";
    public static final String FILE_TYPE_DOCX = "docx";
    public static final String FILE_TYPE_DOC = "doc";
    public static final String FILE_TYPE_HTML = "html";
    
    public static final String GEN_SYMBOLIC_FAIL = "GEN_SYMBOLIC_FAIL";
    public static final String GEN_SYMBOLIC_FAIL_DES = "GEN SYMBOLIC FAIL";
    
    
    public static final String LINK_FOLDER_REPORT_IN_SERVER = "C:\\Users\\BePro\\Desktop\\invoiceFolder";
    public static final String LINK_FONT_REPORT_IN_SERVER = "C:\\Users\\BePro\\Desktop\\invoiceFolder\\font";
    public static final String LINK_OUT_REPORT_IN_SERVER = "C:\\Users\\BePro\\Desktop\\invoiceFolder\\out";
}
