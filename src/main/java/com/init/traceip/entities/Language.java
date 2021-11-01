package com.init.traceip.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Language {

	private String iso639_1;
	private String iso639_2;
	private String name;
	private String nativeName;

}
