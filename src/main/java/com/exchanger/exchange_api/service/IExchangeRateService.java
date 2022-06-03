package com.exchanger.exchange_api.service;

import com.exchanger.exchange_api.domain.ExchangeRate;
import com.exchanger.exchange_api.exception.HttpResponseException;

public interface IExchangeRateService {
    ExchangeRate getExchangeRate(String source, String target) throws HttpResponseException;

    String[] listCurrencies();
}
