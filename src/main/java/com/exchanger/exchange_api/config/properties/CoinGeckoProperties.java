package com.exchanger.exchange_api.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "coingecko")
public class CoinGeckoProperties {
    private String url;
    private String key;

    public String getUrl() {
        return url;
    }

    public String getKey() {
        return key;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
