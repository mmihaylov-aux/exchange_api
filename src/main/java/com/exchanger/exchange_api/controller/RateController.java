package com.exchanger.exchange_api.controller;

import com.exchanger.exchange_api.domain.ExchangeRate;
import com.exchanger.exchange_api.exception.HttpResponseException;
import com.exchanger.exchange_api.service.IRateService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rate")
@Api
public class RateController {
    private final IRateService exchangeRateService;

    @Autowired
    public RateController(IRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/{from}/{to}")
    public ResponseEntity<ExchangeRate> getExchangeRate(@PathVariable("from") String from,
                                                        @PathVariable("to") String to) throws HttpResponseException {
        return new ResponseEntity<>(exchangeRateService.getExchangeRate(from, to), HttpStatus.OK);
    }

    @GetMapping("/currencies")
    public ResponseEntity<String[]> listCurrencies() {
        return new ResponseEntity<>(exchangeRateService.listCurrencies(), HttpStatus.OK);
    }
}
