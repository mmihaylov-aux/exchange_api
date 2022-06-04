package com.exchanger.exchange_api.controller;

import com.exchanger.exchange_api.dto.request.ConversionRequestDTO;
import com.exchanger.exchange_api.enumeration.ErrorCode;
import com.exchanger.exchange_api.exception.HttpResponseException;
import com.exchanger.exchange_api.service.ConversionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Calendar;

import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ConversionControllerTests {
    @Mock
    ConversionService conversionService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    MockMvc mvc;

    final String conversionUrl = "/conversion";

    final String dateParamName = "date";
    final String pageParamName = "page";
    final String pageAmountParamName = "pageAmount";
    final int pageAmountMaxValue = 100;

    final String source = "USD";
    final String target = "BTC";

    final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void prepareMocks() throws HttpResponseException {
        Mockito.when(conversionService.convert(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new HttpResponseException(ErrorCode.INTERNAL_ERROR, "This should not be called"));
    }

    @Test
    public void when_conversion_body_zero_value_should_throw_error() {
        expectErrorCode(MockMvcRequestBuilders.post(conversionUrl)
                .content(body(new ConversionRequestDTO(BigDecimal.ZERO, source, target)))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void when_conversion_body_missing_should_throw_error() {
        expectErrorCode(MockMvcRequestBuilders.post(conversionUrl));
        expectErrorCode(MockMvcRequestBuilders.post(conversionUrl)
                .content("{}")
                .contentType("application/json"));
    }

    @Test
    public void when_conversion_body_invalid_should_throw_error() {
        expectErrorCode(MockMvcRequestBuilders.post(conversionUrl)
                .content("}")
                .contentType("application/json"));
    }

    @Test
    public void when_conversion_body_negative_value_should_throw_error() {
        expectErrorCode(MockMvcRequestBuilders.post(conversionUrl)
                .content(body(new ConversionRequestDTO(BigDecimal.valueOf(-1), source, target)))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void when_conversion_body_null_source_should_throw_error() {
        expectErrorCode(MockMvcRequestBuilders.post(conversionUrl)
                .content(body(new ConversionRequestDTO(BigDecimal.ONE, null, target)))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void when_conversion_body_null_target_should_throw_error() {
        expectErrorCode(MockMvcRequestBuilders.post(conversionUrl)
                .content(body(new ConversionRequestDTO(BigDecimal.ONE, source, null)))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void when_conversion_get_invalid_date_format_should_throw_error() {
        final String invalidMonth = "20.0.2021";
        final String invalidDay = "32.2.2022";

        expectErrorCode(MockMvcRequestBuilders.get(conversionUrl)
                .queryParam(dateParamName, invalidMonth)
                .contentType(MediaType.APPLICATION_JSON));
        expectErrorCode(MockMvcRequestBuilders.get(conversionUrl)
                .queryParam(dateParamName, invalidDay)
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void when_conversion_get_future_date_format_should_throw_error() {
        final Calendar now = Calendar.getInstance();
        now.add(Calendar.HOUR, 24);

        expectErrorCode(MockMvcRequestBuilders.get(conversionUrl)
                .queryParam(dateParamName, "%s.%s.%s"
                        .formatted(now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.MONTH) + 1, now.get(Calendar.YEAR)))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void when_conversion_get_negative_page_should_throw_error() {
        final String negativeValue = "-1";

        expectErrorCode(MockMvcRequestBuilders.get(conversionUrl)
                .queryParam(pageParamName, negativeValue)
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void when_conversion_get_negative_page_amount_should_throw_error() {
        final String negativeValue = "-1";

        expectErrorCode(MockMvcRequestBuilders.get(conversionUrl)
                .queryParam(pageAmountParamName, negativeValue)
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void when_conversion_get_zero_page_amount_should_throw_error() {
        final String zeroValue = "0";

        expectErrorCode(MockMvcRequestBuilders.get(conversionUrl)
                .queryParam(pageAmountParamName, zeroValue)
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void when_conversion_get_over_max_page_amount_should_throw_error() {
        final String overMaxValue = "%s".formatted(pageAmountMaxValue + 1);

        expectErrorCode(MockMvcRequestBuilders.get(conversionUrl)
                .queryParam(pageAmountParamName, overMaxValue)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private void expectErrorCode(MockHttpServletRequestBuilder request) {
        try {
            mvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.code",
                            is(ErrorCode.VALIDATION_ERROR.getCode())));
        } catch (Exception e) {
            Assert.assertNull("An exception should not be thrown", e);
        }
    }

    private String body(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            Assert.assertNull("An exception should not be thrown", e);
        }
        return "";
    }
}
