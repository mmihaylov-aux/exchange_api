package com.exchanger.exchange_api.exception;

import com.exchanger.exchange_api.dto.ErrorResponseDTO;
import com.exchanger.exchange_api.enumeration.ErrorCode;
import org.springframework.http.ResponseEntity;

public class HttpResponseException extends Exception {
    private ErrorCode code;
    private String message;

    public HttpResponseException(ErrorCode code) {
        this.code = code;
    }

    public HttpResponseException(ErrorCode code, String message) {
        this(code);
        this.message = message;
    }

    public ResponseEntity<ErrorResponseDTO> getResponse() {
        if (message == null) message = code.getMessage();
        return new ResponseEntity<>(new ErrorResponseDTO(code.getCode(), message), code.getStatus());
    }

    public ErrorCode getCode() {
        return code;
    }

    public void setCode(ErrorCode code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
