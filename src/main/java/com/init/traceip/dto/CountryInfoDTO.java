package com.init.traceip.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryInfoDTO {

    @JsonProperty(value = "country")
    private String country;

    @JsonProperty(value = "iso_code")
    private String isoCode;

    @JsonProperty(value = "languages")
    private String languages;

    @JsonProperty(value = "currencies")
    private String currencies;

    @JsonProperty(value = "time")
    private String time;

    @JsonProperty(value = "estimated_distance_to_argentina")
    private String estimatedDistance;
}
