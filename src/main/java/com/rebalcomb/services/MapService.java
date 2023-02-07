package com.rebalcomb.services;

import com.rebalcomb.models.DocumentXLSXData;
import com.rebalcomb.models.Location;
import com.rebalcomb.repositories.MapRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MapService {

    private final XLSXService xlsxService;
    private final CSVReaderService csvReaderService;
    private final MapRepository mapRepository;
    public static List<String> ipAdressList = new ArrayList<>();
    private static List<DocumentXLSXData> documentXLSXDataList;
    public final List<Location> locations = new ArrayList<>();

    @Autowired
    public MapService(XLSXService xlsxService, CSVReaderService csvReaderService, MapRepository mapRepository) {
        this.xlsxService = xlsxService;
        this.csvReaderService = csvReaderService;
        this.mapRepository = mapRepository;
        mainThread();
        loadDataAction();
    }

    public List<String> download() throws IOException, InterruptedException {
        if(ipAdressList.size() > 0)
            return ipAdressList;

        ipAdressList = mapRepository.findAllIp();
        return ipAdressList;
    }

    public void mainThread() {

        Thread threadUpdateUsers = new Thread(() -> {
            while (true) {
                try {
                    documentXLSXDataList = xlsxService.xlsxReader(new File("geoLocatorr.xlsx"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                for (DocumentXLSXData data : documentXLSXDataList) {
                    Optional<DocumentXLSXData> tmp = mapRepository.findByIp(data.getIp());
                    if(tmp.isEmpty())
                        mapRepository.save(data);
                }

                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        threadUpdateUsers.start();

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
            while (true) {
                try {
                    List<DocumentXLSXData> xlsxDataList = mapRepository.findAll();
                    for (DocumentXLSXData documentXLSXData : xlsxDataList) {
                        if (documentXLSXData.getLongitude() == null || documentXLSXData.getLongitude() == null) {
                            try {
                                documentXLSXData = csvReaderService.findDataByIp(documentXLSXData);
                                mapRepository.save(documentXLSXData);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }catch (Exception e){

                }
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        threadUpdateUsers.start();

    }

    @SneakyThrows
    public void saveDataToFile() {
        xlsxService.xlsxWriter(mapRepository.findAll());
    }
}
