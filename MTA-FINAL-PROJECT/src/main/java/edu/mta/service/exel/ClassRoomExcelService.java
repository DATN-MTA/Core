package edu.mta.service.exel;

import edu.mta.dto.ClassRoomRequestDTO;
import edu.mta.helper.ClassRoomExcelHelper;
import edu.mta.service.ClassRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClassRoomExcelService extends ExcelServiceAbstract{
    @Autowired
    private ClassRoomExcelHelper classRoomExcelHelper;

    @Autowired
    private ClassRoomService classRoomService;

    public ByteArrayInputStream save(MultipartFile file) {
        try {
            List<String> resultList = new ArrayList<>();
            List<ClassRoomRequestDTO> classRoomRequestDTOS = classRoomExcelHelper.excelToDTO(file.getInputStream());
            for (ClassRoomRequestDTO classRoom : classRoomRequestDTOS) {
                String result = classRoomService.addNewClassRoom(classRoom);
                if (result == null) {
                    resultList.add("Success");
                } else {
                    resultList.add(result);
                }
            }
            return classRoomExcelHelper.writeExcelResultFiles(file.getInputStream(), resultList);
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }
}
