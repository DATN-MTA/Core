package edu.mta.service.exel;

import edu.mta.helper.StudentClassExcelHelper;
import edu.mta.service.StudentClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentClassExcelService extends ExcelServiceAbstract{
    @Autowired
    private StudentClassService studentClassService;
    @Autowired
    StudentClassExcelHelper studentClassExcelHelper;

    public ByteArrayInputStream save(MultipartFile file, int classId) {
        try {
            List <String> resultList = new ArrayList<>();
            List<String> studentEmailList = studentClassExcelHelper.excelToDTO(file.getInputStream());
            for (String studentEmail : studentEmailList) {
                String result = studentClassService.addNewStudentClass(studentEmail, classId);
                if (result == null) {
                    resultList.add("Success");
                } else {
                    resultList.add(result);
                }
            }
            return studentClassExcelHelper.writeExcelResultFiles(file.getInputStream(), resultList);
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }
}
