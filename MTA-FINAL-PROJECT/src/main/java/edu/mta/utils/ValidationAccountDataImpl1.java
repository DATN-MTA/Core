package edu.mta.utils;

import edu.mta.enumData.AccountRole;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ValidationAccountDataImpl1")
public class ValidationAccountDataImpl1 implements ValidationAccountData {

	@Override
	public String validateEmailData(String email) {
		if (email == null || email.isBlank()) {
			return "Missing email info!";
		} else {
			if (!email.contains("@") || !email.contains(".com")) {
				return "Email is in wrong format!";
			}
		}
		return null;
	}

	@Override
	public String validatePasswordData(String password) {
		if (password == null || password.isBlank()) {
			return "Missing password info!";
		}
		return null;
	}

	@Override
	public String validateUsernameData(String username) {
		if (username == null || username.isBlank()) {
			return "Missing username info!";
		}
		return null;
	}

	@Override
	public String validateRoleData(int role) {
		boolean flag = false;
		for (AccountRole instance: AccountRole.values()) {
			if (role == instance.getValue()) {
				flag = true;
				break;
			}
		}
		
		if (flag == false) {
			return "Role value is out of valid range!";
		}
		return null;
	}
	
	

	@Override
	public String validateIdData(int id) {
		if (id < 1) {
			return "Id can not be less than 1!";
		}
		return null;
	}

	@Override
	public String validateImeiData(String imei) {
		if (imei == null || imei.isBlank()) {
			return "Missing imei info!";
		}
		return null;
	}

}
