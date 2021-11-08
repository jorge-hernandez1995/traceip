package com.init.traceip.controller;

import java.util.List;

import com.init.traceip.services.SendCountryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.init.traceip.entities.SendCountry;


@Slf4j
@RestController
public class TraceIPController {

    private final SendCountryService sendCountryService;

    public TraceIPController(SendCountryService sendCountryService) {
        this.sendCountryService = sendCountryService;
    }


    @GetMapping("/traceip/{ip}")
    public String getCountryInfo(@PathVariable String ip) {
        return sendCountryService.getCountryInfo(ip);
    }

    @GetMapping("/countries")
    public List<SendCountry> findAll() {
        return sendCountryService.findAll();
    }

    @GetMapping("/country/{id}")
    public SendCountry findByName(@PathVariable String id) {
        return sendCountryService.findByName(id);
    }

    @GetMapping("/statistics")
    public String getStatistics() throws Exception {
        return sendCountryService.findMinAndMaxDistanceCountries();
    }

}
