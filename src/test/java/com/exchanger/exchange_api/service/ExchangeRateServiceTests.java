package com.exchanger.exchange_api.service;

import com.exchanger.exchange_api.client.ApiLayerClient;
import com.exchanger.exchange_api.client.CoinGeckoClient;
import com.exchanger.exchange_api.dto.response.ApiLayerLiveResponseDTO;
import com.exchanger.exchange_api.dto.response.CoinGeckoLiveResponseDTO;
import com.exchanger.exchange_api.dto.response.ExchangeRateResponseDTO;
import com.exchanger.exchange_api.enumeration.CurrencyProvider;
import com.exchanger.exchange_api.enumeration.ErrorCode;
import com.exchanger.exchange_api.exception.HttpResponseException;
import com.exchanger.exchange_api.model.CurrencyModel;
import com.exchanger.exchange_api.repository.CurrencyRepository;
import com.exchanger.exchange_api.service.internal.ExchangeRateServiceImpl;
import com.exchanger.exchange_api.util.CurrencyCombiner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

@RunWith(SpringRunner.class)
public class ExchangeRateServiceTests {
    @Mock
    ApiLayerClient apiLayerClient;

    @Mock
    CoinGeckoClient coinGeckoClient;

    @Mock
    CurrencyRepository currencyRepository;

    @InjectMocks
    ExchangeRateServiceImpl exchangeRateService;

    final String eurCurrency = "EUR";
    final String usdCurrency = "USD";
    final String bgnCurrency = "USD";

    final String btcCrypto = "BTC";
    final String btcCryptoId = "bitcoin";
    final String ethCrypto = "ETH";
    final String ethCryptoId = "ethereum";

    final BigDecimal exchangeRate = BigDecimal.valueOf(0.6);
    final BigDecimal cryptoExchangeRate = BigDecimal.valueOf(1.5);

    CurrencyModel[] currencyModels = {
            new CurrencyModel(eurCurrency, null, CurrencyProvider.ApiLayer),
            new CurrencyModel(usdCurrency, null, CurrencyProvider.ApiLayer),
            new CurrencyModel(bgnCurrency, null, CurrencyProvider.ApiLayer),
            new CurrencyModel(btcCrypto, btcCryptoId, CurrencyProvider.CoinGecko),
            new CurrencyModel(ethCrypto, ethCryptoId, CurrencyProvider.CoinGecko),
    };

    @Before
    public void prepareMocks() throws HttpResponseException {
        Mockito.when(currencyRepository.getFirstByCurrency(Mockito.anyString()))
                .thenReturn(null);

        for (CurrencyModel currencyModel : currencyModels) {
            Mockito.when(currencyRepository.getFirstByCurrency(currencyModel.getCurrency()))
                    .thenReturn(currencyModel);
        }

        Mockito.when(apiLayerClient.getRate(Mockito.anyString(), Mockito.anyString()))
                .thenAnswer(invocationOnMock -> new ApiLayerLiveResponseDTO(new HashMap<>() {{
                    put(CurrencyCombiner.combine(invocationOnMock.getArgument(0), invocationOnMock.getArgument(1)),
                            exchangeRate);
                }}));

        Mockito.when(coinGeckoClient.getRate(Mockito.any()))
                .thenAnswer(invocationOnMock -> new CoinGeckoLiveResponseDTO(
                        new CoinGeckoLiveResponseDTO.MarketDataDTO(
                                new HashMap<>() {{
                                    put(usdCurrency.toLowerCase(), cryptoExchangeRate);
                                }}
                        )));
    }

    @Test
    public void when_get_exchange_rate_with_same_currency_should_throw_exception() {
        HttpResponseException ex = null;
        try {
            exchangeRateService.getExchangeRate(eurCurrency, eurCurrency);
        } catch (HttpResponseException e) {
            ex = e;
        }
        Assert.assertNotNull("An exception should be thrown", ex);
        Assert.assertEquals(ErrorCode.SAME_CURRENCY, ex.getCode());
    }

    @Test
    public void when_get_exchange_rate_with_invalid_currency_should_throw_exception() {
        HttpResponseException ex = null;
        try {
            exchangeRateService.getExchangeRate("FAKE_CURRENCY", eurCurrency);
        } catch (HttpResponseException e) {
            ex = e;
        }
        Assert.assertNotNull("An exception should be thrown", ex);
        Assert.assertEquals(ErrorCode.INVALID_CURRENCY_TYPE, ex.getCode());

        ex = null;
        try {
            exchangeRateService.getExchangeRate(eurCurrency, "FAKE_CURRENCY");
        } catch (HttpResponseException e) {
            ex = e;
        }
        Assert.assertNotNull("An exception should be thrown", ex);
        Assert.assertEquals(ErrorCode.INVALID_CURRENCY_TYPE, ex.getCode());
    }

    @Test
    public void when_get_exchange_rate_with_apiLayer_provider_only_should_be_correct() {
        try {
            ExchangeRateResponseDTO rate = exchangeRateService.getExchangeRate(usdCurrency, eurCurrency);
            Assert.assertEquals(exchangeRate, rate.getValue());
        } catch (HttpResponseException e) {
            Assert.assertNull("An exception should not be thrown", e);
        }
    }

    @Test
    public void when_get_exchange_rate_with_crypto_should_be_correct() {
        final RoundingMode roundingMode = RoundingMode.HALF_UP;
        try {
            ExchangeRateResponseDTO rate = exchangeRateService.getExchangeRate(btcCrypto, eurCurrency);
            Assert.assertEquals(exchangeRate.divide(cryptoExchangeRate, roundingMode), rate.getValue());

            rate = exchangeRateService.getExchangeRate(eurCurrency, btcCrypto);
            Assert.assertEquals(cryptoExchangeRate.divide(exchangeRate, roundingMode), rate.getValue());

            rate = exchangeRateService.getExchangeRate(ethCrypto, btcCrypto);
            Assert.assertEquals(cryptoExchangeRate.divide(cryptoExchangeRate, roundingMode), rate.getValue());
        } catch (HttpResponseException e) {
            Assert.assertNull("An exception should not be thrown", e);
        }
    }

}
