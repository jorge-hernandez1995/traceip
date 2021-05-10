package com.init.traceip.entities;

import java.util.ArrayList;

public class CountryInfo {

	private ArrayList<Currency> currencies;
	private ArrayList<Language> languages;
	private ArrayList<String> timezones;
	private ArrayList<String> latlng;

	public ArrayList<Currency> getCurrencies() {
		return currencies;
	}

	public void setCurrencies(ArrayList<Currency> currencies) {
		this.currencies = currencies;
	}

	public ArrayList<Language> getLanguages() {
		return languages;
	}

	public void setLanguages(ArrayList<Language> languages) {
		this.languages = languages;
	}

	public ArrayList<String> getTimezones() {
		return timezones;
	}

	public void setTimezones(ArrayList<String> timezones) {
		this.timezones = timezones;
	}

	public ArrayList<String> getLatlng() {
		return latlng;
	}

	public void setLatlng(ArrayList<String> latlng) {
		this.latlng = latlng;
	}

}
