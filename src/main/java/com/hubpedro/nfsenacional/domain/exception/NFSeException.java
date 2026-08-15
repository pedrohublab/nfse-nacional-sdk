package com.hubpedro.nfsenacional.domain.exception;

/**
 * Exceção base de runtime para todas as operações do SDK NFS-e Nacional.
 */
public class NFSeException extends RuntimeException {

    public NFSeException(String message) {
        super(message);
    }

    public NFSeException(String message, Throwable cause) {
        super(message, cause);
    }
}
