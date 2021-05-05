package edu.mta.service.exel;

import edu.mta.helper.ExcelHelperAbstract;

import java.io.ByteArrayInputStream;

public class ExcelServiceAbstract {

    public ByteArrayInputStream load(ExcelHelperAbstract excelHelperAbstract) {
        ByteArrayInputStream in = excelHelperAbstract.generateExcel();
        return in;
    }
}
