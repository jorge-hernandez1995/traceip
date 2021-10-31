package com.init.traceip.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyConversion implements Serializable {

	private static final long serialVersionUID = -6374993914929470702L;

	@JsonProperty(value = "country_id")
	private boolean success;

	@JsonProperty(value = "country_id")
	private Long timestamp;

	@JsonProperty(value = "country_id")
	private String base;

	@JsonProperty(value = "country_id")
	private String date;

	@JsonProperty(value = "country_id")
	private Rate rates;


}
