package com.exchanger.exchange_api.dto.response;

import java.math.BigDecimal;

public class ConversionResponseDTO {
    private BigDecimal value;
    private String transactionId;

    public ConversionResponseDTO() {
    }

    public ConversionResponseDTO(BigDecimal value, String transactionId) {
        this.value = value;
        this.transactionId = transactionId;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
