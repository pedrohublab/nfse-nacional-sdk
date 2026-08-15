package com.hubpedro.nfsenacional.domain.valueobject;

import java.math.BigDecimal;

/**
 * Value Object que representa deduções aplicáveis ao serviço.
 * Imutável, construído via Builder, auto-validado.
 */
public final class Deducao {

    private final BigDecimal percentualDeducao;
    private final BigDecimal valorDeducao;

    private Deducao(Builder builder) {
        this.percentualDeducao = builder.percentualDeducao;
        this.valorDeducao = builder.valorDeducao;
        validar();
    }

    private void validar() {
        if (percentualDeducao != null) {
            if (percentualDeducao.compareTo(BigDecimal.ZERO) < 0
                || percentualDeducao.compareTo(new BigDecimal("100")) > 0) {
                throw new IllegalArgumentException("Percentual de dedução deve estar entre 0 e 100");
            }
        }
        if (valorDeducao != null && valorDeducao.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor da dedução não pode ser negativo");
        }
    }

    public BigDecimal getPercentualDeducao() {
        return percentualDeducao;
    }

    public BigDecimal getValorDeducao() {
        return valorDeducao;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BigDecimal percentualDeducao;
        private BigDecimal valorDeducao;

        public Builder percentualDeducao(BigDecimal percentualDeducao) {
            this.percentualDeducao = percentualDeducao;
            return this;
        }

        public Builder valorDeducao(BigDecimal valorDeducao) {
            this.valorDeducao = valorDeducao;
            return this;
        }

        public Deducao build() {
            return new Deducao(this);
        }
    }
}
