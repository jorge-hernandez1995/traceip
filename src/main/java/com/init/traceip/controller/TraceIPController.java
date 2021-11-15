package com.init.traceip.controller;

import java.util.List;

import com.init.traceip.dto.ApiResponseDTO;
import com.init.traceip.dto.CountryInfoDTO;
import com.init.traceip.services.SendCountryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponseDTO<CountryInfoDTO>> getCountryInfo(@PathVariable String ip) {
        CountryInfoDTO dataResponse = sendCountryService.getCountryInfo(ip);
        return ResponseEntity.ok(ApiResponseDTO.<CountryInfoDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Country Info retrieved successfully")
                .data(dataResponse)
                .build());
    }

    @GetMapping("/countries")
    public ResponseEntity<ApiResponseDTO<List<SendCountry>>> findAll() {
        List<SendCountry> countryList = sendCountryService.findAll();
        return ResponseEntity.ok(ApiResponseDTO.<List<SendCountry>>builder()
                .code(HttpStatus.OK.value())
                .message("Country List retrieved successfully")
                .data(countryList)
                .build());
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
