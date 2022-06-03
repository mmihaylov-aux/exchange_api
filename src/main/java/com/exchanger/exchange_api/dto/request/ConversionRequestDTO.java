package com.exchanger.exchange_api.dto.request;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Valid
public class ConversionRequestDTO {
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;
    @NotNull
    private String source;
    @NotNull
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
