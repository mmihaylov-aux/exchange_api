package com.exchanger.exchange_api.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class ApiLayerListResponseDTO {
    private final Map<String, String> currencies;

    @JsonCreator
    public ApiLayerListResponseDTO(@JsonProperty(value = "currencies", required = true) Map<String, String> currencies) {
        this.currencies = currencies;
    }

    public Map<String, String> getCurrencies() {
        return currencies;
    }
}