package com.hubpedro.nfsenacional.domain.valueobject;

import com.hubpedro.nfsenacional.domain.enums.IndicadorTotalTributos;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object que representa o total de tributos.
 * Imutável, construído via Builder, auto-validado.
 */
public final class TotalTributos {

    private final IndicadorTotalTributos indicadorTotalTributos;
    private final BigDecimal percentualTotalTributosSN;

    private TotalTributos(Builder builder) {
        this.indicadorTotalTributos = Objects.requireNonNull(builder.indicadorTotalTributos,
                "Indicador de total de tributos é obrigatório");
        this.percentualTotalTributosSN = builder.percentualTotalTributosSN;
    }

    public IndicadorTotalTributos getIndicadorTotalTributos() {
        return indicadorTotalTributos;
    }

    public BigDecimal getPercentualTotalTributosSN() {
        return percentualTotalTributosSN;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private IndicadorTotalTributos indicadorTotalTributos;
        private BigDecimal percentualTotalTributosSN;

        public Builder indicadorTotalTributos(IndicadorTotalTributos indicadorTotalTributos) {
            this.indicadorTotalTributos = indicadorTotalTributos;
            return this;
        }

        public Builder percentualTotalTributosSN(BigDecimal percentualTotalTributosSN) {
            this.percentualTotalTributosSN = percentualTotalTributosSN;
            return this;
        }

        public TotalTributos build() {
            return new TotalTributos(this);
        }
    }
}
