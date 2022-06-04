package com.exchanger.exchange_api.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel
public class ExchangeRateResponseDTO {
    @ApiModelProperty(example = "100.1")
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
