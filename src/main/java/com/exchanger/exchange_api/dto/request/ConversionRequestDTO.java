package com.exchanger.exchange_api.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Valid
@ApiModel
public class ConversionRequestDTO {
    @DecimalMin(value = "0.0", inclusive = false)
    @ApiModelProperty(example = "100.1", required = true)
    private BigDecimal amount;
    @NotNull
    @ApiModelProperty(example = "EUR", required = true)
    private String source;
    @NotNull
    @ApiModelProperty(example = "USD", required = true)
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
