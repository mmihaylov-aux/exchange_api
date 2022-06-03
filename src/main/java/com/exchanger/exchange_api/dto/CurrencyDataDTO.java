package com.exchanger.exchange_api.dto;

import com.exchanger.exchange_api.enumeration.CurrencyProvider;

public interface CurrencyDataDTO {
    String getCurrency();
    String getCurrencyId();
    CurrencyProvider getProvider();
}
