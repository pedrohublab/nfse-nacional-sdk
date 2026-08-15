package com.hubpedro.nfsenacional.domain.exception;

/**
 * Exceção genérica para erros de validação estrutural.
 */
public class ValidationException extends NFSeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
