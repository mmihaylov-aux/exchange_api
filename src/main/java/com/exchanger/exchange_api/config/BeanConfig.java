package com.exchanger.exchange_api.config;

import com.exchanger.exchange_api.config.properties.HttpClientProperties;
import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Component
public class BeanConfig {
    @Bean
    public WebClient.Builder webClientBuilder(HttpClientProperties httpClientProperties) {
        if(httpClientProperties.getTimeout() == 0)
            httpClientProperties.setTimeout(2000);

        final int size = 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .codecs(configurer -> configurer.defaultCodecs().enableLoggingRequestDetails(true))
                .build();

        final HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, httpClientProperties.getTimeout())
                .responseTimeout(Duration.ofMillis(httpClientProperties.getTimeout()));

        return WebClient.builder()
                .exchangeStrategies(strategies)
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }
}
