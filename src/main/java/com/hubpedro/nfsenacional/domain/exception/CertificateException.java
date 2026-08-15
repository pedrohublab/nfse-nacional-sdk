package com.hubpedro.nfsenacional.domain.exception;

/**
 * Exceções relacionadas a certificados digitais A1/ICP-Brasil.
 */
public class CertificateException extends NFSeException {

    public CertificateException(String message) {
        super(message);
    }

    public CertificateException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class CertificateNotFoundException extends CertificateException {
        public CertificateNotFoundException(String path) {
            super("Certificado não encontrado no caminho: " + path);
        }
    }

    public static class CertificatePasswordNotFoundException extends CertificateException {
        public CertificatePasswordNotFoundException(String message) {
            super(message);
        }
    }

    public static class CertificatePathNotFoundException extends CertificateException {
        public CertificatePathNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidPasswordException extends CertificateException {
        public InvalidPasswordException() {
            super("Senha do certificado incorreta");
        }

        public InvalidPasswordException(Throwable cause) {
            super("Senha do certificado incorreta", cause);
        }
    }

    public static class CertificateExpiredException extends CertificateException {
        public CertificateExpiredException(String message) {
            super("Certificado expirado ou fora da validade: " + message);
        }
    }

    public static class InvalidCertificateTypeException extends CertificateException {
        public InvalidCertificateTypeException(String message) {
            super("Certificado inválido ou não ICP-Brasil: " + message);
        }
    }

    public static class SignatureException extends CertificateException {
        public SignatureException(String message, Throwable cause) {
            super("Erro ao assinar digitalmente: " + message, cause);
        }
    }

    public static class CnpjNotFoundException extends CertificateException {
        public CnpjNotFoundException() {
            super("CNPJ não encontrado nas extensões ou Subject do certificado digital");
        }

        public CnpjNotFoundException(String message) {
            super(message);
        }
    }
}
