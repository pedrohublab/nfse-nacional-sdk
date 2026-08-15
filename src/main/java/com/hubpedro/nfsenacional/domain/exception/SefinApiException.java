package com.hubpedro.nfsenacional.domain.exception;

/**
 * Exceção lançada para erros retornados pela API REST da SEFIN Nacional / ADN.
 */
public class SefinApiException extends NFSeException {

    private final String codigoError;
    private final int httpStatus;
    private final String responseBody;

    public SefinApiException(String message) {
        this(message, null, 0, null, null);
    }

    public SefinApiException(String message, String codigoError, int httpStatus) {
        this(message, codigoError, httpStatus, null, null);
    }

    public SefinApiException(String message, String codigoError, int httpStatus, String responseBody) {
        this(message, codigoError, httpStatus, responseBody, null);
    }

    public SefinApiException(String message, String codigoError, int httpStatus, String responseBody, Throwable cause) {
        super(message, cause);
        this.codigoError = codigoError;
        this.httpStatus = httpStatus;
        this.responseBody = responseBody;
    }

    public String getCodigoError() {
        return codigoError;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
