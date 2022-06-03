package com.exchanger.exchange_api.dto.error;

public class ApiLayerErrorResponseDTO implements IRequestError {
    private ApiLayerErrorInfo error;
    private String message;

    @Override
    public String getMessage() {
        return message != null ? message : error.getInfo();
    }


    public void setError(ApiLayerErrorInfo error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    static class ApiLayerErrorInfo {
        private String info;

        public void setInfo(String info) {
            this.info = info;
        }

        public String getInfo() {
            return info;
        }
    }
}