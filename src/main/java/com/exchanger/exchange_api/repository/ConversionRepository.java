package com.exchanger.exchange_api.repository;

import com.exchanger.exchange_api.model.ConversionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ConversionRepository extends CrudRepository<ConversionModel, String> {
    Page<ConversionModel> findAllByCreatedAtBetween(Date start, Date end, Pageable pageable);
}
