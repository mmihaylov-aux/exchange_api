package com.exchanger.exchange_api.client;

import com.exchanger.exchange_api.dto.CurrencyDataDTO;
import com.exchanger.exchange_api.dto.response.CoinGeckoListResponseDTO;
import com.exchanger.exchange_api.dto.response.CoinGeckoLiveResponseDTO;
import com.exchanger.exchange_api.exception.HttpResponseException;

public interface CoinGeckoClient {
    CoinGeckoListResponseDTO[] listCurrencies() throws HttpResponseException;

    CoinGeckoLiveResponseDTO getRate(CurrencyDataDTO currency) throws HttpResponseException;
}
