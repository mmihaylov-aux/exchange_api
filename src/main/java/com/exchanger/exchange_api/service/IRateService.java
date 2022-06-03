package com.exchanger.exchange_api.service;

import com.exchanger.exchange_api.domain.ExchangeRate;
import com.exchanger.exchange_api.exception.HttpResponseException;

public interface IRateService {
    ExchangeRate getExchangeRate(String from, String to) throws HttpResponseException;

    String[] listCurrencies();
}
