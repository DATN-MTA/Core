package edu.mta.service;

import java.util.Optional;

import edu.mta.enumData.AccountStatus;
import edu.mta.model.Account;
import edu.mta.model.User;
import edu.mta.repository.AccountRepository;
import edu.mta.utils.GeneralValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("AccountServiceImpl1")
public class AccountServiceImpl1 implements AccountService {

	private AccountRepository accountRepository;

	@Autowired
	public AccountServiceImpl1(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	public Account findAccountByEmailAndPassword(String email, String password) {
		Optional<Account> account = this.accountRepository.findByEmailAndPassword(email, password);
		if (account.isPresent()) {
			return account.get();
		}
		return null;
	}

	@Override
	public Account checkEmailIsUsed(String email) {
		Optional<Account> account = this.accountRepository.findByEmail(email);
		return account.isPresent() ? account.get() : null;
	}

	@Override
	public void saveAccount(Account account) {
		this.accountRepository.save(account);
		return;
	}

	@Override
	public boolean deactivateAccount(String email) {
		Optional<Account> account = this.accountRepository.findByEmail(email);
		if (account.isPresent()) {
			Account target = account.get();
			
			target.setIsActive(AccountStatus.DISABLE.getValue());
			this.accountRepository.save(target);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean activateAccount(String email) {
		Optional<Account> account = this.accountRepository.findByEmail(email);
		if (account.isPresent()) {
			Account target = account.get();
			
			//only admin has full right to disable all types of account;
			//student and teacher just can only disable their own type of account
//			if (role != AccountRole.ADMIN.getValue() && role != target.getRole()) {
//				return false;
//			}
			
			target.setIsActive(AccountStatus.ACTIVE.getValue());
			this.accountRepository.save(target);
			return true;
		}
		return false;
	}

	@Override
	public Account updateAccountInfo(Account account) {
		Optional<Account> oldInfo = this.accountRepository.findById(account.getId());
		if (oldInfo.isEmpty()) {
			return null;
		}

		this.accountRepository.save(account);
		return account;
	}

	@Override
	public void addUserInfo(User user) {
		String userInfo = createUserInfoString(user);

		Account account = this.accountRepository.findById(user.getId()).get();
		account.setUserInfo(userInfo);
		this.accountRepository.save(account);
		return;
	}

	@Override
	public Account findAccountByID(int id) {
		Optional<Account> account = this.accountRepository.findById(id);
		if (account.isEmpty()) {
			return null;
		}

		return account.get();
	}

	@Override
	public boolean updateUserInfo(User user) {
		Optional<Account> oldInfo = this.accountRepository.findById(user.getId());

		if (oldInfo.isEmpty()) {
			return false;
		}

		Account account = oldInfo.get();
		String userInfo = createUserInfoString(user);
		account.setUserInfo(userInfo);
		this.accountRepository.save(account);
		return true;
	}

	@Override
	public String createUserInfoString(User user) {
		String userInfo = null;

		// userInfo has format: "fullName+address+phone+birthDay"
		userInfo = user.getFullName() + GeneralValue.regexForSplitUserInfo;
		userInfo += user.getAddress() + GeneralValue.regexForSplitUserInfo;
		userInfo += user.getPhone() + GeneralValue.regexForSplitUserInfo;
		userInfo += user.getBirthDay();
		
		return userInfo;
	}

	@Override
	public Account findAccountByEmail(String email) {
		Optional<Account> account = this.accountRepository.findByEmail(email);
		if (account.isEmpty()) {
			return null;
		}
		return account.get();
	}

}
