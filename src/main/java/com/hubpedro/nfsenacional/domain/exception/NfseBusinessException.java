package com.hubpedro.nfsenacional.domain.exception;

/**
 * Exceção de negócio para validações e regras da NFS-e.
 */
public class NfseBusinessException extends NFSeException {

    public NfseBusinessException(String message) {
        super(message);
    }

    public NfseBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
