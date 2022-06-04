package com.exchanger.exchange_api.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel
public class ConversionResponseDTO {
    @ApiModelProperty(example = "100.1")
    private BigDecimal value;
    @ApiModelProperty(example = "UUID")
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
