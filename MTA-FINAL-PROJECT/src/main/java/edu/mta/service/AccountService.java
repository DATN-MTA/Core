package edu.mta.service;

import edu.mta.dto.AccountDataDTO;
import edu.mta.model.Account;
import edu.mta.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author BePro
 *
 */
public interface AccountService {
	
	/**
	 * @param email 
	 * @param password
	 * @return an Account object if login success;
	 */
	Account findAccountByEmailAndPassword(String email, String password);

	
	/**
	 * @param email
	 * @return true if this email has already been used by another account
	 */
	Account checkEmailIsUsed(String email);
	
	/**
	 * @param account - New/Update account need to be saved
	 */
	void saveAccount(Account account);
	
	void addUserInfo(User user);
	
	boolean updateUserInfo(User user);
	
	Account findAccountByID(int id);
	
	boolean activeOrDeactivateAccount(String email);
	
	boolean activateAccount(String email);

	Account updateAccountInfo(Account account);

	Account findAccountByEmail(String email);

	String createUserInfoString(User user);

	String signin(String username, String password);

	String signup(AccountDataDTO accountDataDTO);

	Account whoami(HttpServletRequest req);

	boolean activeOrDeactivateAccount(List<Integer> acountIds, Integer status);

	Page<Account> getAllAccount(Pageable page, String emailOrUserName);
}
