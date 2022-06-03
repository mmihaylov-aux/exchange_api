package com.exchanger.exchange_api.service.internal;

import com.exchanger.exchange_api.client.internal.ApiLayerClient;
import com.exchanger.exchange_api.client.internal.CoinGeckoClient;
import com.exchanger.exchange_api.domain.ExchangeRate;
import com.exchanger.exchange_api.dto.CurrencyDataDTO;
import com.exchanger.exchange_api.dto.response.ApiLayerLiveResponseDTO;
import com.exchanger.exchange_api.dto.response.CoinGeckoLiveResponseDTO;
import com.exchanger.exchange_api.enumeration.CurrencyProvider;
import com.exchanger.exchange_api.enumeration.ErrorCode;
import com.exchanger.exchange_api.exception.HttpResponseException;
import com.exchanger.exchange_api.model.CurrencyModel;
import com.exchanger.exchange_api.repository.CurrencyRepository;
import com.exchanger.exchange_api.service.IRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.StreamSupport;

@Service
public class RateService implements IRateService {
    private final ApiLayerClient apiLayerClient;
    private final CoinGeckoClient coinGeckoClient;
    private final CurrencyRepository currencyRepository;

    private static final String MIDDLE_EXCHANGE_CURRENCY = "USD";
    private static final String MIDDLE_EXCHANGE_CURRENCY_LOWERCASE = MIDDLE_EXCHANGE_CURRENCY.toLowerCase();

    @Autowired
    public RateService(ApiLayerClient apiLayerClient,
                       CoinGeckoClient coinGeckoClient,
                       CurrencyRepository currencyRepository) {
        this.apiLayerClient = apiLayerClient;
        this.coinGeckoClient = coinGeckoClient;
        this.currencyRepository = currencyRepository;
    }

    @Override
    public ExchangeRate getExchangeRate(String from, String to) throws HttpResponseException {
        CurrencyDataDTO fromCurrency = this.currencyRepository.getFirstByCurrency(from);
        CurrencyDataDTO toCurrency = this.currencyRepository.getFirstByCurrency(to);

        if (fromCurrency == null || toCurrency == null)
            throw new HttpResponseException(ErrorCode.INVALID_CURRENCY_TYPE);

        BigDecimal exchangeRate;
        if (fromCurrency.getProvider() == toCurrency.getProvider() &&
                fromCurrency.getProvider() == CurrencyProvider.ApiLayer) {
            ApiLayerLiveResponseDTO rate = apiLayerClient.getRate(from, to);
            exchangeRate = rate.getQuotes().get(from + "" + to);
        } else {
            BigDecimal fromRate = getUsdRate(fromCurrency);
            BigDecimal toRate = getUsdRate(toCurrency);
            exchangeRate = toRate.divide(fromRate, RoundingMode.HALF_UP);
        }

        return new ExchangeRate(exchangeRate);
    }

    private BigDecimal getUsdRate(CurrencyDataDTO currencyData) throws HttpResponseException {
        switch (currencyData.getProvider()) {
            case ApiLayer -> {
                if (currencyData.getCurrency().equals(MIDDLE_EXCHANGE_CURRENCY)) return BigDecimal.ONE;
                return apiLayerClient.getRate(currencyData.getCurrency(), MIDDLE_EXCHANGE_CURRENCY)
                        .getQuotes().get(currencyData.getCurrency() + "" + MIDDLE_EXCHANGE_CURRENCY);
            }
            case CoinGecko -> {
                CoinGeckoLiveResponseDTO rate = this.coinGeckoClient.getRate(currencyData.getCurrencyId());
                return rate.getMarketDataDTO().getCurrentPrice().get(MIDDLE_EXCHANGE_CURRENCY_LOWERCASE);
            }
        }

        throw new HttpResponseException(ErrorCode.INVALID_CURRENCY_TYPE);
    }

    @Override
    public String[] listCurrencies() {
        Iterable<CurrencyModel> all = currencyRepository.findAll();
        return StreamSupport.stream(all.spliterator(), false)
                .map(CurrencyModel::getCurrency).distinct()
                .toArray(String[]::new);
    }
}
