package com.hubpedro.nfsenacional.domain.valueobject;

import java.math.BigDecimal;

/**
 * Value Object que representa os valores de PIS e COFINS.
 * Imutável, construído via Builder, auto-validado.
 */
public final class PisCofins {

    private final BigDecimal valorPIS;
    private final BigDecimal valorCOFINS;

    private PisCofins(Builder builder) {
        this.valorPIS = builder.valorPIS;
        this.valorCOFINS = builder.valorCOFINS;
        validar();
    }

    private void validar() {
        if (valorPIS != null && valorPIS.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor do PIS não pode ser negativo");
        }
        if (valorCOFINS != null && valorCOFINS.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor do COFINS não pode ser negativo");
        }
    }

    public BigDecimal getValorPIS() {
        return valorPIS;
    }

    public BigDecimal getValorCOFINS() {
        return valorCOFINS;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BigDecimal valorPIS;
        private BigDecimal valorCOFINS;

        public Builder valorPIS(BigDecimal valorPIS) {
            this.valorPIS = valorPIS;
            return this;
        }

        public Builder valorCOFINS(BigDecimal valorCOFINS) {
            this.valorCOFINS = valorCOFINS;
            return this;
        }

        public PisCofins build() {
            return new PisCofins(this);
        }
    }
}
