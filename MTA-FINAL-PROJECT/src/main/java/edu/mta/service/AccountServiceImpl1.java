package edu.mta.service;

import edu.mta.common.PasswordUtil;
import edu.mta.dto.AccountDataDTO;
import edu.mta.dto.UserDataResponseDTO;
import edu.mta.exception.CustomException;
import edu.mta.mapper.AccountDTOMapper;
import edu.mta.model.Account;
import edu.mta.model.User;
import edu.mta.repository.AccountRepository;
import edu.mta.repository.UserRepository;
import edu.mta.security.jwt.JwtTokenProvider;
import edu.mta.service.mail.EmailService;
import edu.mta.utils.GeneralValue;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
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
    private EmailService emailService;

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
    public Account findAccountByID(int id) {
        Optional<Account> account = this.accountRepository.findById(id);
        if (account == null) {
            return null;
        }

        return account.get();
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
            String password = PasswordUtil.randomPassword();
            account.setPassword(passwordEncoder.encode(password));
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
    public boolean activeOrDeactivateAccount(List<Integer> acountIds, Integer status) {
        for (Integer accountId : acountIds) {
            Account account = accountRepository.getOne(accountId);
            account.setIsActive(status);
            accountRepository.save(account);
        }
        return true;
    }

    @Override
    public Page<Account> getAllAccount(Pageable page, String emailOrUserName) {
        if (emailOrUserName == null)
        return accountRepository.findAll(page);
        return accountRepository.findByUsernameContainingOrEmailContaining(emailOrUserName, page);
    }

    @Override
    public Account findAccountByEmail(String email) {
        Optional<Account> account = this.accountRepository.findByEmail(email);
        if (account == null) {
            return null;
        }
        return account.get();
    }

    @Override
    public boolean updatePassword(String email, String password, HttpServletRequest req) {
        Account temp = null;
        Optional<Account> optionalAccount;
        if (email != null && !email.isEmpty()) {
            optionalAccount= accountRepository.findByEmail(email);
            if (optionalAccount.isPresent()) {
                temp = optionalAccount.get();
            }
        } else {
            temp = accountRepository.getUserByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
        }
        if (temp != null) {
            temp.setPassword(passwordEncoder.encode(password));
            accountRepository.save(temp);
            return true;
        }
        return false;
    }

    @Override
    public boolean forceUpdatePassword(String emailToUpdate, HttpServletRequest req) {
        try {
            emailService.sendResetPasswordMail(emailToUpdate, req);
            return true;
        } catch (Exception ex) {
            throw new CustomException("Account not found", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Override
    public boolean updateUserInfo(UserDataResponseDTO userDataDTO) {
        Account account = accountRepository.findByUserId(userDataDTO.getId());
        if (account != null) {
            account.setUser(modelMapper.map(userDataDTO, User.class));
            accountRepository.save(account);
            return true;
        } else {
            throw new CustomException("User not found", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
