package com.exchanger.exchange_api.repository;

import com.exchanger.exchange_api.dto.CurrencyDataDTO;
import com.exchanger.exchange_api.model.CurrencyModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends CrudRepository<CurrencyModel, Integer> {
    CurrencyDataDTO getFirstByCurrency(String currency);
}
