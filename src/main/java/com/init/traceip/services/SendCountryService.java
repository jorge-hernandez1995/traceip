package com.init.traceip.services;

import com.init.traceip.entities.SendCountry;

import java.util.List;

public interface SendCountryService {

    List<SendCountry> findAll();

    SendCountry findByName(String name);

    String getCountryInfo(String ip);

    String findMinAndMaxDistanceCountries();
}
