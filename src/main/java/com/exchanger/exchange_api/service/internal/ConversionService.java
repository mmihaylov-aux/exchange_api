package com.exchanger.exchange_api.service.internal;

import com.exchanger.exchange_api.domain.ExchangeRate;
import com.exchanger.exchange_api.dto.response.ConversionResponseDTO;
import com.exchanger.exchange_api.enumeration.ErrorCode;
import com.exchanger.exchange_api.exception.HttpResponseException;
import com.exchanger.exchange_api.model.ConversionModel;
import com.exchanger.exchange_api.repository.ConversionRepository;
import com.exchanger.exchange_api.service.IExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ConversionService {
   private final IExchangeRateService exchangeRateService;
   private final ConversionRepository conversionRepository;

   @Autowired
   public ConversionService(IExchangeRateService exchangeRateService, ConversionRepository conversionRepository) {
      this.exchangeRateService = exchangeRateService;
      this.conversionRepository = conversionRepository;
   }

   public ConversionResponseDTO convert(BigDecimal value, String source, String target) throws HttpResponseException {
      if (value.compareTo(BigDecimal.ZERO) <= 0) throw new HttpResponseException(ErrorCode.INVALID_CONVERSION_VALUE);

      final ExchangeRate exchangeRate = this.exchangeRateService.getExchangeRate(source, target);
      ConversionModel conversionModel = new ConversionModel(source, target, value,
              value.multiply(exchangeRate.getValue()), exchangeRate.getValue());

      conversionModel = conversionRepository.save(conversionModel);

      return new ConversionResponseDTO(conversionModel.getTargetAmount(), conversionModel.getId().toString());
   }
}
