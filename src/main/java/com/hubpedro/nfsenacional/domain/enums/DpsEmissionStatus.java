package com.hubpedro.nfsenacional.domain.enums;

/**
 * Status normalizado de processamento e resposta da DPS.
 */
public enum DpsEmissionStatus {
    DRAFT,
    PENDING,
    AUTHORIZED,
    REJECTED,
    UNKNOWN,
    TIMEOUT;

    public boolean isAuthorized() {
        return this == AUTHORIZED;
    }

    public boolean isRejected() {
        return this == REJECTED;
    }

    public boolean isPendingConsultation() {
        return this == PENDING || this == UNKNOWN || this == TIMEOUT;
    }
}
