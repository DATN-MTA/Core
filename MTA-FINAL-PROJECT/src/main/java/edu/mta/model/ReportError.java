package edu.mta.model;

public class ReportError {

	private int errorCode;
	
	private String description;

	public ReportError() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ReportError(int errorCode, String description) {
		super();
		this.errorCode = errorCode;
		this.description = description;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "{\"errorCode\" : \"" + errorCode + "\", \"description\" : \""+ description + "\"}";
	}
	
	
}
