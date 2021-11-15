package com.init.traceip.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDTO<T> {

    @JsonProperty(value = "code")
    protected int code;

    @JsonProperty(value = "message")
    protected String message;

    @JsonProperty(value = "error")
    protected List<String> error;

    @JsonProperty(value = "data")
    protected T data;
}
