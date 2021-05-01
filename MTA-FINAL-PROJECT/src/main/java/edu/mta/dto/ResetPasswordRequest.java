package edu.mta.dto;

public class ResetPasswordRequest {
    private  String email;
    private  String newPassword ;
    private  String confirmPassword;


    public ResetPasswordRequest() {
        // TODO Auto-generated constructor stub
    }


    public ResetPasswordRequest(String token, String email, String newPassword) {
        super();
        this.email = email;
        this.newPassword = newPassword;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }



    public String getNewPassword() {
        return newPassword;
    }


    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
