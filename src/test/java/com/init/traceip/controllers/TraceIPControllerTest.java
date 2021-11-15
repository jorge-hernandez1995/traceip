package com.init.traceip.controllers;

import com.init.traceip.controller.TraceIPController;
import com.init.traceip.dto.CountryInfoDTO;
import com.init.traceip.entities.SendCountry;
import com.init.traceip.services.SendCountryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = TraceIPController.class)
class TraceIPControllerTest {

    @MockBean
    private SendCountryService sendCountryService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void test_GetCountryInfo_Should_Return_InfoSuccessfully() throws Exception {
        when(sendCountryService.getCountryInfo(anyString()))
                .thenReturn(getCountryInfoDTO());

        this.mockMvc.perform(get("/traceip/111.111.111.111"))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void test_FindAll_Should_Return_AllSentCountries() throws Exception {
        when(sendCountryService.findAll())
                .thenReturn(List.of(getSentCountry()));

        this.mockMvc.perform(get("/countries"))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void test_GetStatistics_Should_Return_InfoSuccessfully() throws Exception {
        when(sendCountryService.findMinAndMaxDistanceCountries())
                .thenReturn("Distance Statistics");

        this.mockMvc.perform(get("/statistics"))
                .andDo(print())
                .andExpect(status().isOk());

    }

    private CountryInfoDTO getCountryInfoDTO(){
        return CountryInfoDTO.builder()
                .country("country")
                .isoCode("isoCode")
                .languages("languages")
                .currencies("currency")
                .time("time")
                .estimatedDistance("estimatedDistance")
                .build();
    }

    private SendCountry getSentCountry(){
        return SendCountry.builder()
                .name("name")
                .distance(2000)
                .invocations(2)
                .build();
    }


}
