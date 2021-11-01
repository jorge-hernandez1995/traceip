package com.init.traceip.services.impl;

import com.init.traceip.entities.Country;
import com.init.traceip.entities.CountryInfo;
import com.init.traceip.entities.SendCountry;
import com.init.traceip.repository.SendCountryRepository;
import com.init.traceip.services.SendCountryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.init.traceip.constants.TraceIPConstants.ARGENTINA_LATITUDE;
import static com.init.traceip.constants.TraceIPConstants.ARGENTINA_LONGITUDE;

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
    public String getCountryInfo(String ip) {
        double inUSD = 0;

        RestTemplate rest = new RestTemplate();

        Country country = rest.getForObject("https://api.ip2country.info/ip?" + ip, Country.class);

        CountryInfo info = rest.getForObject("https://restcountries.com/v2/alpha/" + country.getCountryCode()
                + "?fields=languages,currencies,timezones,latlng", CountryInfo.class);
        log.trace("Country Info: {}", info);


        int distance = estimatedDistanceToARG(ARGENTINA_LATITUDE, ARGENTINA_LONGITUDE, Double.parseDouble(info.getLatlng().get(0)),
                Double.parseDouble(info.getLatlng().get(1)));

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

        return "País: " + country.getCountryName() + "\n ISO Code: " + info.getLanguages().get(0).getIso639_1()
                + "\n Idiomas: " + getAllLanguages(info) + "\n Monedas: " + info.getCurrencies().get(0).getCode()
                + " = " + inUSD + " U$S" + "\n Hora(s):\n " + getTime(info) + "\n Distancia Estimada: " + distance
                + " KM (-34,-64) a " + "(" + info.getLatlng().get(0) + "," + info.getLatlng().get(1) + ")";
    }

    public String findMinAndMaxDistanceCountries() {
        BidiMap<String, Integer> countriesMap = new DualHashBidiMap<>();
        List<SendCountry> listOfCountries = (List<SendCountry>) sendCountryRepository.findAll();
        for (SendCountry sendCountry : listOfCountries) {
            countriesMap.put(sendCountry.getName(), sendCountry.getDistance());
        }
        String minDistanceCountry = countriesMap.getKey(Collections.min(countriesMap.values()));
        int minDistance = Collections.min(countriesMap.values());
        int minDistanceInvocatinos = getInvocations(minDistanceCountry);

        String maxDistanceCountry = countriesMap.getKey(Collections.max(countriesMap.values()));
        int maxDistance = Collections.max(countriesMap.values());
        int maxDistanceInvocations = getInvocations(maxDistanceCountry);

        int avarage = (minDistance * minDistanceInvocatinos + maxDistance * maxDistanceInvocations)
                / (minDistanceInvocatinos + maxDistanceInvocations);

        StringBuilder s = new StringBuilder().append("as");

        return "Distancia más cercana a Buenos Aires es: " + minDistanceCountry + ". Distancia: " + minDistance
                + " KM \n" + "Distancia más lejana a Buenos Aires es: " + maxDistanceCountry + ". Distancia: "
                + maxDistance + " KM \n" + "El promedio es:" + avarage;
    }

    public int getInvocations(String name) {
        SendCountry sendCountry = findByName(name);
        return sendCountry.getInvocations();
    }

    public int estimatedDistanceToARG(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double temp1 = Math.pow(sindLat, 2)
                + Math.pow(sindLng, 2) * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double temp2 = 2 * Math.atan2(Math.sqrt(temp1), Math.sqrt(1 - temp1));
        double distance = earthRadius * temp2;

        return (int) distance;
    }

    public String getAllLanguages(CountryInfo info) {

        String languages = "";
        for (int i = 0; i < info.getLanguages().size(); i++) {
            languages += info.getLanguages().get(i).getName() + ", ";
        }

        return languages;

    }

    public String getTime(CountryInfo info) {

        String hours = "";

        for (int i = 0; i < info.getTimezones().size(); i++) {
            OffsetDateTime dateTime = OffsetDateTime.now(ZoneId.of(info.getTimezones().get(i)));
            LocalTime localTime = dateTime.toLocalTime();
            hours += localTime.toString().substring(0, 8) + " (" + ZoneId.of(info.getTimezones().get(i)) + ") / ";
        }

        return hours;
    }

}
