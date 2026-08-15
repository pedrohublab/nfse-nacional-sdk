package com.hubpedro.nfsenacional.domain.exception;

/**
 * Exceção lançada quando um CPF informado é inválido.
 */
public class CpfInvalidoException extends NFSeException {

    public CpfInvalidoException(String message) {
        super(message);
    }
}
