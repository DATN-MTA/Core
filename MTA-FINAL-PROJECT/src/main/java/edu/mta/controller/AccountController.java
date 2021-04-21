package edu.mta.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mta.enumData.AccountRole;
import edu.mta.enumData.AccountStatus;
import edu.mta.model.Account;
import edu.mta.model.ReportError;
import edu.mta.model.User;
import edu.mta.service.AccountService;
import edu.mta.utils.FrequentlyUtils;
import edu.mta.utils.GeneralValue;
import edu.mta.utils.ValidationAccountData;
import edu.mta.utils.ValidationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class AccountController {

	private AccountService accountService;
	private ValidationAccountData validationAccountData;
	private ValidationData validationData;
	private FrequentlyUtils frequentlyUtils;

	@Autowired
	public AccountController(@Qualifier("AccountServiceImpl1") AccountService accountService,
			@Qualifier("ValidationDataImpl1") ValidationData validationData,
			@Qualifier("FrequentlyUtilsImpl1") FrequentlyUtils frequentlyUtils,
			@Qualifier("ValidationAccountDataImpl1") ValidationAccountData validationAccountData) {
		this.accountService = accountService;
		this.validationData = validationData;
		this.frequentlyUtils = frequentlyUtils;
		this.validationAccountData = validationAccountData;
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> checkLogin(@RequestBody String infoLogin) {
		Map<String, Object> jsonMap = null;
		ObjectMapper objectMapper = null;
		String email = null;
		String password = null;
		String errorMessage = null;
		ReportError report = null;

		try {
			objectMapper = new ObjectMapper();
			jsonMap = objectMapper.readValue(infoLogin, new TypeReference<Map<String, Object>>() {
			});

			// check request body has enough info in right JSON format
			if (!this.frequentlyUtils.checkKeysExist(jsonMap, "email", "password")) {
				report = new ReportError(1, "Email and password are required!");
				return ResponseEntity.badRequest().body(report);
			}

			errorMessage = this.validationData.validateAccountData(jsonMap);
			if (errorMessage != null) {
				report = new ReportError(10, "Login failed because " + errorMessage);
				return ResponseEntity.badRequest().body(report);
				// return new ResponseEntity<>(report, HttpStatus.FORBIDDEN);
			}

			email = jsonMap.get("email").toString();
			password = jsonMap.get("password").toString();

			Account account = this.accountService.findAccountByEmailAndPassword(email, password);
			if (account == null) {
				report = new ReportError(11, "Email or password is incorrect!");
				return new ResponseEntity<>(report, HttpStatus.UNAUTHORIZED);
			}
			
			if (account.getIsActive() != AccountStatus.ACTIVE.getValue()) {
				report = new ReportError(19, "Account must be activated!");
				return new ResponseEntity<>(report, HttpStatus.UNAUTHORIZED);
			}

			// in the first login, student will be redirect to Update info page
			if (account.getRole() == AccountRole.STUDENT.getValue()) {
				if (account.getImei() == null || account.getImei().isBlank()) {
					account.setImei(null);
				}
			}

			return ResponseEntity.ok(account);

		} catch (Exception e) {
			e.printStackTrace();
			report = new ReportError(2, "Error happened when jackson deserialization info!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, report.toString());
		}
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public ResponseEntity<?> registration(@RequestBody String registrationData) {
		Map<String, Object> jsonMap = null;
		ObjectMapper objectMapper = null;
		Account account = null;
		String errorMessage = null;
		String email = null;
		String username = null;
		String password = null;
		int role = -1;
		ReportError report;

		try {
			objectMapper = new ObjectMapper();
			jsonMap = objectMapper.readValue(registrationData, new TypeReference<Map<String, Object>>() {
			});

			// check request body has enough info in right JSON format
			if (!this.frequentlyUtils.checkKeysExist(jsonMap, "email", "username", "role", "password", "userInfo")) {
				report = new ReportError(1, "You have to fill all required information!");
				return ResponseEntity.badRequest().body(report);
			}

			errorMessage = this.validationData.validateAccountData(jsonMap);
			if (errorMessage != null) {
				report = new ReportError(12, "Registration failed because " + errorMessage);
				return ResponseEntity.badRequest().body(report);
			}

			email = jsonMap.get("email").toString();
			if (this.accountService.checkEmailIsUsed(email) != null) {
				report = new ReportError(13, "Registrantion failed because this email has already been used");
				return ResponseEntity.badRequest().body(report);
			}
			
			String userInfo = jsonMap.get("userInfo").toString();
			
			username = jsonMap.get("username").toString();
			password = jsonMap.get("password").toString();
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String encodedPassword = passwordEncoder.encode(password);
			role = Integer.parseUnsignedInt(jsonMap.get("role").toString());

			account = new Account(username, encodedPassword, role, email);
			account.setUserInfo(userInfo);
			account.setIsActive(AccountStatus.ACTIVE.getValue());
			account.setImei(null);
			account.setUpdateImeiCounter(0);
			this.accountService.saveAccount(account);

			return new ResponseEntity<>("Registration success", HttpStatus.CREATED);

		} catch (Exception e) {
			e.printStackTrace();
			report = new ReportError(2, "Error happened when jackson deserialization info!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, report.toString());
		}
	}

	@RequestMapping(value = "/deactivateAccount", method = RequestMethod.PUT)
	public ResponseEntity<?> disableAccount(@RequestBody String requestInfo) {
		Map<String, Object> jsonMap = null;
		ObjectMapper objectMapper = null;
		String errorMessage = null;
		String email = null;
		ReportError report;

		try {
			objectMapper = new ObjectMapper();
			jsonMap = objectMapper.readValue(requestInfo, new TypeReference<Map<String, Object>>() {
			});

			// check request body has enough info in right JSON format
			if (!this.frequentlyUtils.checkKeysExist(jsonMap, "email")) {
				report = new ReportError(1, "You have to fill all required information!");
				return ResponseEntity.badRequest().body(report);
			}

			errorMessage = this.validationData.validateAccountData(jsonMap);
			if (errorMessage != null) {
				report = new ReportError(14, "Deactive account failed because " + errorMessage);
				return ResponseEntity.badRequest().body(report);
			}

			email = jsonMap.get("email").toString();
			if (this.accountService.deactivateAccount(email)) {
				report = new ReportError(200, "Deactivate account successful!");
				return ResponseEntity.ok(report);
			}

			report = new ReportError(11, "Authentication has failed or has not yet been provided!");
			return new ResponseEntity<>(report, HttpStatus.UNAUTHORIZED);

		} catch (Exception e) {
			e.printStackTrace();
			report = new ReportError(2, "Error happened when jackson deserialization info!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, report.toString());
		}
	}

	@RequestMapping(value = "/activateAccount", method = RequestMethod.PUT)
	public ResponseEntity<?> activateAccount(@RequestBody String requestInfo) {
		Map<String, Object> jsonMap = null;
		ObjectMapper objectMapper = null;
		String errorMessage = null;
		String email = null;
//		String password = null;
//		int role = -1;
		ReportError report;

		try {
			objectMapper = new ObjectMapper();
			jsonMap = objectMapper.readValue(requestInfo, new TypeReference<Map<String, Object>>() {
			});

			// check request body has enough info in right JSON format
			if (!this.frequentlyUtils.checkKeysExist(jsonMap, "email")) {
				report = new ReportError(1, "You have to fill all required information!");
				return ResponseEntity.badRequest().body(report);
			}

			errorMessage = this.validationData.validateAccountData(jsonMap);
			if (errorMessage != null) {
				report = new ReportError(15, "Active account failed because " + errorMessage);
				return ResponseEntity.badRequest().body(report);
			}

			email = jsonMap.get("email").toString();
			//password = jsonMap.get("password").toString();
			//role = Integer.parseInt(jsonMap.get("role").toString());
			if (this.accountService.activateAccount(email)) {
				report = new ReportError(200, "Activate account successful!");
				return ResponseEntity.ok(report);
			}

			report = new ReportError(11, "Authentication has failed or has not yet been provided!");
			return new ResponseEntity<>(report, HttpStatus.UNAUTHORIZED);

		} catch (Exception e) {
			e.printStackTrace();
			report = new ReportError(2, "Error happened when jackson deserialization info!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, report.toString());
		}
	}

	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	public ResponseEntity<?> getAccountInfo(@RequestHeader(value = "email", required = true) String email,
			@RequestHeader(value = "password", required = true) String password) {
		Map<String, Object> jsonMap = null;
		String errorMessage = null;
		ReportError report;

		try {
			jsonMap = new HashMap<>();
			jsonMap.put("email", email);
			jsonMap.put("password", password);

			errorMessage = this.validationData.validateAccountData(jsonMap);
			if (errorMessage != null) {
				report = new ReportError(16, "Getting account info failed because " + errorMessage);
				return ResponseEntity.badRequest().body(report);
			}

			Account account = this.accountService.findAccountByEmailAndPassword(email, password);
			if (account != null) {
				return ResponseEntity.ok(account);
			}

			report = new ReportError(11, "Authentication has failed or has not yet been provided!");
			return new ResponseEntity<>(report, HttpStatus.UNAUTHORIZED);

		} catch (Exception e) {
			e.printStackTrace();
			report = new ReportError(2, "Error happened when jackson deserialization info!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, report.toString());
		}
	}

	@RequestMapping(value = "/accounts", method = RequestMethod.PUT)
	public ResponseEntity<?> updateAccountInfo(@RequestHeader(value = "email") String email,
			@RequestHeader(value = "password") String password,
			@RequestParam(value = "updateUser", required = true) boolean updateUser, @RequestBody String accountInfo) {

		System.out.println("\n\n begin update");
		ObjectMapper objectMapper = null;
		Map<String, Object> jsonMap = new HashMap<>();
		Account account = null;
		Account tmpAccount = null;
		String errorMessage = null;
		String newEmail = null;
		String newImei = null;
		ReportError report;
		User user = null;

		// check old info (provided by header) is correct
		jsonMap.put("email", email);
		jsonMap.put("password", password);
		errorMessage = this.validationData.validateAccountData(jsonMap);
		if (errorMessage != null) {
			System.out.println("validate failed");
			report = new ReportError(17, "Updating account info failed because " + errorMessage);
			return ResponseEntity.badRequest().body(report);
		}

		account = this.accountService.findAccountByEmailAndPassword(email, password);
		if (account == null) {
			report = new ReportError(11, "Authentication has failed or has not yet been provided!");
			return new ResponseEntity<>(report, HttpStatus.UNAUTHORIZED);
		}

		// prepare map for new info
		jsonMap.clear();
		try {
			objectMapper = new ObjectMapper();
			jsonMap = objectMapper.readValue(accountInfo, new TypeReference<Map<String, Object>>() {
			});

			// check request body has enough info in right JSON format
			if (updateUser == false) {
				if (!this.frequentlyUtils.checkKeysExist(jsonMap, "email", "password", "username", "imei")) {
					report = new ReportError(1, "You have to fill all required information!");
					return ResponseEntity.badRequest().body(report);
				}

			} else {
				if (!this.frequentlyUtils.checkKeysExist(jsonMap, "email", "password", "username", "imei", "birthday",
						"phone", "address", "fullName")) {
					report = new ReportError(1, "You have to fill all required information!");
					return ResponseEntity.badRequest().body(report);
				}
			}

			// check new account data is valid
			errorMessage = this.validationData.validateAccountData(jsonMap);
			if (errorMessage != null) {
				report = new ReportError(17, "Updating account info failed because " + errorMessage);
				return ResponseEntity.badRequest().body(report);
			}

			// Check if new email is not used anywhere (must exclude account itself)
			newEmail = jsonMap.get("email").toString();
			tmpAccount = this.accountService.findAccountByEmail(newEmail);
			if (tmpAccount != null && tmpAccount.getId() != account.getId()) {
				report = new ReportError(17,
						"Updating account info failed because the email is already used by another account");
				return ResponseEntity.badRequest().body(report);
			}

			newImei = jsonMap.get("imei").toString();
			if (!newImei.equals(account.getImei()) || account.getRole() == AccountRole.STUDENT.getValue()) {
				if (account.getUpdateImeiCounter() == GeneralValue.maxTimesForUpdatingImei) {
					report = new ReportError(18, "This account is not allowed to change IMEI number anymore");
					return ResponseEntity.badRequest().body(report);
				}

				account.setImei(newImei);
				account.setUpdateImeiCounter(account.getUpdateImeiCounter() + 1);
			}

			if (updateUser == true) {
				LocalDate birthday = null;
				String address = null;
				String fullName = null;
				String phone = null;

				errorMessage = this.validationData.validateUserData(jsonMap);
				if (errorMessage != null) {
					report = new ReportError(20, errorMessage);
					return ResponseEntity.badRequest().body(report);
				}

				fullName = jsonMap.get("fullName").toString();
				phone = jsonMap.get("phone").toString();
				birthday = LocalDate.parse(jsonMap.get("birthday").toString());
				address = jsonMap.get("address").toString();
				user = new User(address, fullName, birthday, phone);

				String userInfo = this.accountService.createUserInfoString(user);
				account.setUserInfo(userInfo);
			}

			account.setEmail(newEmail);
			account.setPassword(jsonMap.get("password").toString());
			account.setUsername(jsonMap.get("username").toString());
			this.accountService.updateAccountInfo(account);

			report = new ReportError(200, "Updating account info successful!");
			return ResponseEntity.ok(report);

		} catch (Exception e) {
			e.printStackTrace();
			report = new ReportError(2, "Error happened when jackson deserialization info!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, report.toString());
		}
	}

	@RequestMapping(value = "/users", method = RequestMethod.POST)
	public ResponseEntity<?> addUserInfo(@RequestBody String userInfo) {
		ObjectMapper objectMapper = null;
		Map<String, Object> jsonMap = null;
		int id = -1;
		Account account = null;
		User user = null;
		String errorMessage = null;
		LocalDate birthday = null;
		String address = null;
		String fullName = null;
		String phone = null;
		ReportError report;

		try {
			objectMapper = new ObjectMapper();
			jsonMap = objectMapper.readValue(userInfo, new TypeReference<Map<String, Object>>() {
			});

			// check request body has enough info in right JSON format
			if (!this.frequentlyUtils.checkKeysExist(jsonMap, "id", "birthday", "phone", "address", "fullName")) {
				report = new ReportError(1, "You have to fill all required information!");
				return ResponseEntity.badRequest().body(report);
			}

			errorMessage = this.validationData.validateUserData(jsonMap);
			if (errorMessage != null) {
				report = new ReportError(20, "Adding user info failed because" + errorMessage);
				return ResponseEntity.badRequest().body(report);
			}

			id = Integer.parseInt(jsonMap.get("id").toString());
			account = this.accountService.findAccountByID(id);
			if (account == null) {
				report = new ReportError(11, "Authentication has failed or has not yet been provided!");
				return new ResponseEntity<>(report, HttpStatus.UNAUTHORIZED);
			}

			String tmpUserInfo = account.getUserInfo();
			if (tmpUserInfo != null && !tmpUserInfo.isEmpty()) {
				report = new ReportError(21, "User's info cannot be overriden by this API !");
				return new ResponseEntity<>(report, HttpStatus.CONFLICT);
			}

			fullName = jsonMap.get("fullName").toString();
			phone = jsonMap.get("phone").toString();
			birthday = LocalDate.parse(jsonMap.get("birthday").toString());
			address = jsonMap.get("address").toString();
			user = new User(id, address, fullName, birthday, phone);

			this.accountService.addUserInfo(user);
			report = new ReportError(200, "Add user info successful!");
			return ResponseEntity.ok(report);

		} catch (Exception e) {
			e.printStackTrace();
			report = new ReportError(2, "Error happened when jackson deserialization info!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, report.toString());
		}
	}

	@RequestMapping(value = "/users", method = RequestMethod.PUT)
	public ResponseEntity<?> updateUserInfo(@RequestBody String userInfo) {
		ObjectMapper objectMapper = null;
		Map<String, Object> jsonMap = null;
		int id = -1;
		Account account = null;
		User user = null;
		String errorMessage = null;
		LocalDate birthday = null;
		String address = null;
		String fullName = null;
		String phone = null;
		ReportError report;

		try {
			objectMapper = new ObjectMapper();
			jsonMap = objectMapper.readValue(userInfo, new TypeReference<Map<String, Object>>() {
			});

			// check request body has enough info in right JSON format
			if (!this.frequentlyUtils.checkKeysExist(jsonMap, "id", "birthday", "phone", "address", "fullName")) {
				report = new ReportError(1, "You have to fill all required information!");
				return ResponseEntity.badRequest().body(report);
			}

			errorMessage = this.validationData.validateUserData(jsonMap);
			if (errorMessage != null) {
				report = new ReportError(22, "Updating user info failed because" + errorMessage);
				return ResponseEntity.badRequest().body(report);
			}

			id = Integer.parseInt(jsonMap.get("id").toString());
			account = this.accountService.findAccountByID(id);
			if (account == null) {
				report = new ReportError(11, "Authentication has failed or has not yet been provided!");
				return new ResponseEntity<>(report, HttpStatus.UNAUTHORIZED);
			}

			fullName = jsonMap.get("fullName").toString();
			phone = jsonMap.get("phone").toString();
			birthday = LocalDate.parse(jsonMap.get("birthday").toString());
			address = jsonMap.get("address").toString();
			user = new User(id, address, fullName, birthday, phone);

			this.accountService.updateUserInfo(user);
			report = new ReportError(200, "Update user info successful!");
			return ResponseEntity.ok(report);

		} catch (Exception e) {
			e.printStackTrace();
			report = new ReportError(2, "Error happened when jackson deserialization info!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, report.toString());
		}
	}

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public ResponseEntity<?> findUserInfo(@RequestHeader(value = "email", required = true) String email,
			@RequestHeader(value = "password", required = true) String password) {

		Map<String, Object> jsonMap = null;
		String errorMessage = null;
		ReportError report;

		try {
			jsonMap = new HashMap<>();
			jsonMap.put("email", email);
			jsonMap.put("password", password);

			errorMessage = this.validationData.validateAccountData(jsonMap);
			if (errorMessage != null) {
				report = new ReportError(16, "Getting account info failed because " + errorMessage);
				return ResponseEntity.badRequest().body(report);
			}

			Account account = this.accountService.findAccountByEmailAndPassword(email, password);
			if (account != null) {
				String userInfo = account.getUserInfo();
				if (userInfo == null || userInfo.isBlank()) {
					report = new ReportError(23, "This user info has not existed yet!");
					return new ResponseEntity<>(report, HttpStatus.NOT_FOUND);
				}
			}

			return ResponseEntity.ok(account.getUserInfo());
		} catch (Exception e) {
			e.printStackTrace();
			report = new ReportError(2, "Error happened when jackson deserialization info!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, report.toString());
		}
	}

	@RequestMapping(value = "/createMultipleAccount", method = RequestMethod.POST)
	public ResponseEntity<?> createMultipleAccount(@RequestBody String accountInfo) {
		
		ObjectMapper objectMapper = null;
		List<Account> listAccount = null;
		String errorMessage = null;
		ReportError report;
		Account account = null;
		int invalidAccount = 0;
		int rowCounter = 1; // Excel table: first row = info of field
		String infoOfRow = "";

		try {
			objectMapper = new ObjectMapper();
			listAccount = objectMapper.readValue(accountInfo, new TypeReference<List<Account>>() {
			});

			for (Account tmpAccount : listAccount) {
				rowCounter++;
				errorMessage = this.validationAccountData.validateUsernameData(tmpAccount.getUsername());
				if (errorMessage != null) {
					invalidAccount++;
					infoOfRow += rowCounter +',';
					continue;
				}

				errorMessage = this.validationAccountData.validatePasswordData(tmpAccount.getPassword());
				BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
				String encodedPassword = passwordEncoder.encode(tmpAccount.getPassword());
				tmpAccount.setPassword(encodedPassword);

				if (errorMessage != null) {
					invalidAccount++;
					infoOfRow += rowCounter +',';
					continue;
				}

				errorMessage = this.validationAccountData.validateEmailData(tmpAccount.getEmail());
				if (errorMessage != null) {
					invalidAccount++;
					infoOfRow += rowCounter +',';
					continue;
				}

				account = this.accountService.findAccountByEmail(tmpAccount.getEmail());
				if (account != null) {
					invalidAccount++;
					infoOfRow += rowCounter + ", ";
					continue;
				}

				this.accountService.saveAccount(tmpAccount);
			}
                           
                        if (invalidAccount == 0) {
                            report = new ReportError(200, "" + invalidAccount+ "-0");
                        } else {
                            report = new ReportError(200, "" + invalidAccount + "-" + infoOfRow);
                        }
			
			return ResponseEntity.ok(report);

		} catch (Exception e) {
			e.printStackTrace();
			report = new ReportError(2, "Error happened when jackson deserialization info!");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, report.toString());
		}
	}
}
