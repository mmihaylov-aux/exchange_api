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
import com.exchanger.exchange_api.service.IExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.StreamSupport;

@Service
public class ExchangeRateService implements IExchangeRateService {
    private final ApiLayerClient apiLayerClient;
    private final CoinGeckoClient coinGeckoClient;
    private final CurrencyRepository currencyRepository;

    private static final String MIDDLE_EXCHANGE_CURRENCY = "USD";
    private static final String MIDDLE_EXCHANGE_CURRENCY_LOWERCASE = MIDDLE_EXCHANGE_CURRENCY.toLowerCase();

    @Autowired
    public ExchangeRateService(ApiLayerClient apiLayerClient,
                               CoinGeckoClient coinGeckoClient,
                               CurrencyRepository currencyRepository) {
        this.apiLayerClient = apiLayerClient;
        this.coinGeckoClient = coinGeckoClient;
        this.currencyRepository = currencyRepository;
    }

    @Override
    public ExchangeRate getExchangeRate(String source, String target) throws HttpResponseException {
        if(source.equals(target)) throw new HttpResponseException(ErrorCode.SAME_CURRENCY);
        CurrencyDataDTO sourceCurrency = this.currencyRepository.getFirstByCurrency(source);
        CurrencyDataDTO toCurrency = this.currencyRepository.getFirstByCurrency(target);

        if (sourceCurrency == null || toCurrency == null)
            throw new HttpResponseException(ErrorCode.INVALID_CURRENCY_TYPE);

        BigDecimal exchangeRate;
        if (sourceCurrency.getProvider() == toCurrency.getProvider() &&
                sourceCurrency.getProvider() == CurrencyProvider.ApiLayer) {
            ApiLayerLiveResponseDTO rate = apiLayerClient.getRate(source, target);
            exchangeRate = rate.getQuotes().get(source + "" + target);
        } else {
            BigDecimal sourceRate = getUsdRate(sourceCurrency);
            BigDecimal toRate = getUsdRate(toCurrency);
            exchangeRate = toRate.divide(sourceRate, RoundingMode.HALF_UP);
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
