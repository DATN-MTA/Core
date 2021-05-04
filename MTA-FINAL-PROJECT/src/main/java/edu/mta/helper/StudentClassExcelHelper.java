package edu.mta.helper;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class StudentClassExcelHelper extends ExcelHelperAbstract{
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = {"Email *"};

    @Override
    public String[] getHEADERs() {
        return HEADERs;
    }

    @Autowired
    private ModelMapper modelMapper;

    public List<String> excelToDTO(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            List<String> emailStudentList = new ArrayList<>();
            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();


                int cellIdx = 0;
                while (cellsInRow.hasNext()) {

                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0:
                            emailStudentList.add(currentCell.getStringCellValue());
                            break;
                        default:
                            break;
                    }

                    cellIdx++;
                }
            }

            workbook.close();
            return emailStudentList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }


}
