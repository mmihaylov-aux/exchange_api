package com.exchanger.exchange_api.dto.response;

import java.math.BigDecimal;

public class ExchangeRateResponseDTO {
    private BigDecimal value;

    public ExchangeRateResponseDTO(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
