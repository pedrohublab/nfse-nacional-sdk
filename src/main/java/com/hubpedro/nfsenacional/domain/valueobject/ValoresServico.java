package com.hubpedro.nfsenacional.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object que representa os valores do serviço.
 * Imutável, construído via Builder, auto-validado.
 */
public final class ValoresServico {

    private final BigDecimal valorRecebido;
    private final BigDecimal valorServico;

    private ValoresServico(Builder builder) {
        this.valorRecebido = builder.valorRecebido;
        this.valorServico = Objects.requireNonNull(builder.valorServico, "Valor do serviço é obrigatório");
        validar();
    }

    private void validar() {
        if (valorServico.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor do serviço não pode ser negativo");
        }
    }

    public BigDecimal getValorRecebido() {
        return valorRecebido;
    }

    public BigDecimal getValorServico() {
        return valorServico;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BigDecimal valorRecebido;
        private BigDecimal valorServico;

        public Builder valorRecebido(BigDecimal valorRecebido) {
            this.valorRecebido = valorRecebido;
            return this;
        }

        public Builder valorServico(BigDecimal valorServico) {
            this.valorServico = valorServico;
            return this;
        }

        public ValoresServico build() {
            return new ValoresServico(this);
        }
    }
}
