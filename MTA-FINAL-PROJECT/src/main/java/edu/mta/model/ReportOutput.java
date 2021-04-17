package edu.mta.model;

public class ReportOutput extends CommonOutput {

	private byte[] fileOutput;
    private String linkFile;
    
	public ReportOutput() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ReportOutput(String errorCode, String description) {
		super(errorCode, description);
		// TODO Auto-generated constructor stub
	}
	
	public ReportOutput(byte[] fileOutput, String linkFile) {
		super();
		this.fileOutput = fileOutput;
		this.linkFile = linkFile;
	}
	
	public byte[] getFileOutput() {
		return fileOutput;
	}
	
	public void setFileOutput(byte[] fileOutput) {
		this.fileOutput = fileOutput;
	}
	
	public String getLinkFile() {
		return linkFile;
	}
	
	public void setLinkFile(String linkFile) {
		this.linkFile = linkFile;
	}
    
}
