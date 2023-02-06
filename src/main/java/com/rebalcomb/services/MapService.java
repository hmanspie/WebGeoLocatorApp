package com.rebalcomb.services;

import com.rebalcomb.models.DocumentXLSXData;
import com.rebalcomb.models.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MapService {

    private final XLSXService xlsxService;
    private final CSVReaderService csvReaderService;
    public static final List<String> ipAdressList = new ArrayList<>();
    private static List<DocumentXLSXData> documentXLSXDataList;

    public final List<Location> locations = new ArrayList<>();

    @Autowired
    public MapService(XLSXService xlsxService, CSVReaderService csvReaderService) {
        this.xlsxService = xlsxService;
        this.csvReaderService = csvReaderService;
    }

    public List<String> download() throws IOException, InterruptedException {
        if(ipAdressList.size() > 0)
            return ipAdressList;
        documentXLSXDataList = xlsxService.xlsxReader(new File("geoLocatorr.xlsx"));
        loadDataAction();
        for (DocumentXLSXData documentXLSXData : documentXLSXDataList)
            ipAdressList.add(documentXLSXData.getIp());
        return ipAdressList;
    }

    public List<Location> addMarker(String ip) throws IOException {
        for (DocumentXLSXData documentXLSXData : documentXLSXDataList) {
            if (documentXLSXData.getIp().equals(ip)) {
                locations.add(setNewMarker(documentXLSXData));
                return locations;
            }
        }
        return null;
    }

    private Location setNewMarker(DocumentXLSXData documentXlsxData) throws IOException {
        if(documentXlsxData.getLatitude() == null || documentXlsxData.getLongitude() == null)
            documentXlsxData = csvReaderService.findDataByIp(documentXlsxData);

        return
                new Location(new double[]{Double.parseDouble(documentXlsxData.getLongitude()), Double.parseDouble(documentXlsxData.getLatitude())}, "<h2>" + documentXlsxData.getIp() + "</h2>"
                        + "Servername:    " + documentXlsxData.getServername() + "<br>"
                        + "Security:      " + documentXlsxData.getSecurity() + "<br>"
                        + "BSSID:         " + documentXlsxData.getBssid() + "<br>"
                        + "Country code:  " + documentXlsxData.getCode() + "<br>"
                        + "Country:       " + documentXlsxData.getCountry() + "<br>"
                        + "City:          " + documentXlsxData.getCity() + "<br>"
                        + "Region:        " + documentXlsxData.getRegion() + "<br>"
                        + "Zip code:      " + documentXlsxData.getZipcode() + "<br>"
                        + "Timezone:      " + documentXlsxData.getTimezone() + "<br>"
                        + "Latitude:      " + documentXlsxData.getLatitude() + "<br>"
                        + "Longitude:     " + documentXlsxData.getLongitude());
    }

    public void loadDataAction() {
        Thread threadUpdateUsers = new Thread(() -> {
            double step = 1.0 / Double.valueOf(documentXLSXDataList.size());
            for (DocumentXLSXData documentXLSXData : documentXLSXDataList) {
                if (documentXLSXData.getLongitude() == null || documentXLSXData.getLongitude() == null) {
                    try {
                        documentXLSXData = csvReaderService.findDataByIp(documentXLSXData);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            try {
                xlsxService.xlsxWriter(documentXLSXDataList);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        threadUpdateUsers.start();

    }
}
