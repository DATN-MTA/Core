package edu.mta.service.exel;

import edu.mta.dto.AccountDataDTO;
import edu.mta.helper.AccountExcelHelper;
import edu.mta.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class AccountExcelService {
    @Autowired
    private AccountService accountService;
    @Autowired
    AccountExcelHelper accountExcelHelper;

    public void save(MultipartFile file) {
        try {
            List<AccountDataDTO> accountDataDTOList = accountExcelHelper.exelToAccountDataDTO(file.getInputStream());
            for (AccountDataDTO accountDataDTO : accountDataDTOList) {
                accountService.signup(accountDataDTO);
            }
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }

    public ByteArrayInputStream load() {
        ByteArrayInputStream in = accountExcelHelper.generateExcel();
        return in;
    }
}