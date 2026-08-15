package com.hubpedro.nfsenacional.domain.exception;

/**
 * Exceção semântica lançada quando a DPS já foi emitida anteriormente (duplicidade / código 204).
 */
public class SefinDuplicatedEmissionException extends SefinApiException {

    public SefinDuplicatedEmissionException(String message) {
        super(message, "204", 422, null, null);
    }

    public SefinDuplicatedEmissionException(String message, String codigoError, int httpStatus) {
        super(message, codigoError, httpStatus, null, null);
    }

    public SefinDuplicatedEmissionException(String message, String codigoError, int httpStatus, String responseBody) {
        super(message, codigoError, httpStatus, responseBody, null);
    }
}
