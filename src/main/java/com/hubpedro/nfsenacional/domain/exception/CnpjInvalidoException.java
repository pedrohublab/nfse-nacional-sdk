package com.hubpedro.nfsenacional.domain.exception;

/**
 * Exceção lançada quando um CNPJ informado é inválido.
 */
public class CnpjInvalidoException extends NFSeException {

    public CnpjInvalidoException(String message) {
        super(message);
    }
}
