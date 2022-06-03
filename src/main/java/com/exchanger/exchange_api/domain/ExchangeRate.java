package com.exchanger.exchange_api.domain;

import java.math.BigDecimal;

public class ExchangeRate {
    private BigDecimal value;

    public ExchangeRate(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
