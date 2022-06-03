package com.exchanger.exchange_api.exception;

public class HttpBadRequestException extends Exception {
    private final String message;
    public HttpBadRequestException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
