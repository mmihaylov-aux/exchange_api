package com.exchanger.exchange_api.service;

import com.exchanger.exchange_api.dto.response.ConversionListResponseDTO;
import com.exchanger.exchange_api.dto.response.ConversionResponseDTO;
import com.exchanger.exchange_api.exception.HttpResponseException;

import java.math.BigDecimal;
import java.util.Date;

public interface ConversionService {
    ConversionResponseDTO convert(BigDecimal value, String source, String target) throws HttpResponseException;

    ConversionListResponseDTO listTransactions(String transactionId,
                                               Date transactionDate,
                                               int page, int pageAmount) throws HttpResponseException;
}
