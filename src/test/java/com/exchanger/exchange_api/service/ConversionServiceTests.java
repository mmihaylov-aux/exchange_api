package com.exchanger.exchange_api.service;

import com.exchanger.exchange_api.domain.ExchangeRate;
import com.exchanger.exchange_api.dto.response.ConversionListResponseDTO;
import com.exchanger.exchange_api.dto.response.ConversionResponseDTO;
import com.exchanger.exchange_api.enumeration.ErrorCode;
import com.exchanger.exchange_api.exception.HttpResponseException;
import com.exchanger.exchange_api.model.ConversionModel;
import com.exchanger.exchange_api.repository.ConversionRepository;
import com.exchanger.exchange_api.service.internal.ConversionServiceImpl;
import com.exchanger.exchange_api.service.internal.ExchangeRateServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.*;

@RunWith(SpringRunner.class)
public class ConversionServiceTests {
    @Mock
    ConversionRepository conversionRepository;

    @Mock
    ExchangeRateServiceImpl exchangeRateService;

    @InjectMocks
    ConversionServiceImpl conversionService;

    final String source = "EUR";
    final String target = "USD";
    final BigDecimal exchangeRateFromEurToUsd = BigDecimal.valueOf(1.123);
    final UUID conversionModelNewUUID = UUID.randomUUID();

    final List<ConversionModel> dbTransactions = Arrays.stream(new ConversionModel[]{
            new ConversionModel(UUID.randomUUID()),
            new ConversionModel(UUID.randomUUID()),
            new ConversionModel(UUID.randomUUID())
    }).toList();

    @Before
    public void prepareMocks() throws HttpResponseException {
        Mockito.when(exchangeRateService.getExchangeRate(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(new ExchangeRate(exchangeRateFromEurToUsd));

        Mockito.when(conversionRepository.save(Mockito.any(ConversionModel.class)))
                .thenAnswer(invocationOnMock -> {
                    ConversionModel model = invocationOnMock.getArgument(0, ConversionModel.class);
                    model.setId(conversionModelNewUUID);
                    return model;
                });

        Mockito.when(conversionRepository.findById(Mockito.anyString()))
                .thenAnswer(invocationOnMock -> Optional.empty());

        Mockito.when(conversionRepository.findById(conversionModelNewUUID.toString()))
                .thenAnswer(invocationOnMock -> {
                    ConversionModel model = new ConversionModel();
                    model.setId(conversionModelNewUUID);
                    return Optional.of(model);
                });

        Mockito.when(conversionRepository.findAllByCreatedAtBetween(
                Mockito.any(), Mockito.any(), Mockito.any()))
                .thenAnswer(invocationOnMock -> new PageImpl<>(dbTransactions,
                        invocationOnMock.getArgument(2), dbTransactions.size()));
    }

    @Test
    public void when_convert_should_return_correct_value_and_id() {
        final BigDecimal conversionValue = BigDecimal.valueOf(100);

        try {
            ConversionResponseDTO output = conversionService.convert(conversionValue, source, target);

            Assert.assertEquals(exchangeRateFromEurToUsd.multiply(conversionValue), output.getValue());
            Assert.assertEquals(conversionModelNewUUID.toString(), output.getTransactionId());

        } catch (HttpResponseException e) {
            Assert.assertNull("An exception should not be thrown", e);
        }
    }

    @Test
    public void when_listing_transaction_by_id_should_return_correct_entry() {
        try {
            ConversionListResponseDTO output = conversionService.listTransactions(
                    conversionModelNewUUID.toString(), null, 0, 10);

            Assert.assertEquals(1, output.getConversions().size());
            Assert.assertEquals(conversionModelNewUUID.toString(), output.getConversions().get(0).getId());

        } catch (HttpResponseException e) {
            Assert.assertNull("An exception should not be thrown", e);
        }
    }

    @Test
    public void when_listing_transaction_by_no_id_or_date_should_throw_exception() {
        try {
            conversionService.listTransactions(
                    conversionModelNewUUID + "_INVALID", null, 0, 10);
        } catch (HttpResponseException e) {
            Assert.assertNotNull("An exception should be thrown", e);
            Assert.assertEquals(ErrorCode.CONVERSION_LIST_ID_NOT_FOUND, e.getCode());
        }
    }

    @Test
    public void when_listing_transaction_by_incorrect_id_should_throw_exception() {
        try {
            conversionService.listTransactions(null, null, 0, 10);
        } catch (HttpResponseException e) {
            Assert.assertNotNull("An exception should be thrown", e);
            Assert.assertEquals(ErrorCode.CONVERSION_LIST_PROVIDE_ID_OR_DATE, e.getCode());
        }
    }

    @Test
    public void when_listing_transaction_by_date_should_return_all_transactions() {
        final int page = 0;
        final int pageAmount = 10;
        try {
            ConversionListResponseDTO output = conversionService.listTransactions(
                    null, new Date(), page, pageAmount);

            if (dbTransactions.size() <= pageAmount)
                Assert.assertEquals(dbTransactions.size(), output.getConversions().size());

            Assert.assertEquals(page, output.getCurrentPage());
            Assert.assertTrue(output.getConversions().size() <= pageAmount);
            Assert.assertEquals(page, output.getMaxPage());

        } catch (HttpResponseException e) {
            Assert.assertNull("An exception should not be thrown", e);
        }
    }

    @Test
    public void when_listing_transaction_by_date_should_return_paged_transactions() {
        for (int i = 0; i < dbTransactions.size(); i++) testPagination(i, 1);
    }

    private void testPagination(int page, int pageAmount){
        try {
            ConversionListResponseDTO output = conversionService.listTransactions(
                    null, new Date(), page, pageAmount);

            if (dbTransactions.size() <= pageAmount)
                Assert.assertEquals(dbTransactions.size(), output.getConversions().size());

            Assert.assertEquals(page, output.getCurrentPage());
            Assert.assertEquals(dbTransactions.size() / pageAmount - 1, output.getMaxPage());

        } catch (HttpResponseException e) {
            Assert.assertNull("An exception should not be thrown", e);
        }
    }
}
