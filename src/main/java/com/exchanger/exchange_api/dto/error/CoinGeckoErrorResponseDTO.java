package com.exchanger.exchange_api.dto.error;

public class CoinGeckoErrorResponseDTO implements IRequestError {
    private String error;

    @Override
    public String getMessage() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
