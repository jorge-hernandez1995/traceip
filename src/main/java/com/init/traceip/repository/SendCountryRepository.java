package com.init.traceip.repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.init.traceip.entities.SendCountry;

@Repository
public class SendCountryRepository implements RedisRepository {

	private static final String KEY = "SendCountry";

	private RedisTemplate<String, SendCountry> redisTemplate;
	private HashOperations hashOperations;

	public SendCountryRepository(RedisTemplate<String, SendCountry> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@PostConstruct
	private void init() {
		hashOperations = redisTemplate.opsForHash();
	}

	@Override
	public Map<String, SendCountry> findAll() {
		return hashOperations.entries(KEY);
	}

	@Override
	public SendCountry findByName(String id) {
		return (SendCountry) hashOperations.get(KEY, id);
	}

	@Override
	public void save(SendCountry sendCountry) {
		hashOperations.put(KEY, sendCountry.getName().toString(), sendCountry);

	}

	public void update(SendCountry sendCountry) {
		sendCountry.setInvocations(sendCountry.getInvocations() + 1);
		hashOperations.put(KEY, sendCountry.getName().toString(), sendCountry);
	}

	public boolean ifExist(String name) {
		boolean ifexist;
		if (hashOperations.get(KEY, name) == null) {
			ifexist = false;
		} else {
			ifexist = true;
		}
		return ifexist;
	}

	public String findMinAndMaxDistanceCountries() {
		BidiMap<String, Integer> countriesMap = new DualHashBidiMap<>();
		List<SendCountry> listOfCountries = hashOperations.values(KEY);
		for (SendCountry sendCountry : listOfCountries) {
			countriesMap.put(sendCountry.getName(), sendCountry.getDistance());
		}
		String minDistanceCountry = countriesMap.getKey(Collections.min(countriesMap.values()));
		int minDistance = Collections.min(countriesMap.values());
		int minDistanceInvocatinos = getInvocatios(minDistanceCountry);

		String maxDistanceCountry = countriesMap.getKey(Collections.max(countriesMap.values()));
		int maxDistance = Collections.max(countriesMap.values());
		int maxDistanceInvocations = getInvocatios(maxDistanceCountry);

		int avarage = (minDistance * minDistanceInvocatinos + maxDistance * maxDistanceInvocations)
				/ (minDistanceInvocatinos + maxDistanceInvocations);

		return "Distancia más cercana a Buenos Aires es: " + minDistanceCountry + ". Distancia: " + minDistance
				+ " KM \n" + "Distancia más lejana a Buenos Aires es: " + maxDistanceCountry + ". Distancia: "
				+ maxDistance + " KM \n" + "El promedio es:" + avarage;
	}

	public int getInvocatios(String name) {
		SendCountry sendCountry = (SendCountry) hashOperations.get(KEY, name);
		return sendCountry.getInvocations();
	}

}