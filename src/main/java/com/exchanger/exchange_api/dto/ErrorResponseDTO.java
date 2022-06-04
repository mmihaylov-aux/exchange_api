package com.exchanger.exchange_api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class ErrorResponseDTO {
    @ApiModelProperty(example = "1000")
    private final int code;
    @ApiModelProperty(example = "Error description")
    private String message;

    public ErrorResponseDTO(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
