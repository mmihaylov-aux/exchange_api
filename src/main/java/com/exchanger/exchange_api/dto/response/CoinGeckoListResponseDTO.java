package com.exchanger.exchange_api.dto.response;

public class CoinGeckoListResponseDTO {
    private String id;
    private String symbol;

    public CoinGeckoListResponseDTO() {
    }

    public String getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}