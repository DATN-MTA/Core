package edu.mta.helper;

import edu.mta.dto.ClassRoomRequestDTO;
import edu.mta.model.Room;
import edu.mta.model.Class;
import edu.mta.service.ClassService;
import edu.mta.service.RoomService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class ClassRoomExcelHelper extends ExcelHelperAbstract{
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = {"Tên phòng", "Tên lớp", "Ngày trong tuần", "Giờ bắt đầu", "Giờ kết thúc"};

    @Override
    public String[] getHEADERs() {
        return HEADERs;
    }

    @Autowired
    private RoomService roomService;

    @Autowired
    private ClassService classService;

    public List<ClassRoomRequestDTO> excelToDTO(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            List<ClassRoomRequestDTO> classRoomRequestDTOList = new ArrayList<>();
            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();
                ClassRoomRequestDTO classRoomRequestDTO = new ClassRoomRequestDTO();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {

                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0:
                            Room room = roomService.findRoomByName(currentCell.getStringCellValue());
                            if (room != null) {
                                classRoomRequestDTO.setRoomID(room.getId());
                            } else {
                                classRoomRequestDTO.setRoomID(0);
                            }
                            break;
                        case 1:
                            Class classIns = classService.findClassByClassName(currentCell.getStringCellValue());
                            if (classIns != null) {
                                classRoomRequestDTO.setClassID(classIns.getId());
                            } else {
                                classRoomRequestDTO.setClassID(0);
                            }
                            break;
                        case 2:
                            classRoomRequestDTO.setWeekday((int)currentCell.getNumericCellValue());
                            break;
                        case 3:
                            classRoomRequestDTO.setBeginAt(currentCell.getStringCellValue());
                            break;
                        case 4:
                            classRoomRequestDTO.setFinishAt(currentCell.getStringCellValue());
                            break;
                        default:
                            break;
                    }

                    cellIdx++;
                }
                classRoomRequestDTOList.add(classRoomRequestDTO);
            }

            workbook.close();
            return classRoomRequestDTOList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }
}
