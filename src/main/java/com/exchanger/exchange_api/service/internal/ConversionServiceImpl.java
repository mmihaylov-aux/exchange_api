package com.exchanger.exchange_api.service.internal;

import com.exchanger.exchange_api.domain.ExchangeRate;
import com.exchanger.exchange_api.dto.response.ConversionListResponseDTO;
import com.exchanger.exchange_api.dto.response.ConversionResponseDTO;
import com.exchanger.exchange_api.enumeration.ErrorCode;
import com.exchanger.exchange_api.exception.HttpResponseException;
import com.exchanger.exchange_api.model.ConversionModel;
import com.exchanger.exchange_api.repository.ConversionRepository;
import com.exchanger.exchange_api.service.ExchangeRateService;
import com.exchanger.exchange_api.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConversionServiceImpl implements com.exchanger.exchange_api.service.ConversionService {
    private final ExchangeRateService exchangeRateService;
    private final ConversionRepository conversionRepository;

    @Autowired
    public ConversionServiceImpl(ExchangeRateService exchangeRateService, ConversionRepository conversionRepository) {
        this.exchangeRateService = exchangeRateService;
        this.conversionRepository = conversionRepository;
    }

    public ConversionResponseDTO convert(BigDecimal value, String source, String target) throws HttpResponseException {
        final ExchangeRate exchangeRate = this.exchangeRateService.getExchangeRate(source, target);
        ConversionModel conversionModel = new ConversionModel(source, target, value,
                value.multiply(exchangeRate.getValue()), exchangeRate.getValue());

        conversionModel = conversionRepository.save(conversionModel);

        return new ConversionResponseDTO(conversionModel.getTargetAmount(), conversionModel.getId().toString());
    }

    public ConversionListResponseDTO listTransactions(String transactionId,
                                                      Date transactionDate,
                                                      int page, int pageAmount) throws HttpResponseException {
        ConversionListResponseDTO response = new ConversionListResponseDTO(page);
        if (transactionId != null) {
            Optional<ConversionModel> conversion = this.conversionRepository.findById(transactionId);
            if (conversion.isEmpty()) throw new HttpResponseException(ErrorCode.CONVERSION_LIST_ID_NOT_FOUND);

            response.getConversions().add(new ConversionListResponseDTO.ConversionDTO(conversion.get()));
            return response;
        }

        if (transactionDate == null)
            throw new HttpResponseException(ErrorCode.CONVERSION_LIST_PROVIDE_ID_OR_DATE);

        Page<ConversionModel> allByCreatedAt = this.conversionRepository
                .findAllByCreatedAtBetween(transactionDate, DateUtils.getTomorrow(transactionDate),
                        PageRequest.of(page, pageAmount, Sort.by("createdAt").descending()));

        response.setConversions(allByCreatedAt.get().map(ConversionListResponseDTO.ConversionDTO::new)
                .collect(Collectors.toList()));
        response.setMaxPage(allByCreatedAt.getTotalPages() - 1);

        return response;
    }

}
