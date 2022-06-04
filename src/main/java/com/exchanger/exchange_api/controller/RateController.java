package com.exchanger.exchange_api.controller;

import com.exchanger.exchange_api.domain.ExchangeRate;
import com.exchanger.exchange_api.exception.HttpResponseException;
import com.exchanger.exchange_api.service.ExchangeRateService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rate")
@Validated
@Api
public class RateController {
    private final ExchangeRateService exchangeRateService;

    @Autowired
    public RateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/{source}/{target}")
    public ResponseEntity<ExchangeRate> getExchangeRate(@PathVariable("source") String source,
                                                        @PathVariable("target") String target) throws HttpResponseException {
        return new ResponseEntity<>(exchangeRateService.getExchangeRate(source, target), HttpStatus.OK);
    }

    @GetMapping("/currencies")
    public ResponseEntity<String[]> listCurrencies() {
        return new ResponseEntity<>(exchangeRateService.listCurrencies(), HttpStatus.OK);
    }
}
