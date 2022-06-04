package com.exchanger.exchange_api.controller;

import com.exchanger.exchange_api.dto.ErrorResponseDTO;
import com.exchanger.exchange_api.dto.response.ExchangeRateResponseDTO;
import com.exchanger.exchange_api.exception.HttpResponseException;
import com.exchanger.exchange_api.service.ExchangeRateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("exchange-rate")
@Validated
@Api(tags = {"Exchange Rate API"})
public class ExchangeRateController {
    private final ExchangeRateService exchangeRateService;

    @Autowired
    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping(value = "/{source}/{target}", produces = "application/json")
    @ApiOperation("Get the exchange rate between two currencies")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success",
                    response = ExchangeRateResponseDTO.class),
            @ApiResponse(code = 400, message = "Invalid/same currencies",
                    response = ErrorResponseDTO.class),
    })
    public ResponseEntity<ExchangeRateResponseDTO> getExchangeRate(@PathVariable("source") String source,
                                                                   @PathVariable("target") String target) throws HttpResponseException {
        return new ResponseEntity<>(exchangeRateService.getExchangeRate(source, target), HttpStatus.OK);
    }

    @GetMapping(value = "/currencies", produces = "application/json")
    @ApiOperation("List all available currencies")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success",
                    response = String[].class)
    })
    public ResponseEntity<String[]> listCurrencies() {
        return new ResponseEntity<>(exchangeRateService.listCurrencies(), HttpStatus.OK);
    }
}
