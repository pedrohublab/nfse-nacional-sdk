package com.hubpedro.nfsenacional.domain.valueobject;

import com.hubpedro.nfsenacional.domain.enums.TipoRetencaoISSQN;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object que representa a base de cálculo do ISSQN.
 * Imutável, construído via Builder, auto-validado.
 */
public final class BaseCalculo {

    private final BigDecimal valorBaseCalculo;
    private final BigDecimal aliquota;
    private final BigDecimal valorISS;
    private final TipoRetencaoISSQN tipoRetencao;

    private BaseCalculo(Builder builder) {
        this.valorBaseCalculo = Objects.requireNonNull(builder.valorBaseCalculo,
                "Valor da base de cálculo é obrigatório");
        this.aliquota = Objects.requireNonNull(builder.aliquota, "Alíquota é obrigatória");
        this.valorISS = Objects.requireNonNull(builder.valorISS, "Valor do ISS é obrigatório");
        this.tipoRetencao = Objects.requireNonNull(builder.tipoRetencao, "Tipo de retenção é obrigatório");
        validar();
    }

    private void validar() {
        if (valorBaseCalculo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor da base de cálculo não pode ser negativo");
        }
        if (aliquota.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Alíquota não pode ser negativa");
        }
        if (valorISS.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor do ISS não pode ser negativo");
        }
    }

    public BigDecimal getValorBaseCalculo() {
        return valorBaseCalculo;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public BigDecimal getValorISS() {
        return valorISS;
    }

    public TipoRetencaoISSQN getTipoRetencao() {
        return tipoRetencao;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BigDecimal valorBaseCalculo;
        private BigDecimal aliquota;
        private BigDecimal valorISS;
        private TipoRetencaoISSQN tipoRetencao;

        public Builder valorBaseCalculo(BigDecimal valorBaseCalculo) {
            this.valorBaseCalculo = valorBaseCalculo;
            return this;
        }

        public Builder aliquota(BigDecimal aliquota) {
            this.aliquota = aliquota;
            return this;
        }

        public Builder valorISS(BigDecimal valorISS) {
            this.valorISS = valorISS;
            return this;
        }

        public Builder tipoRetencao(TipoRetencaoISSQN tipoRetencao) {
            this.tipoRetencao = tipoRetencao;
            return this;
        }

        public BaseCalculo build() {
            return new BaseCalculo(this);
        }
    }
}
