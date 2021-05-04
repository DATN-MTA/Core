package edu.mta.controller;

import edu.mta.common.Constants;
import edu.mta.common.PasswordUtil;
import edu.mta.dto.AccountDataDTO;
import edu.mta.dto.AccountResponseDTO;
import edu.mta.dto.ResetPasswordRequest;
import edu.mta.dto.UserDataResponseDTO;
import edu.mta.exception.CustomException;
import edu.mta.helper.AccountExcelHelper;
import edu.mta.model.Account;
import edu.mta.service.AccountService;
import edu.mta.service.exel.AccountExcelService;
import edu.mta.service.mail.AccessTokenService;
import edu.mta.service.mail.EmailService;
import edu.mta.utils.FrequentlyUtils;
import edu.mta.utils.ValidationAccountData;
import edu.mta.utils.ValidationData;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
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
    private AccountExcelHelper accountExcelHelper;

    @Autowired
    private AccountExcelService accountExcelService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AccessTokenService accessTokenService;


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

    @PostMapping("/signin")
    @ApiResponses(value = {//
            @ApiResponse(code = 204, message = "No data founded"), //
            @ApiResponse(code = 400, message = "Invalidate data request"), //
            @ApiResponse(code = 422, message = "Invalid username/password supplied or account has been deactivated")})
    public String login(//
                        @ApiParam("Username") @RequestParam String username, //
                        @ApiParam("Password") @RequestParam String password) {
        return accountService.signin(username, password);
    }

    @PostMapping("/signup")
    @ApiOperation(value = "${UserController.signup}")
    @ApiResponses(value = {//
            @ApiResponse(code = 204, message = "No data founded"), //
            @ApiResponse(code = 400, message = "Invalidate data request"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 422, message = "Username is already in use")})
    public String signup(@ApiParam("Signup User") @RequestBody AccountDataDTO accountDataDTO) {
        return accountService.signup(accountDataDTO);
    }

    @GetMapping(value = "/getCurrentUser")
    @ApiOperation(value = "${UserController.me}", response = AccountResponseDTO.class, authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {//
            @ApiResponse(code = 204, message = "No data founded"), //
            @ApiResponse(code = 400, message = "Invalidate data request"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public AccountResponseDTO whoami(HttpServletRequest req) {
        Account account = accountService.whoami(req);
        AccountResponseDTO accountResponseDTO = modelMapper.map(account, AccountResponseDTO.class);
        accountResponseDTO.setUserDTO(modelMapper.map(account.getUser(), UserDataResponseDTO.class));
        return accountResponseDTO;
    }

    @GetMapping(value = "/getUserInfo")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {//
            @ApiResponse(code = 204, message = "No data founded"), //
            @ApiResponse(code = 400, message = "Invalidate data request"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public AccountResponseDTO getUserInfo(@RequestParam(value = "accountId", required = true) Integer accountId) {
        Account account = accountService.getUserInfo(accountId);
        AccountResponseDTO accountResponseDTO = modelMapper.map(account, AccountResponseDTO.class);
        accountResponseDTO.setUserDTO(modelMapper.map(account.getUser(), UserDataResponseDTO.class));
        return accountResponseDTO;
    }

    @GetMapping(value = "/getAllAccounts")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {//
            @ApiResponse(code = 204, message = "No data founded"), //
            @ApiResponse(code = 400, message = "Invalidate data request"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> getAllAccount(@RequestParam(required = false) Integer page,
                                           @RequestParam(required = false) Integer pageSize,
                                           @RequestParam(value = "emailOrUserName", required = false) String emailOrUserName) {
        Pageable pageRequest = PageRequest.of(page != null ? page : 0, pageSize != null ? pageSize : 5);
        Page<Account> pageAccounts = this.accountService.getAllAccount(pageRequest != null ? pageRequest : null, emailOrUserName);
        List<Account> accountList = pageAccounts.getContent();
        List<AccountResponseDTO> accountResponseDTOList = new ArrayList<>();
        for (Account account : accountList) {
            AccountResponseDTO accountResponseDTO = modelMapper.map(account, AccountResponseDTO.class);
            accountResponseDTO.setUserDTO(modelMapper.map(account.getUser(), UserDataResponseDTO.class));
            accountResponseDTOList.add(accountResponseDTO);
        }
        if (pageAccounts == null) {
            throw new CustomException("Not found data", HttpStatus.NO_CONTENT);
        } else {
            Map<String, Object> response = new HashMap<>();
            if (!pageAccounts.isEmpty()) {
                response.put("data", accountResponseDTOList);
                response.put("totalPages", pageAccounts.getTotalPages());
                response.put("totalItems", pageAccounts.getTotalElements());
                response.put("currentPage", pageAccounts.getNumber());
                return ResponseEntity.ok(response);
            }
        }
        return null;
    }

    @RequestMapping(value = "/deactivateAccount", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {//
            @ApiResponse(code = 204, message = "No data founded"), //
            @ApiResponse(code = 400, message = "Invalidate data request"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> disableAccount(@RequestBody List<Integer> accountIds) {
        return ResponseEntity.ok(accountService.activeOrDeactivateAccount(accountIds, Constants.accoutStatusInActive));
    }

    @RequestMapping(value = "/activeOrInactiveSingleAccount", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {//
            @ApiResponse(code = 204, message = "No data founded"), //
            @ApiResponse(code = 400, message = "Invalidate data request"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> activeOrInactiveSingleAccount(@RequestBody Integer accountId) {
        return ResponseEntity.ok(accountService.activeOrInactiveSingleAccount(accountId, Constants.accoutStatusInActive));
    }

    @RequestMapping(value = "/activateAccount", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {//
            @ApiResponse(code = 204, message = "No data founded"), //
            @ApiResponse(code = 400, message = "Invalidate data request"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> activateAccount(@RequestBody List<Integer> accountIds) {
        return ResponseEntity.ok(accountService.activeOrDeactivateAccount(accountIds, Constants.accoutStatusActive));
    }

    @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
    @ApiResponses(value = {//
            @ApiResponse(code = 204, message = "No data founded"), //
            @ApiResponse(code = 400, message = "Invalidate data request"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public boolean updatePassword(@RequestBody ResetPasswordRequest resetPasswordRequest, HttpServletRequest req) {
        if (!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmPassword())) {
            throw new CustomException("password and confirm pwd is not equals", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (StringUtils.isEmpty(resetPasswordRequest.getNewPassword())) {
            throw new CustomException("password or confirm password is not presented", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return accountService.updatePassword(resetPasswordRequest.getEmail(), resetPasswordRequest.getNewPassword(), req);
    }

    @RequestMapping(path = "/forceUpdatePassword", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {//
            @ApiResponse(code = 204, message = "No data founded"), //
            @ApiResponse(code = 400, message = "Invalidate data request"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public boolean forceUpdatePassword(@RequestParam(required = true) String email, @Context HttpServletRequest req) {
        return accountService.forceUpdatePassword(email, req);
    }

    @RequestMapping(path = "/updatePasswordAfterForgot", method = RequestMethod.POST)
    public ResponseEntity<?> updatePasswordAfterForgot(@RequestBody ResetPasswordRequest resetPasswordRequest, @Context HttpServletRequest req) {
        Account account = accountService.findAccountByEmail(resetPasswordRequest.getEmail());
        if (StringUtils.isEmpty(resetPasswordRequest.getNewPassword())) {
            throw new CustomException("password and confirm pwd is not equals", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (accessTokenService.isEligible(account, resetPasswordRequest.getToken())) {
            accountService.updatePassword(resetPasswordRequest.getEmail(), resetPasswordRequest.getNewPassword(), req);
            return ResponseEntity.ok("Succeed! Password has been updated");
        }
        throw new CustomException("Account Not Found", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @RequestMapping(value = "/forgotMyPassword", method = RequestMethod.POST)
    public ResponseEntity<?> sendConformationMailTo(@RequestBody String sendConformationMailTo, @Context HttpServletResponse res, @Context HttpServletRequest req) {
        if (!PasswordUtil.emailValidator(sendConformationMailTo)) {
            throw new CustomException("*Plaese enter a valid email address", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (emailService.sendResetPasswordMail(sendConformationMailTo, req)) {
            return ResponseEntity.ok("Succeed! A confirmation linked has been sent to email");
        }
        throw new CustomException("*Email has not registered", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.PUT)
    @ApiResponses(value = {//
            @ApiResponse(code = 204, message = "No data founded"), //
            @ApiResponse(code = 400, message = "Invalidate data request"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> updateUserInfo(@RequestBody UserDataResponseDTO userDataDTO) {
        if (accountService.updateUserInfo(userDataDTO)) {
            return ResponseEntity.ok("Succeed! User info has been updated");
        } else {
            throw new CustomException("*Failed", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @RequestMapping(value = "/uploadFileToCreateMultipleAccounts", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {//
            @ApiResponse(code = 204, message = "No data founded"), //
            @ApiResponse(code = 400, message = "Invalidate data request"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";

        if (AccountExcelHelper.hasExcelFormat(file)) {
            try {
                accountExcelService.save(file);

                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body("Upload file succeed");
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Could not upload the file");
            }
        }

        message = "Please upload an excel file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not upload the file");
    }

    @RequestMapping(path = "/downloadTemplateUploadAccounts", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {//
            @ApiResponse(code = 204, message = "No data founded"), //
            @ApiResponse(code = 400, message = "Invalidate data request"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<Resource> getFile() {
        String filename = "Account_Template.xlsx";
        InputStreamResource file = new InputStreamResource(accountExcelService.load(accountExcelHelper));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }
}
