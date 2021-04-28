package com.init.traceip.repository;

import java.util.Map;

import com.init.traceip.entitys.SendCountry;

public interface RedisRepository {
	
	Map<String, SendCountry> findAll();

	SendCountry findByName(String id);

	void save(SendCountry sendCountry);
}
