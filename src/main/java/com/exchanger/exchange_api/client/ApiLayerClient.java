package com.exchanger.exchange_api.client;

import com.exchanger.exchange_api.dto.response.ApiLayerListResponseDTO;
import com.exchanger.exchange_api.dto.response.ApiLayerLiveResponseDTO;
import com.exchanger.exchange_api.exception.HttpResponseException;

import java.math.BigDecimal;

public interface ApiLayerClient {
    ApiLayerListResponseDTO listCurrencies() throws HttpResponseException;

    ApiLayerLiveResponseDTO getRate(String source, String currency) throws HttpResponseException;

    BigDecimal convert(String source, String target, BigDecimal value);
}
