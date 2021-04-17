package edu.mta.model;

public class CommonOutput {

	private String errorCode;
    private String description;
    
	public CommonOutput() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CommonOutput(String errorCode, String description) {
		super();
		this.errorCode = errorCode;
		this.description = description;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
    
    
}
