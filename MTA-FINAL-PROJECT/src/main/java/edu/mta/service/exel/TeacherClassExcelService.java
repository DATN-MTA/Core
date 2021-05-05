package edu.mta.service.exel;

import edu.mta.dto.TeacherClassDTO;
import edu.mta.helper.TeacherClassExcelHelper;
import edu.mta.service.TeacherClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TeacherClassExcelService extends ExcelServiceAbstract{
    @Autowired
    private TeacherClassExcelHelper teacherClassExcelHelper;

    @Autowired
    private TeacherClassService teacherClassService;

    public ByteArrayInputStream save(MultipartFile file) {
        try {
            List<String> resultList = new ArrayList<>();
            List<TeacherClassDTO> teacherClassDTOList = teacherClassExcelHelper.excelToDTO(file.getInputStream());
            for (TeacherClassDTO teacherClassDTO : teacherClassDTOList) {
                String result = teacherClassService.addNewTeacherClass(teacherClassDTO);
                if (result == null) {
                    resultList.add("Success");
                } else {
                    resultList.add(result);
                }
            }
            return teacherClassExcelHelper.writeExcelResultFiles(file.getInputStream(), resultList);
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }
}
