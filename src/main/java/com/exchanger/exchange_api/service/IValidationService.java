package com.exchanger.exchange_api.service;

import javax.validation.ConstraintViolationException;

public interface IValidationService {
    void validate(Object o) throws ConstraintViolationException;
}
