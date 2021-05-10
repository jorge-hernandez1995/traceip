package com.init.traceip.controller;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.init.traceip.entities.Country;
import com.init.traceip.entities.CountryInfo;
import com.init.traceip.entities.CurrencyConversion;
import com.init.traceip.entities.SendCountry;
import com.init.traceip.repository.SendCountryRepository;

@RestController
public class TraceIPController {
	private SendCountryRepository sendCountryRepository;
	private static final double LATARG = -34;
	private static final double LNGARG = -64;

	public TraceIPController(SendCountryRepository sendCountryRepository) {
		this.sendCountryRepository = sendCountryRepository;
	}

	@GetMapping("/traceip/{ip}")
	public String getInfo(@PathVariable String ip) {

		double inUSD = 0;

		RestTemplate rest = new RestTemplate();

		Country country = rest.getForObject("https://api.ip2country.info/ip?" + ip, Country.class);

		CountryInfo info = rest.getForObject("https://restcountries.eu/rest/v2/alpha/" + country.getCountryCode()
				+ "?fields=languages;currencies;timezones;latlng", CountryInfo.class);

		CurrencyConversion conver = rest
				.getForObject("http://data.fixer.io/api/latest?access_key=a66ec071a16b4c5e6df94a82806953f5&base="
						+ info.getCurrencies().get(0).getCode() + "&symbols=USD", CurrencyConversion.class);

		int distance = estimatedDistanceToARG(LATARG, LNGARG, Double.parseDouble(info.getLatlng().get(0)),
				Double.parseDouble(info.getLatlng().get(1)));

		if (sendCountryRepository.ifExist(country.getCountryName())) {
			SendCountry sendCountry = sendCountryRepository.findByName(country.getCountryName());
			sendCountryRepository.update(sendCountry);

		} else {
			SendCountry sendCountry = new SendCountry(country.getCountryName(), distance, 1);
			sendCountryRepository.save(sendCountry);

		}

		if (conver.isSuccess()) {
			inUSD = conver.getRates().getUSD(); 
		}

		return "País: " + country.getCountryName() + "\n ISO Code: " + info.getLanguages().get(0).getIso639_1()
				+ "\n Idiomas: " + getAllLanguages(info) + "\n Monedas: " + info.getCurrencies().get(0).getCode()
				+ " = " + inUSD + " U$S" + "\n Hora(s):\n " + getTime(info) + "\n Distancia Estimada: " + distance
				+ " KM (-34,-64) a " + "(" + info.getLatlng().get(0) + "," + info.getLatlng().get(1) + ")";

	}

	@GetMapping("/countries")
	public Map<String, SendCountry> findAll() {
		return sendCountryRepository.findAll();
	}

	@GetMapping("/country/{id}")
	public SendCountry findByName(@PathVariable String id) {
		return sendCountryRepository.findByName(id);
	}

	@GetMapping("/statistics")
	public String statistics() {
		return sendCountryRepository.findMinAndMaxDistanceCountries();
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

}
