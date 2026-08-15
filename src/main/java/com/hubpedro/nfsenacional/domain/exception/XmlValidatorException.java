package com.hubpedro.nfsenacional.domain.exception;

/**
 * Exceção para validação genérica de regras de negócio de XML.
 */
public class XmlValidatorException extends NFSeException {

    public XmlValidatorException(String message) {
        super(message);
    }

    public XmlValidatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
