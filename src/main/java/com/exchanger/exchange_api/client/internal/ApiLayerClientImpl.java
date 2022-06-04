package com.exchanger.exchange_api.client.internal;

import com.exchanger.exchange_api.client.HttpRequestClient;
import com.exchanger.exchange_api.client.ApiLayerClient;
import com.exchanger.exchange_api.config.properties.ApiLayerProperties;
import com.exchanger.exchange_api.dto.error.ApiLayerErrorResponseDTO;
import com.exchanger.exchange_api.dto.response.ApiLayerListResponseDTO;
import com.exchanger.exchange_api.dto.response.ApiLayerLiveResponseDTO;
import com.exchanger.exchange_api.exception.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

@Component
public class ApiLayerClientImpl extends HttpRequestClient implements ApiLayerClient {
    private final static String API_KEY_HEADER = "apikey";

    @Autowired
    public ApiLayerClientImpl(ApiLayerProperties properties, WebClient.Builder clientBuilder) {
        super(clientBuilder.baseUrl(properties.getUrl())
                .defaultHeader(API_KEY_HEADER, properties.getKey()),
                ApiLayerErrorResponseDTO.class);
    }

    @Override
    public ApiLayerListResponseDTO listCurrencies() throws HttpResponseException {
        return this.get("list", ApiLayerListResponseDTO.class);
    }

    @Override
    public ApiLayerLiveResponseDTO getRate(String source, String currency) throws HttpResponseException {
        return this.get("live?source=%s&currencies=%s".formatted(source, currency),
                ApiLayerLiveResponseDTO.class);
    }

    @Override
    public BigDecimal convert(String source, String target, BigDecimal value) {
        return null;
    }
}
