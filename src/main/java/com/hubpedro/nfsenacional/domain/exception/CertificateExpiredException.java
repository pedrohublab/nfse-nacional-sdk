package com.hubpedro.nfsenacional.domain.exception;

/**
 * Exceção semântica lançada quando o certificado digital A1 está expirado.
 */
public class CertificateExpiredException extends CertificateException {

    public CertificateExpiredException(String message) {
        super("Certificado digital expirado: " + message);
    }
}
