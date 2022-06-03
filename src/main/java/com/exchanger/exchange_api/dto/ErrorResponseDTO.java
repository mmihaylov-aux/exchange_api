package com.exchanger.exchange_api.dto;

public class ErrorResponseDTO {
    private final int code;
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
