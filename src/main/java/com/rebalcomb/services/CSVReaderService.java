package com.rebalcomb.services;

import com.rebalcomb.models.DocumentXLSXData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

@Service
public class CSVReaderService {

    private final IPAddressService ipAddressService;

    @Autowired
    public CSVReaderService(IPAddressService ipAddressService) {
        this.ipAddressService = ipAddressService;
    }

    public DocumentXLSXData findDataByIp(DocumentXLSXData documentXLSXData) throws IOException {
        Reader in = new FileReader("IP2LOCATION-LITE-DB11.CSV");
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);
        long ipLong = ipAddressService.ipToLong(documentXLSXData.getIp());
        boolean flag = false;
        for (CSVRecord record : records) {
            if(!flag)
                flag = true;
            else {
                String from = record.get(0);
                String to = record.get(1);
                if(Long.parseLong(from) < ipLong && ipLong < Long.parseLong(to)){
                    documentXLSXData.setCode(record.get(2));
                    documentXLSXData.setCountry(record.get(3));
                    documentXLSXData.setRegion(record.get(4));
                    documentXLSXData.setCity(record.get(5));
                    documentXLSXData.setLatitude(record.get(6));
                    documentXLSXData.setLongitude(record.get(7));
                    documentXLSXData.setZipcode(record.get(8));
                    documentXLSXData.setTimezone(record.get(9));
                }
            }
        } return documentXLSXData;
    }
}
