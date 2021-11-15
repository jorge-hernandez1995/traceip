package com.init.traceip.services.impl;

import com.init.traceip.dto.CountryInfoDTO;
import com.init.traceip.entities.Country;
import com.init.traceip.entities.CountryInfo;
import com.init.traceip.entities.Language;
import com.init.traceip.entities.SendCountry;
import com.init.traceip.repository.SendCountryRepository;
import com.init.traceip.services.SendCountryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.init.traceip.constants.TraceIPConstants.*;

@Slf4j
@Service
public class SendCountryServiceImpl implements SendCountryService {

    private final SendCountryRepository sendCountryRepository;

    public SendCountryServiceImpl(SendCountryRepository sendCountryRepository) {
        this.sendCountryRepository = sendCountryRepository;
    }


    @Override
    public List<SendCountry> findAll() {
        return (List<SendCountry>) sendCountryRepository.findAll();
    }

    @Override
    public SendCountry findByName(String name) {
        if (sendCountryRepository.findById(name).isPresent()) {
            return sendCountryRepository.findById(name).get();
        } else {
            return null;
        }
    }

    @Override
    public CountryInfoDTO getCountryInfo(String ip) {

        RestTemplate rest = new RestTemplate();

        Country country = rest.getForObject(COUNTRY_NAME_URL + ip, Country.class);
        log.trace("Country Name URL: {}", COUNTRY_NAME_URL + ip);

        String countryInfoUrl = COUNTRY_INFO_URL + country.getCountryCode() + COUNTRY_FIELDS_URL;
        CountryInfo info = rest.getForObject(countryInfoUrl, CountryInfo.class);
        log.trace("Country Info URL: {}", countryInfoUrl);
        log.trace("Country Info: {}", info);


        int distance = estimatedDistanceToARG(Double.parseDouble(info.getLatlng().get(0)),
                Double.parseDouble(info.getLatlng().get(1)));

        String completeDistance = distance + " KM (-34,-64) a " + "(" + info.getLatlng().get(0) + ","
                + info.getLatlng().get(1) + ")";

        if (sendCountryRepository.findById(country.getCountryName()).isPresent()) {
            SendCountry sendCountry = sendCountryRepository.findById(country.getCountryName()).get();
            Integer invocations = sendCountry.getInvocations() + 1;
            sendCountry.setInvocations(invocations);
            sendCountryRepository.save(sendCountry);
        } else {
            SendCountry sendCountry = SendCountry
                    .builder()
                    .name(country.getCountryName())
                    .distance(distance)
                    .invocations(1)
                    .build();
            sendCountryRepository.save(sendCountry);

        }

       return CountryInfoDTO.builder()
                .country(country.getCountryName())
                .isoCode(info.getLanguages().get(0).getIso639_1())
                .languages(getAllLanguages(info))
                .currencies(info.getCurrencies().get(0).getCode())
                .time(getTime(info))
                .estimatedDistance(completeDistance)
                .build();
    }

    public String findMinAndMaxDistanceCountries() throws Exception {
        List<SendCountry> listOfCountries = (List<SendCountry>) sendCountryRepository.findAll();
        SendCountry minDistanceCountry = listOfCountries
                .stream()
                .min(Comparator.comparing(SendCountry::getDistance))
                .orElseThrow(() -> new EntityNotFoundException("Empty Country list"));

        SendCountry maxDistanceCountry = listOfCountries
                .stream()
                .max(Comparator.comparing(SendCountry::getDistance))
                .orElseThrow(() -> new Exception("No objects founded"));

        int average = (minDistanceCountry.getDistance() * minDistanceCountry.getInvocations()
                + maxDistanceCountry.getDistance() * maxDistanceCountry.getInvocations())
                / (minDistanceCountry.getInvocations() + maxDistanceCountry.getInvocations());


        StringBuilder result = new StringBuilder()
                .append("Distancia más cercana a Buenos Aires es: ").append(minDistanceCountry.getName())
                .append(". Distancia: ").append(minDistanceCountry.getDistance()).append(" KM \n")
                .append("Distancia más lejana a Buenos Aires es: ").append(maxDistanceCountry.getName()).append(". Distancia: ")
                .append(maxDistanceCountry.getDistance()).append(" KM \n")
                .append("El promedio es:").append(average);

        return result.toString();
    }

    private int estimatedDistanceToARG(double lat2, double lng2) {

        double earthRadius = 6371;
        double dLat = Math.toRadians(lat2 - ARGENTINA_LATITUDE);
        double dLng = Math.toRadians(lng2 - ARGENTINA_LONGITUDE);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double temp1 = Math.pow(sindLat, 2)
                + Math.pow(sindLng, 2) * Math.cos(Math.toRadians(ARGENTINA_LATITUDE)) * Math.cos(Math.toRadians(lat2));
        double temp2 = 2 * Math.atan2(Math.sqrt(temp1), Math.sqrt(1 - temp1));
        double distance = earthRadius * temp2;

        return (int) distance;
    }

    private String getAllLanguages(CountryInfo countryInfo) {

        List<String> countryLanguagesList = countryInfo.getLanguages()
                .stream()
                .map(Language::getName)
                .collect(Collectors.toList());

        return String.join(", ", countryLanguagesList);

    }

    private String getTime(CountryInfo info) {

        StringBuilder hours = new StringBuilder();

        info.getTimezones().forEach(s -> {
            String localTime1 = LocalTime.now(ZoneId.of(s)).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            hours.append(localTime1).append(" (").append(s).append("), ");
        });

        return hours.toString();
    }

}
