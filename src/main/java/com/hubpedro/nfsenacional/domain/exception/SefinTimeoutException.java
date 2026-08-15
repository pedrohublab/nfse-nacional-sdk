package com.hubpedro.nfsenacional.domain.exception;

/**
 * Exceção lançada quando ocorre timeout na conexão ou leitura com a SEFIN Nacional (HTTP 504 ou SocketTimeout).
 */
public class SefinTimeoutException extends SefinApiException {

    public SefinTimeoutException(String message) {
        super(message, "504", 504, null, null);
    }

    public SefinTimeoutException(String message, Throwable cause) {
        super(message, "504", 504, null, cause);
    }
}
