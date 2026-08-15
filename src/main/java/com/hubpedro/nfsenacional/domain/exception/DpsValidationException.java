package com.hubpedro.nfsenacional.domain.exception;

/**
 * Exceção para erros de validação prévia dos dados da DPS no SDK.
 */
public class DpsValidationException extends NFSeException {

    public DpsValidationException(String message) {
        super(message);
    }

    public DpsValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
