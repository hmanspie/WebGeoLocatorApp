package com.rebalcomb.services;

import com.rebalcomb.models.DocumentXLSXData;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class XLSXService {

    public void xlsxWriter(List<DocumentXLSXData> documentXLSXDataList) throws IOException, InterruptedException {
        Map<String, Object[]> data = new TreeMap<>();
        int counter = 1;
        data.put("0", new Object[]{"IP Address", "Port", "Time (ms)", "Status", "Authorization", "Servername / Device type", "Radio off", "Hidden"
                ,"  BSSID  ", "  ESSID  ", "Security","   Key   ", "WPS PIN", "LAN IP Address","LAN Subnet Mask", "WAN IP Address", "WAN Subnet Mask", "WAN Gateway","Domain Name Servers"
                ,"Country code", "Country name","Region name", "City name", "Latitude","Longitude", "Zip code", "Time zone"});
        Thread.sleep(500);
        for (DocumentXLSXData documentXLSXData : documentXLSXDataList) {
            data.put(String.valueOf(counter++), new Object[]{documentXLSXData.getIp(), documentXLSXData.getPort(), documentXLSXData.getTime(), documentXLSXData.getStatus()
                    , documentXLSXData.getAuthorization(), documentXLSXData.getServername(), documentXLSXData.getRadioOff(), documentXLSXData.getHidden(), documentXLSXData.getBssid()
                    , documentXLSXData.getEssid(), documentXLSXData.getSecurity(), documentXLSXData.getKeyy(), documentXLSXData.getWps(), documentXLSXData.getLanIp(), documentXLSXData.getLanMask()
                    , documentXLSXData.getWanIp(), documentXLSXData.getWanMask(), documentXLSXData.getWanGate(), documentXLSXData.getDomain(), documentXLSXData.getCode(), documentXLSXData.getCountry()
                    , documentXLSXData.getRegion(), documentXLSXData.getCity(), documentXLSXData.getLatitude(), documentXLSXData.getLongitude(), documentXLSXData.getZipcode(), documentXLSXData.getTimezone()});
        }

        XSSFWorkbook myWorkBook = new XSSFWorkbook ();
        XSSFSheet mySheet = myWorkBook.createSheet();


        Set<String> newRows = data.keySet();
        int rownum = 0;
        for (String key : newRows) {
            Row row = mySheet.createRow(rownum++);

            Object[] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if(row.getRowNum() == 0)
                    mySheet.setColumnWidth(cell.getColumnIndex(), String.valueOf(obj).toCharArray().length * 800);
                cell.setCellValue((String) obj);
                if(row.getRowNum() == 0)
                    cell.setCellStyle(buildDefaultCellStyleHeader(myWorkBook));
                else
                    cell.setCellStyle(buildDefaultCellStyle(myWorkBook));
            }
        }
        FileOutputStream os = new FileOutputStream("geoLocatorr.xlsx");
        myWorkBook.write(os);
    }

    private CellStyle buildDefaultCellStyle(Workbook workbook) {
        CellStyle newCellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short)12);
        font.setBold(false);
        newCellStyle.setFont(font);
        newCellStyle.setWrapText(false);
        newCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        newCellStyle.setAlignment(HorizontalAlignment.CENTER);
        newCellStyle.setLocked(true);
        newCellStyle.setFillPattern(FillPatternType.NO_FILL);
        newCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        newCellStyle.setBorderBottom(BorderStyle.THIN);
        newCellStyle.setBorderLeft(BorderStyle.THIN);
        newCellStyle.setBorderRight(BorderStyle.THIN);
        newCellStyle.setBorderTop(BorderStyle.THIN);
        return newCellStyle;
    }

    private CellStyle buildDefaultCellStyleHeader(Workbook workbook) {
        CellStyle newCellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short)12);
        font.setBold(false);
        newCellStyle.setFont(font);
        newCellStyle.setWrapText(false);
        newCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        newCellStyle.setAlignment(HorizontalAlignment.CENTER);
        newCellStyle.setLocked(true);
        newCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        newCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        newCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        newCellStyle.setBorderLeft(BorderStyle.MEDIUM);
        newCellStyle.setBorderRight(BorderStyle.MEDIUM);
        return newCellStyle;
    }

    public List<DocumentXLSXData> xlsxReader(File inputFile) throws IOException {
        String [] array = new String[28];
        List<DocumentXLSXData> documentXLSXDataList = new ArrayList<>();
        FileInputStream fis = new FileInputStream(inputFile);
        ZipSecureFile.setMinInflateRatio(0);
        XSSFWorkbook myWorkBook = new XSSFWorkbook (fis);
        XSSFSheet mySheet = myWorkBook.getSheetAt(0);
        Iterator<Row> rowIterator = mySheet.iterator();
        rowIterator.next();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            array = new String[28];
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                CellType cellType = cell.getCellType();
                if(cellType.equals(CellType.STRING))
                    array[cell.getColumnIndex()] = cell.getStringCellValue();
                else if(cellType.equals(CellType.NUMERIC))
                    array[cell.getColumnIndex()] = String.valueOf(cell.getNumericCellValue());
                else if(cellType.equals(CellType.BOOLEAN))
                    array[cell.getColumnIndex()] = String.valueOf(cell.getBooleanCellValue());
            }
            if(array[0] != null)
                documentXLSXDataList.add(documentXLSXDataMapper(array));
        }
        return documentXLSXDataList;
    }
    private DocumentXLSXData documentXLSXDataMapper(String [] array) {
        DocumentXLSXData documentXLSXData = new DocumentXLSXData();
        int counter = 0;
        documentXLSXData.setIp(array[counter++]);
        documentXLSXData.setPort(array[counter++]);
        documentXLSXData.setTime(array[counter++]);
        documentXLSXData.setStatus(array[counter++]);
        documentXLSXData.setAuthorization(array[counter++]);
        documentXLSXData.setServername(array[counter++]);
        documentXLSXData.setRadioOff(array[counter++]);
        documentXLSXData.setHidden(array[counter++]);
        documentXLSXData.setBssid(array[counter++]);
        documentXLSXData.setEssid(array[counter++]);
        documentXLSXData.setSecurity(array[counter++]);
        documentXLSXData.setKeyy(array[counter++]);
        documentXLSXData.setWps(array[counter++]);
        documentXLSXData.setLanIp(array[counter++]);
        documentXLSXData.setLanMask(array[counter++]);
        documentXLSXData.setWanIp(array[counter++]);
        documentXLSXData.setWanMask(array[counter++]);
        documentXLSXData.setWanGate(array[counter++]);
        documentXLSXData.setDomain(array[counter++]);
        documentXLSXData.setCode(array[counter++]);
        documentXLSXData.setCountry(array[counter++]);
        documentXLSXData.setRegion(array[counter++]);
        documentXLSXData.setCity(array[counter++]);
        documentXLSXData.setLatitude(array[counter++]);
        documentXLSXData.setLongitude(array[counter++]);
        documentXLSXData.setZipcode(array[counter++]);
        documentXLSXData.setTimezone(array[counter++]);
        return documentXLSXData;
    }
}
