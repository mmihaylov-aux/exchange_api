package com.exchanger.exchange_api.service.internal;

import com.exchanger.exchange_api.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Service
public class ValidationServiceImpl implements ValidationService {
    private final Validator validator;

    @Autowired
    public ValidationServiceImpl(Validator validator) {
        this.validator = validator;
    }

    public void validate(Object o) throws ConstraintViolationException {
        Set<ConstraintViolation<Object>> violations = validator.validate(o);
        if (!violations.isEmpty())
            throw new ConstraintViolationException(violations);
    }
}
