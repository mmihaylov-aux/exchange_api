package com.exchanger.exchange_api.model;

import com.exchanger.exchange_api.enumeration.CurrencyProvider;

import javax.persistence.*;

@Entity
@Table
public class CurrencyModel {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String currency;

    @Column
    private String currencyId;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private CurrencyProvider provider;

    public CurrencyModel() {
    }

    public CurrencyModel(String currency, String currencyId, CurrencyProvider provider) {
        this.currency = currency;
        this.currencyId = currencyId;
        this.provider = provider;
    }

    public CurrencyModel(String currency, CurrencyProvider provider) {
        this.currency = currency;
        this.provider = provider;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public CurrencyProvider getProvider() {
        return provider;
    }

    public void setProvider(CurrencyProvider provider) {
        this.provider = provider;
    }
}
