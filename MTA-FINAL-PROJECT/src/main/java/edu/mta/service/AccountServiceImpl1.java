package edu.mta.service;

import edu.mta.dto.AccountDataDTO;
import edu.mta.enumData.AccountStatus;
import edu.mta.exception.CustomException;
import edu.mta.mapper.AccountDTOMapper;
import edu.mta.model.Account;
import edu.mta.model.User;
import edu.mta.repository.AccountRepository;
import edu.mta.repository.UserRepository;
import edu.mta.security.jwt.JwtTokenProvider;
import edu.mta.utils.GeneralValue;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@Qualifier("AccountServiceImpl1")
public class AccountServiceImpl1 implements AccountService {

    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountDTOMapper accountDTOMapper;
    
    @Autowired
    private ModelMapper modelMapper;

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
        if (oldInfo == null) {
            return null;
        }

        this.accountRepository.save(account);
        return account;
    }

    @Override
    public void addUserInfo(User user) {
        String userInfo = createUserInfoString(user);

        Account account = this.accountRepository.findById(user.getId()).get();
        //need_change account.setUserInfo(userInfo);
        this.accountRepository.save(account);
        return;
    }

    @Override
    public Account findAccountByID(int id) {
        Optional<Account> account = this.accountRepository.findById(id);
        if (account == null) {
            return null;
        }

        return account.get();
    }

    @Override
    public boolean updateUserInfo(User user) {
        Optional<Account> oldInfo = this.accountRepository.findById(user.getId());

        if (oldInfo == null) {
            return false;
        }

        Account account = oldInfo.get();
        String userInfo = createUserInfoString(user);
        //need_change account.setUserInfo(userInfo);
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

    public String signin(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return jwtTokenProvider.createToken(username, accountRepository.getUserByUsername(username).getRoles());
        } catch (AuthenticationException e) {
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public String signup(AccountDataDTO accountDataDTO) {
        if (!accountRepository.existsByUsername(accountDataDTO.getUsername())) {
            Account account = accountDTOMapper.createFrom(accountDataDTO);
            account.setPassword(passwordEncoder.encode(accountDataDTO.getPassword()));
            account.setIsActive(1);
            User user = modelMapper.map(accountDataDTO.getUser(), User.class);
            user.setAccount(account);
            account.setUser(user);
            accountRepository.save(account);
            return jwtTokenProvider.createToken(account.getUsername(), account.getRoles());
        } else {
            throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Override
    public Account whoami(HttpServletRequest req) {
        return accountRepository.getUserByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
    }

    @Override
    public Account findAccountByEmail(String email) {
        Optional<Account> account = this.accountRepository.findByEmail(email);
        if (account == null) {
            return null;
        }
        return account.get();
    }

}
