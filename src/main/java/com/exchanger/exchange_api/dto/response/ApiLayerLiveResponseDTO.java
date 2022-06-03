package com.exchanger.exchange_api.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;

public class ApiLayerLiveResponseDTO {
    private final Map<String, BigDecimal> quotes;

    @JsonCreator
    public ApiLayerLiveResponseDTO(@JsonProperty(value = "quotes", required = true) Map<String, BigDecimal> quotes) {
        this.quotes = quotes;
    }

    public Map<String, BigDecimal> getQuotes() {
        return quotes;
    }
}
