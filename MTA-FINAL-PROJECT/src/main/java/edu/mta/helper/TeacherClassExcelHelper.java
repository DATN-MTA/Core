package edu.mta.helper;

import edu.mta.dto.TeacherClassDTO;
import edu.mta.model.Class;
import edu.mta.service.ClassService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class TeacherClassExcelHelper extends ExcelHelperAbstract {
    static String[] HEADERs = {"Email *", "Tên lớp"};
    @Override
    public String[] getHEADERs() {
        return HEADERs;
    }

    @Autowired
    ClassService classService;

    public List<TeacherClassDTO> excelToDTO(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            List<TeacherClassDTO> teacherClassDTOList = new ArrayList<>();
            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();
                TeacherClassDTO teacherClassDTO = new TeacherClassDTO();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {

                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0:
                            if (currentCell == null || currentCell.getCellType() == CellType.BLANK || currentCell.getCellType() != CellType.STRING) {
                               teacherClassDTO.setTeacherEmail(null);
                            } else {
                                teacherClassDTO.setTeacherEmail(currentCell.getStringCellValue());
                            }
                            break;
                        case 1:
                            Class classIns = classService.findClassByClassName(currentCell.getStringCellValue());
                            if (classIns != null) {
                                teacherClassDTO.setClassID(classIns.getId());
                            } else {
                                teacherClassDTO.setClassID(0);
                            }
                            break;
                        default:
                            break;
                    }

                    cellIdx++;
                }
                teacherClassDTOList.add(teacherClassDTO);
            }

            workbook.close();
            return teacherClassDTOList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }
}
