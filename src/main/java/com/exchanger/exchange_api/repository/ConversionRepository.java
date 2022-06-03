package com.exchanger.exchange_api.repository;

import com.exchanger.exchange_api.model.ConversionModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversionRepository extends CrudRepository<ConversionModel, String> {
}
