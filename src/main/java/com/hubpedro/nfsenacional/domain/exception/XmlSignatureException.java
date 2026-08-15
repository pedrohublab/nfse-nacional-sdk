package com.hubpedro.nfsenacional.domain.exception;

/**
 * Exceção lançada quando ocorre erro na assinatura digital XMLDSig.
 */
public class XmlSignatureException extends NFSeException {

    public XmlSignatureException(String message) {
        super(message);
    }

    public XmlSignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
