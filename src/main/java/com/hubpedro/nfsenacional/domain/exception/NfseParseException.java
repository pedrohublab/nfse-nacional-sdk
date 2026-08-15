package com.hubpedro.nfsenacional.domain.exception;

/**
 * Exceção lançada quando ocorre erro no parsing ou decodificação de retorno da NFS-e / SEFIN.
 */
public class NfseParseException extends NFSeException {

    public NfseParseException(String message) {
        super(message);
    }

    public NfseParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
