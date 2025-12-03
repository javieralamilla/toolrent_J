package com.example.ToolRent.controllers;

import com.example.ToolRent.entities.GlobalRatesEntity;
import com.example.ToolRent.services.GlobalRatesService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = GlobalRatesController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
public class GlobalRatesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GlobalRatesService globalRatesService;

    // ==================== getAllGlobalRates ====================

    @Test
    public void getAllGlobalRates_ShouldReturnRates() throws Exception {
        GlobalRatesEntity rate1 = new GlobalRatesEntity(
                1L,
                "Tarifa Diaria de Multa",
                1000);

        GlobalRatesEntity rate2 = new GlobalRatesEntity(
                2L,
                "Tarifa de Mantenimiento",
                500);

        ArrayList<GlobalRatesEntity> rateList = new ArrayList<>(Arrays.asList(rate1, rate2));

        given(globalRatesService.getGlobalRates()).willReturn(rateList);

        mockMvc.perform(get("/api/v1/globalRates/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].rateName", is("Tarifa Diaria de Multa")))
                .andExpect(jsonPath("$[0].dailyRateValue", is(1000)))
                .andExpect(jsonPath("$[1].rateName", is("Tarifa de Mantenimiento")))
                .andExpect(jsonPath("$[1].dailyRateValue", is(500)));
    }

    @Test
    public void getAllGlobalRates_WithEmptyList_ShouldReturnEmptyArray() throws Exception {
        ArrayList<GlobalRatesEntity> emptyRateList = new ArrayList<>();

        given(globalRatesService.getGlobalRates()).willReturn(emptyRateList);

        mockMvc.perform(get("/api/v1/globalRates/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }



    // ==================== findGlobalRatesById ====================

    @Test
    public void findGlobalRatesById_ShouldReturnRate() throws Exception {
        GlobalRatesEntity rate = new GlobalRatesEntity(
                1L,
                "Tarifa Diaria de Multa",
                1000);

        given(globalRatesService.findGlobalRatesById(1L)).willReturn(rate);

        mockMvc.perform(get("/api/v1/globalRates/id/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.rateName", is("Tarifa Diaria de Multa")))
                .andExpect(jsonPath("$.dailyRateValue", is(1000)));
    }

    @Test
    public void findGlobalRatesById_ShouldReturnBadRequest_WhenException() throws Exception {
        given(globalRatesService.findGlobalRatesById(999L))
                .willThrow(new RuntimeException("Tarifa no encontrada"));

        mockMvc.perform(get("/api/v1/globalRates/id/{id}", 999L))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findGlobalRatesById_WithDifferentRate_ShouldReturnCorrectRate() throws Exception {
        GlobalRatesEntity rate = new GlobalRatesEntity(
                2L,
                "Tarifa de Mantenimiento",
                500);

        given(globalRatesService.findGlobalRatesById(2L)).willReturn(rate);

        mockMvc.perform(get("/api/v1/globalRates/id/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.rateName", is("Tarifa de Mantenimiento")))
                .andExpect(jsonPath("$.dailyRateValue", is(500)));
    }

    // ==================== saveGlobalRate ====================

    @Test
    public void saveGlobalRate_ShouldReturnSavedRate() throws Exception {
        GlobalRatesEntity savedRate = new GlobalRatesEntity(
                1L,
                "Tarifa Diaria de Multa",
                1000);

        given(globalRatesService.saveRate(Mockito.any(GlobalRatesEntity.class))).willReturn(savedRate);

        String rateJson = """
            {
                "rateName": "Tarifa Diaria de Multa",
                "dailyRateValue": 1000
            }
            """;

        mockMvc.perform(post("/api/v1/globalRates/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rateJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.rateName", is("Tarifa Diaria de Multa")))
                .andExpect(jsonPath("$.dailyRateValue", is(1000)));
    }

    @Test
    public void saveGlobalRate_WithDifferentRate_ShouldReturnSavedRate() throws Exception {
        GlobalRatesEntity savedRate = new GlobalRatesEntity(
                2L,
                "Tarifa de Mantenimiento",
                500);

        given(globalRatesService.saveRate(Mockito.any(GlobalRatesEntity.class))).willReturn(savedRate);

        String rateJson = """
            {
                "rateName": "Tarifa de Mantenimiento",
                "dailyRateValue": 500
            }
            """;

        mockMvc.perform(post("/api/v1/globalRates/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rateJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.rateName", is("Tarifa de Mantenimiento")))
                .andExpect(jsonPath("$.dailyRateValue", is(500)));
    }

    @Test
    public void saveGlobalRate_ShouldReturnBadRequest_WhenException() throws Exception {
        given(globalRatesService.saveRate(Mockito.any(GlobalRatesEntity.class)))
                .willThrow(new RuntimeException("Tarifa duplicada"));

        String rateJson = """
            {
                "rateName": "Tarifa Duplicada",
                "dailyRateValue": 1000
            }
            """;

        mockMvc.perform(post("/api/v1/globalRates/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rateJson))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void saveGlobalRate_WithZeroValue_ShouldReturnSavedRate() throws Exception {
        GlobalRatesEntity savedRate = new GlobalRatesEntity(
                1L,
                "Tarifa Especial",
                0);

        given(globalRatesService.saveRate(Mockito.any(GlobalRatesEntity.class))).willReturn(savedRate);

        String rateJson = """
            {
                "rateName": "Tarifa Especial",
                "dailyRateValue": 0
            }
            """;

        mockMvc.perform(post("/api/v1/globalRates/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rateJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.rateName", is("Tarifa Especial")))
                .andExpect(jsonPath("$.dailyRateValue", is(0)));
    }
    // ==================== updateValueRate ====================

    @Test
    public void updateValueRate_ShouldReturnUpdatedRate() throws Exception {
        GlobalRatesEntity updatedRate = new GlobalRatesEntity(
                1L,
                "Tarifa Diaria de Multa",
                1200);

        given(globalRatesService.updateValueRate(1L, 1200)).willReturn(updatedRate);

        mockMvc.perform(put("/api/v1/globalRates/{id}/{dailyRateValue}", 1L, 1200))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.rateName", is("Tarifa Diaria de Multa")))
                .andExpect(jsonPath("$.dailyRateValue", is(1200)));
    }

    @Test
    public void updateValueRate_WithLargeValue_ShouldReturnUpdatedRate() throws Exception {
        GlobalRatesEntity updatedRate = new GlobalRatesEntity(
                1L,
                "Tarifa Diaria de Multa",
                5000);

        given(globalRatesService.updateValueRate(1L, 5000)).willReturn(updatedRate);

        mockMvc.perform(put("/api/v1/globalRates/{id}/{dailyRateValue}", 1L, 5000))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.dailyRateValue", is(5000)));
    }

    @Test
    public void updateValueRate_WithMinimumValue_ShouldReturnUpdatedRate() throws Exception {
        GlobalRatesEntity updatedRate = new GlobalRatesEntity(
                1L,
                "Tarifa Diaria de Multa",
                100);

        given(globalRatesService.updateValueRate(1L, 100)).willReturn(updatedRate);

        mockMvc.perform(put("/api/v1/globalRates/{id}/{dailyRateValue}", 1L, 100))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.dailyRateValue", is(100)));
    }

    @Test
    public void updateValueRate_ShouldReturnBadRequest_WhenException() throws Exception {
        given(globalRatesService.updateValueRate(999L, 1200))
                .willThrow(new RuntimeException("Tarifa no encontrada"));

        mockMvc.perform(put("/api/v1/globalRates/{id}/{dailyRateValue}", 999L, 1200))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateValueRate_WithInvalidValue_ShouldReturnBadRequest_WhenException() throws Exception {
        given(globalRatesService.updateValueRate(1L, -100))
                .willThrow(new RuntimeException("Valor inválido"));

        mockMvc.perform(put("/api/v1/globalRates/{id}/{dailyRateValue}", 1L, -100))
                .andExpect(status().isBadRequest());
    }


}