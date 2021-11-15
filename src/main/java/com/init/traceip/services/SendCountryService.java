package com.init.traceip.services;

import com.init.traceip.dto.CountryInfoDTO;
import com.init.traceip.entities.SendCountry;

import java.util.List;

public interface SendCountryService {

    List<SendCountry> findAll();

    SendCountry findByName(String name);

    CountryInfoDTO getCountryInfo(String ip);

    String findMinAndMaxDistanceCountries() throws Exception;
}
