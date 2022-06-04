package com.exchanger.exchange_api.service;

import com.exchanger.exchange_api.dto.response.ExchangeRateResponseDTO;
import com.exchanger.exchange_api.exception.HttpResponseException;

public interface ExchangeRateService {
    ExchangeRateResponseDTO getExchangeRate(String source, String target) throws HttpResponseException;

    String[] listCurrencies();
}
