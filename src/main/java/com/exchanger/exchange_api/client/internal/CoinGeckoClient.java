package com.exchanger.exchange_api.client.internal;

import com.exchanger.exchange_api.client.HttpRequestClient;
import com.exchanger.exchange_api.config.properties.CoinGeckoProperties;
import com.exchanger.exchange_api.dto.error.CoinGeckoErrorResponseDTO;
import com.exchanger.exchange_api.dto.response.CoinGeckoListResponseDTO;
import com.exchanger.exchange_api.dto.response.CoinGeckoLiveResponseDTO;
import com.exchanger.exchange_api.exception.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CoinGeckoClient extends HttpRequestClient {
//    private final static String API_KEY_HEADER = "apikey";

    @Autowired
    public CoinGeckoClient(CoinGeckoProperties properties, WebClient.Builder clientBuilder) {
        super(clientBuilder.baseUrl(properties.getUrl()),
//                .defaultHeader(API_KEY_HEADER, properties.getKey()),
                CoinGeckoErrorResponseDTO.class);
    }

    public CoinGeckoListResponseDTO[] listCurrencies() throws HttpResponseException {
        return this.get("coins/list", CoinGeckoListResponseDTO[].class);
    }

    public CoinGeckoLiveResponseDTO getRate(String sourceId) throws HttpResponseException {
        return this.get("coins/%s".formatted(sourceId),
                CoinGeckoLiveResponseDTO.class);
    }
}
