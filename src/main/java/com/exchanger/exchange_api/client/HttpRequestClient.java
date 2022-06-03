package com.exchanger.exchange_api.client;

import com.exchanger.exchange_api.dto.error.IRequestError;
import com.exchanger.exchange_api.enumeration.ErrorCode;
import com.exchanger.exchange_api.exception.HttpBadRequestException;
import com.exchanger.exchange_api.exception.HttpResponseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

public abstract class HttpRequestClient {
    private final Logger logger = LoggerFactory.getLogger("API CALL");

    private final WebClient webClient;
    private final Class<? extends IRequestError> errorType;
    private final ObjectMapper objectMapper;

    protected HttpRequestClient(WebClient.Builder clientBuilder, Class<? extends IRequestError> errorType) {
        this.webClient = clientBuilder.filter(responseHandler()).build();
        this.errorType = errorType;
        this.objectMapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    protected <T> T get(String url, Class<T> responseType) throws HttpResponseException {
        String responseBody = null;
        try {
            responseBody = webClient
                    .get().uri(url)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse ->
                            clientResponse.bodyToMono(String.class).map(HttpBadRequestException::new))
                    .bodyToMono(String.class)
                    .onErrorMap(Predicate.not(HttpResponseException.class::isInstance)
                                    .and(Predicate.not(HttpBadRequestException.class::isInstance)),
                            throwable -> new HttpResponseException(ErrorCode.INTERNAL_ERROR))
                    .block();

            logResponseBody(responseBody);

            return this.objectMapper.readValue(responseBody, responseType);
        } catch (Exception e) {
            if (e instanceof JsonProcessingException && responseBody != null) {
                try {
                    throwBadRequestException(responseBody);
                } catch (JsonProcessingException jsonMappingException) {
                    e = jsonMappingException;
                }
            }

            Throwable unwrap = Exceptions.unwrap(e);

            if (unwrap instanceof HttpResponseException exceptionResponse)
                throw exceptionResponse;

            if (unwrap instanceof HttpBadRequestException badRequest) {
                logResponseBody(badRequest.getMessage());
                try {
                    throwBadRequestException(badRequest.getMessage());
                } catch (JsonProcessingException jsonMappingException) {
                    e = jsonMappingException;
                }
            }
            logger.error("Error getting response: {}", e.getMessage());
            throw new HttpResponseException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private void throwBadRequestException(String message) throws HttpResponseException, JsonProcessingException {
        IRequestError responseMessage = this.objectMapper.readValue(message, errorType);
        throw new HttpResponseException(ErrorCode.EXTERNAL_API_BAD_REQUEST, responseMessage.getMessage());
    }

    private ExchangeFilterFunction responseHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError())
                return logResponseBody(clientResponse, Mono.error(new HttpResponseException(ErrorCode.EXTERNAL_API_ERROR)));

            return Mono.just(clientResponse);
        });
    }

    private Mono<ClientResponse> logResponseBody(ClientResponse clientResponse, Mono<ClientResponse> mono) {
        return clientResponse.bodyToMono(String.class)
                .flatMap(body -> {
                    logResponseBody(body);
                    return mono;
                });
    }

    private void logResponseBody(String body) {
        logger.info("Response body: \n{}", body);
    }
}
