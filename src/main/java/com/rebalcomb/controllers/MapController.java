package com.rebalcomb.controllers;

import com.rebalcomb.dto.IpRequest;
import com.rebalcomb.services.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
@RequestMapping("/map")
public class MapController {

  @Value("${tomtom.apikey}")
  private String tomTomApiKey;

  private final MapService mapService;

  @Autowired
  public MapController(MapService mapService) {
    this.mapService = mapService;
  }

  @GetMapping("/home")
  public ModelAndView homePage(ModelAndView model) {
      model.setViewName("home");
      model.addObject("apikey", tomTomApiKey);
      model.addObject("ipRequest", new IpRequest());
      mapService.locations.clear();
      MapService.ipAdressList.clear();
      return model;
  }

    @GetMapping("/download")
    public ModelAndView downloadFile(ModelAndView model) throws IOException, InterruptedException {
        model.setViewName("home");
        model.addObject("apikey", tomTomApiKey);
        model.addObject("ipRequest", new IpRequest());
        model.addObject("ipList", mapService.download());
        return model;
    }

    @PostMapping("/selected")
    public ModelAndView selected(ModelAndView model,  IpRequest ipRequest) throws IOException, InterruptedException {
        model.setViewName("home");
        model.addObject("apikey", tomTomApiKey);
        model.addObject("ipRequest", new IpRequest());
        model.addObject("ipList", mapService.download());
        model.addObject("coolLocations", mapService.addMarker(ipRequest.getIp()));
        return model;
    }

    @GetMapping("/save")
    public ModelAndView save(ModelAndView model) throws IOException, InterruptedException {
        model.setViewName("home");
        model.addObject("apikey", tomTomApiKey);
        model.addObject("ipRequest", new IpRequest());
        model.addObject("ipList", mapService.download());
        model.addObject("coolLocations", mapService.locations);
        mapService.saveDataToFile();
        return model;
    }

}
