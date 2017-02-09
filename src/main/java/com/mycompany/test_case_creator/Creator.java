/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.test_case_creator;

import com.mycompany.test_case_creator.session_factory.Session;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.xssf.usermodel.*;

/**
 *
 * @author joseramon.gago
 */
public class Creator {
    private XSSFWorkbook wb;
    private XSSFSheet currentSheet;
    private final Session session;
    private Map<String, Integer> headers;
    
    public Creator(Session session) {
        this.session = session;
        wb = new XSSFWorkbook();
        headers = new HashMap<>();
    }
    
    public void create(String xlsxPath) throws Exception, FileNotFoundException, IOException {
        InputStream is = new FileInputStream(xlsxPath);
        wb = new XSSFWorkbook(is);
        
        getHeaders();
        
        int lastRow = currentSheet.getLastRowNum() - 1;
        lastRow = 36; // Quitar 
        for (int i = 1;currentSheet.getRow(i) != null && i < lastRow; i++) {
            XSSFRow currentRow = currentSheet.getRow(i);
            
            if (currentRow.getCell(headers.get("Subject")).getStringCellValue().trim().isEmpty())
                continue;
            
            if (i % 3 == 0) {
                String pathCase = currentRow.getCell(headers.get("Subject")).getStringCellValue().trim();
                String testName = currentRow.getCell(headers.get("Test Name")).getStringCellValue().trim();
                String pathEvidence = currentRow.getCell(headers.get("Evidences")).getStringCellValue().trim();
                String status = currentRow.getCell(headers.get("Exec_Status")).getStringCellValue().trim();
                session.update(pathCase, testName, status, pathEvidence);
            }
        }
    }
    
    private void getHeaders() {
        currentSheet = wb.getSheetAt(0);
        XSSFRow headerRow = currentSheet.getRow(0);
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            headers.put(headerRow.getCell(i).getStringCellValue().trim(), i);
        }
    } 
}
