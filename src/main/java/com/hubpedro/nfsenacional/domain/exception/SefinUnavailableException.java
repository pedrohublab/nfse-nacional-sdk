package com.hubpedro.nfsenacional.domain.exception;

/**
 * Exceção semântica lançada quando a API da SEFIN está temporariamente indisponível (HTTP 500, 502, 503).
 */
public class SefinUnavailableException extends SefinApiException {

    public SefinUnavailableException(String message) {
        super(message, "500", 500, null, null);
    }

    public SefinUnavailableException(String message, String codigoError, int httpStatus) {
        super(message, codigoError, httpStatus, null, null);
    }

    public SefinUnavailableException(String message, String codigoError, int httpStatus, String responseBody) {
        super(message, codigoError, httpStatus, responseBody, null);
    }
}
