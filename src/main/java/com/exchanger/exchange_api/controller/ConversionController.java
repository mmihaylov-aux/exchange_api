package com.exchanger.exchange_api.controller;

import com.exchanger.exchange_api.dto.request.ConversionRequestDTO;
import com.exchanger.exchange_api.dto.response.ConversionResponseDTO;
import com.exchanger.exchange_api.exception.HttpResponseException;
import com.exchanger.exchange_api.service.internal.ConversionService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("conversion")
@Api
public class ConversionController {
    private final ConversionService conversionService;

    @Autowired
    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostMapping
    public ResponseEntity<ConversionResponseDTO> getExchangeRate(@RequestBody ConversionRequestDTO body) throws HttpResponseException {
        return new ResponseEntity<>(conversionService.convert(body.getAmount(), body.getSource(), body.getTarget()), HttpStatus.OK);
    }
}
