package com.hubpedro.nfsenacional.domain.exception;

/**
 * Alias de compatibilidade para CpfInvalidoException.
 */
public class CPFInvalido extends CpfInvalidoException {

    public CPFInvalido(String message) {
        super(message);
    }
}
