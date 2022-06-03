package com.exchanger.exchange_api.dto.request;

import java.math.BigDecimal;

public class ConversionRequestDTO {
    private BigDecimal amount;
    private String source;
    private String target;

    public ConversionRequestDTO() {
    }

    public ConversionRequestDTO(BigDecimal amount, String source, String target) {
        this.amount = amount;
        this.source = source;
        this.target = target;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
