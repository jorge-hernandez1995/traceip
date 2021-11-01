package com.init.traceip.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryInfo {

	private ArrayList<Currency> currencies;
	private ArrayList<Language> languages;
	private ArrayList<String> timezones;
	private ArrayList<String> latlng;

}
