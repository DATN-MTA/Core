package edu.mta.helper;

import edu.mta.dto.AccountDataDTO;
import edu.mta.dto.UserDTO;
import edu.mta.model.Role;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class AccountExcelHelper extends ExcelHelperAbstract{
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = {"userName", "email", "password", "roles", "fullName", "address", "birthDay", "phone"};
    static String SHEET = "Account";

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public String[] getHEADERs() {
        return HEADERs;
    }

    public static boolean hasExcelFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }

    public List<AccountDataDTO> exelToAccountDataDTO(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);

            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();

            List<AccountDataDTO> accountDataDTOS = new ArrayList();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                AccountDataDTO accountDataDTO = new AccountDataDTO();
                UserDTO userDTO = new UserDTO();
                int cellIdx = 0;
                while (cellsInRow.hasNext()) {

                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0:
                            accountDataDTO.setUsername(currentCell.getStringCellValue());
                            break;

                        case 1:
                            accountDataDTO.setEmail(currentCell.getStringCellValue());
                            break;
                        case 2:
                            accountDataDTO.setPassword(currentCell.getStringCellValue());
                            break;
                        case 3:
                            List<Role> roleList = new ArrayList<>();
                            String[] roleInput = currentCell.getStringCellValue().split(";");
                            for (String role : roleInput) {
                                roleList.add(modelMapper.map(role, Role.class));
                            }
                            accountDataDTO.setRoles(roleList);
                            break;

                        case 4:
                            userDTO.setFullName(currentCell.getStringCellValue());
                            break;
                        case 5:
                            userDTO.setAddress(currentCell.getStringCellValue());
                            break;
                        case 6:
                            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                            userDTO.setBirthDay(LocalDate.parse(currentCell.getStringCellValue(), dateFormat));
                            break;
                        case 7:
                            userDTO.setPhone(currentCell.getStringCellValue());
                            break;
                        default:
                            break;
                    }

                    cellIdx++;
                }
                accountDataDTO.setUser(userDTO);
                accountDataDTOS.add(accountDataDTO);
            }

            workbook.close();

            return accountDataDTOS;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }
}
