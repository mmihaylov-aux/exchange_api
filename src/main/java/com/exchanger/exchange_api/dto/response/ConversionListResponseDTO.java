package com.exchanger.exchange_api.dto.response;

import com.exchanger.exchange_api.model.ConversionModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConversionListResponseDTO {
    private int currentPage;
    private int maxPage;
    private List<ConversionDTO> conversions;

    public ConversionListResponseDTO(int currentPage) {
        this(currentPage, 0, new ArrayList<>());
    }

    public ConversionListResponseDTO(int currentPage, int maxPage, List<ConversionDTO> conversions) {
        this.currentPage = currentPage;
        this.maxPage = maxPage;
        this.conversions = conversions;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        if (maxPage < 0) maxPage = 0;
        this.maxPage = maxPage;
    }

    public List<ConversionDTO> getConversions() {
        return conversions;
    }

    public void setConversions(List<ConversionDTO> conversions) {
        this.conversions = conversions;
    }


    public static class ConversionDTO {
        private String id;
        private String sourceCurrency;
        private String targetCurrency;
        private BigDecimal sourceAmount;
        private BigDecimal targetAmount;
        private BigDecimal exchangeRate;
        public Date createdAt;

        public ConversionDTO(ConversionModel model) {
            this.id = model.getId().toString();
            this.sourceCurrency = model.getSourceCurrency();
            this.targetCurrency = model.getTargetCurrency();
            this.sourceAmount = model.getSourceAmount();
            this.targetAmount = model.getTargetAmount();
            this.exchangeRate = model.getExchangeRate();
            this.createdAt = model.getCreatedAt();
        }

        public ConversionDTO(String id, String sourceCurrency, String targetCurrency, BigDecimal sourceAmount,
                             BigDecimal targetAmount, BigDecimal exchangeRate, Date createdAt) {
            this.id = id;
            this.sourceCurrency = sourceCurrency;
            this.targetCurrency = targetCurrency;
            this.sourceAmount = sourceAmount;
            this.targetAmount = targetAmount;
            this.exchangeRate = exchangeRate;
            this.createdAt = createdAt;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSourceCurrency() {
            return sourceCurrency;
        }

        public void setSourceCurrency(String sourceCurrency) {
            this.sourceCurrency = sourceCurrency;
        }

        public String getTargetCurrency() {
            return targetCurrency;
        }

        public void setTargetCurrency(String targetCurrency) {
            this.targetCurrency = targetCurrency;
        }

        public BigDecimal getSourceAmount() {
            return sourceAmount;
        }

        public void setSourceAmount(BigDecimal sourceAmount) {
            this.sourceAmount = sourceAmount;
        }

        public BigDecimal getTargetAmount() {
            return targetAmount;
        }

        public void setTargetAmount(BigDecimal targetAmount) {
            this.targetAmount = targetAmount;
        }

        public BigDecimal getExchangeRate() {
            return exchangeRate;
        }

        public void setExchangeRate(BigDecimal exchangeRate) {
            this.exchangeRate = exchangeRate;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }
    }
}
