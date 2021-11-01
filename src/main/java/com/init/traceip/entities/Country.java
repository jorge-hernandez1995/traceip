package com.init.traceip.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Country {

	private String countryCode;
	private String countryCode3;
	private String countryName;
	private String countryEmoji;

}
