package com.exchanger.exchange_api.service.internal;

import com.exchanger.exchange_api.client.ApiLayerClient;
import com.exchanger.exchange_api.client.CoinGeckoClient;
import com.exchanger.exchange_api.dto.CurrencyDataDTO;
import com.exchanger.exchange_api.dto.response.ApiLayerLiveResponseDTO;
import com.exchanger.exchange_api.dto.response.CoinGeckoLiveResponseDTO;
import com.exchanger.exchange_api.dto.response.ExchangeRateResponseDTO;
import com.exchanger.exchange_api.enumeration.CurrencyProvider;
import com.exchanger.exchange_api.enumeration.ErrorCode;
import com.exchanger.exchange_api.exception.HttpResponseException;
import com.exchanger.exchange_api.model.CurrencyModel;
import com.exchanger.exchange_api.repository.CurrencyRepository;
import com.exchanger.exchange_api.service.ExchangeRateService;
import com.exchanger.exchange_api.util.CurrencyCombiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.StreamSupport;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {
    private final ApiLayerClient apiLayerClient;
    private final CoinGeckoClient coinGeckoClient;
    private final CurrencyRepository currencyRepository;

    private static final String MIDDLE_EXCHANGE_CURRENCY = "USD";
    private static final String MIDDLE_EXCHANGE_CURRENCY_LOWERCASE = MIDDLE_EXCHANGE_CURRENCY.toLowerCase();

    @Autowired
    public ExchangeRateServiceImpl(ApiLayerClient apiLayerClient,
                                   CoinGeckoClient coinGeckoClient,
                                   CurrencyRepository currencyRepository) {
        this.apiLayerClient = apiLayerClient;
        this.coinGeckoClient = coinGeckoClient;
        this.currencyRepository = currencyRepository;
    }

    @Override
    public ExchangeRateResponseDTO getExchangeRate(String source, String target) throws HttpResponseException {
        if(source.equals(target)) throw new HttpResponseException(ErrorCode.SAME_CURRENCY);
        CurrencyDataDTO sourceCurrency = this.currencyRepository.getFirstByCurrency(source);
        CurrencyDataDTO targetCurrency = this.currencyRepository.getFirstByCurrency(target);

        if (sourceCurrency == null || targetCurrency == null)
            throw new HttpResponseException(ErrorCode.INVALID_CURRENCY_TYPE);

        BigDecimal exchangeRate;
        if (sourceCurrency.getProvider() == targetCurrency.getProvider() &&
                sourceCurrency.getProvider() == CurrencyProvider.ApiLayer) {
            ApiLayerLiveResponseDTO rate = apiLayerClient.getRate(source, target);
            exchangeRate = rate.getQuotes().get(CurrencyCombiner.combine(source, target));
        } else {
            BigDecimal sourceRate = getUsdRate(sourceCurrency);
            BigDecimal targetRate = getUsdRate(targetCurrency);
            exchangeRate = targetRate.divide(sourceRate, RoundingMode.HALF_UP);
        }

        return new ExchangeRateResponseDTO(exchangeRate);
    }

    private BigDecimal getUsdRate(CurrencyDataDTO currencyData) throws HttpResponseException {
        switch (currencyData.getProvider()) {
            case ApiLayer -> {
                if (currencyData.getCurrency().equals(MIDDLE_EXCHANGE_CURRENCY)) return BigDecimal.ONE;
                return apiLayerClient.getRate(currencyData.getCurrency(), MIDDLE_EXCHANGE_CURRENCY)
                        .getQuotes().get(CurrencyCombiner.combine(currencyData.getCurrency(), MIDDLE_EXCHANGE_CURRENCY));
            }
            case CoinGecko -> {
                CoinGeckoLiveResponseDTO rate = this.coinGeckoClient.getRate(currencyData);
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
