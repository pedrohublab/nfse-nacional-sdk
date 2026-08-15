package com.hubpedro.nfsenacional.domain.exception;

/**
 * Exceção semântica para erros de validação retornados pela SEFIN Nacional (HTTP 400/422).
 */
public class SefinValidationException extends SefinApiException {

    public SefinValidationException(String message) {
        super(message, "422", 422, null, null);
    }

    public SefinValidationException(String message, String codigoError, int httpStatus) {
        super(message, codigoError, httpStatus, null, null);
    }

    public SefinValidationException(String message, String codigoError, int httpStatus, String responseBody) {
        super(message, codigoError, httpStatus, responseBody, null);
    }
}
