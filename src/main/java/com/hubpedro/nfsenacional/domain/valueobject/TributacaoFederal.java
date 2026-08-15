package com.hubpedro.nfsenacional.domain.valueobject;

import java.math.BigDecimal;

/**
 * Value Object que representa a tributação federal.
 * Imutável, construído via Builder, auto-validado.
 */
public final class TributacaoFederal {

    private final PisCofins pisCofins;
    private final BigDecimal valorRetencaoCP;
    private final BigDecimal valorRetencaoIRRF;
    private final BigDecimal valorRetencaoCSLL;

    private TributacaoFederal(Builder builder) {
        this.pisCofins = builder.pisCofins;
        this.valorRetencaoCP = builder.valorRetencaoCP;
        this.valorRetencaoIRRF = builder.valorRetencaoIRRF;
        this.valorRetencaoCSLL = builder.valorRetencaoCSLL;
    }

    public PisCofins getPisCofins() {
        return pisCofins;
    }

    public BigDecimal getValorRetencaoCP() {
        return valorRetencaoCP;
    }

    public BigDecimal getValorRetencaoIRRF() {
        return valorRetencaoIRRF;
    }

    public BigDecimal getValorRetencaoCSLL() {
        return valorRetencaoCSLL;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PisCofins pisCofins;
        private BigDecimal valorRetencaoCP;
        private BigDecimal valorRetencaoIRRF;
        private BigDecimal valorRetencaoCSLL;

        public Builder pisCofins(PisCofins pisCofins) {
            this.pisCofins = pisCofins;
            return this;
        }

        public Builder valorRetencaoCP(BigDecimal valorRetencaoCP) {
            this.valorRetencaoCP = valorRetencaoCP;
            return this;
        }

        public Builder valorRetencaoIRRF(BigDecimal valorRetencaoIRRF) {
            this.valorRetencaoIRRF = valorRetencaoIRRF;
            return this;
        }

        public Builder valorRetencaoCSLL(BigDecimal valorRetencaoCSLL) {
            this.valorRetencaoCSLL = valorRetencaoCSLL;
            return this;
        }

        public TributacaoFederal build() {
            return new TributacaoFederal(this);
        }
    }
}
