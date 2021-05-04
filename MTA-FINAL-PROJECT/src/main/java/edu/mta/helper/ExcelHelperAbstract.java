package edu.mta.helper;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public abstract class ExcelHelperAbstract {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public String[] HEADERs;

    public void setHEADERs(String[] HEADERs) {
        this.HEADERs = HEADERs;
    }

    public abstract String[] getHEADERs();
    public void init() {
        HEADERs = getHEADERs();
    }

    public ExcelHelperAbstract() {
        init();
    }

    public static boolean hasExcelFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }

    public ByteArrayInputStream generateExcel() {

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet();

            // Header
            Row headerRow = sheet.createRow(0);

            for (int col = 0; col < HEADERs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellStyle(setHEADERStyle(workbook, sheet));
                cell.setCellValue(HEADERs[col]);
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }

    private CellStyle setHEADERStyle(Workbook workbook, Sheet sheet) {
        Font headerFont = workbook.createFont();
        headerFont.setColor(IndexedColors.WHITE.index);
        CellStyle headerCellStyle = sheet.getWorkbook().createCellStyle();
        // fill foreground color ...
        headerCellStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.index);
        // and solid fill pattern produces solid grey cell fill
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle.setFont(headerFont);
        return headerCellStyle;
    }

    public ByteArrayInputStream writeExcelResultFiles(InputStream is, List<String> resultList) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Workbook workbook = new XSSFWorkbook(is);

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            int rowNumber = 0;
            for (String result : resultList) {
                while (rows.hasNext()) {
                    Row currentRow = rows.next();
                    Cell cell = currentRow.createCell(currentRow.getLastCellNum(), CellType.STRING);
                    // skip header
                    if (rowNumber == 0) {
                        rowNumber++;
                        cell.setCellStyle(setHEADERStyle(workbook,sheet));
                        cell.setCellValue("Import_result");
                        continue;
                    }
                    cell.setCellValue(result);
                }

            }
            workbook.write(out);
            workbook.close();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to write result Excel file: " + e.getMessage());
        }
    }
}
