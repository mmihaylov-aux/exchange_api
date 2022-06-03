package com.exchanger.exchange_api.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CoinGeckoLiveResponseDTO {
    private final MarketDataDTO marketDataDTO;

    @JsonCreator
    public CoinGeckoLiveResponseDTO(@JsonProperty(value = "market_data", required = true)
                                            MarketDataDTO marketDataDTO) {
        this.marketDataDTO = marketDataDTO;
    }

    public MarketDataDTO getMarketDataDTO() {
        return marketDataDTO;
    }

    public static class MarketDataDTO {
        private final Map<String, BigDecimal> currentPrice;

        @JsonCreator
        public MarketDataDTO(@JsonProperty(value = "current_price", required = true)
                             @JsonDeserialize(using = PriceDeserializer.class)
                                     Map<String, BigDecimal> currentPrice) {
            this.currentPrice = currentPrice;
        }

        public Map<String, BigDecimal> getCurrentPrice() {
            return currentPrice;
        }
    }

    static class PriceDeserializer extends JsonDeserializer<Map<String, BigDecimal>> {

        @Override
        public Map<String, BigDecimal> deserialize(JsonParser jsonParser,
                                                   DeserializationContext deserializationContext)
                throws IOException {
            HashMap<String, BigDecimal> output = new HashMap<>();
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            node.fieldNames().forEachRemaining(s ->
                    output.put(s, BigDecimal.valueOf(node.get(s).doubleValue()))
            );

            return output;
        }
    }
}