package com.hubpedro.nfsenacional.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object que representa todos os valores da DPS.
 * Agrupa valores de serviço, descontos, deduções e tributação.
 * Imutável, construído via Builder, auto-validado.
 */
public final class Valores {

    private final ValoresServico valoresServico;
    private final BigDecimal valorDescontoIncondicionado;
    private final BigDecimal valorDescontoCondicionado;
    private final Deducao deducao;
    private final Tributacao tributacao;

    private Valores(Builder builder) {
        this.valoresServico = Objects.requireNonNull(builder.valoresServico, "Valores do serviço são obrigatórios");
        this.valorDescontoIncondicionado = builder.valorDescontoIncondicionado;
        this.valorDescontoCondicionado = builder.valorDescontoCondicionado;
        this.deducao = builder.deducao;
        this.tributacao = Objects.requireNonNull(builder.tributacao, "Tributação é obrigatória");
    }

    public ValoresServico getValoresServico() {
        return valoresServico;
    }

    public BigDecimal getValorDescontoIncondicionado() {
        return valorDescontoIncondicionado;
    }

    public BigDecimal getValorDescontoCondicionado() {
        return valorDescontoCondicionado;
    }

    public Deducao getDeducao() {
        return deducao;
    }

    public Tributacao getTributacao() {
        return tributacao;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ValoresServico valoresServico;
        private BigDecimal valorDescontoIncondicionado;
        private BigDecimal valorDescontoCondicionado;
        private Deducao deducao;
        private Tributacao tributacao;

        public Builder valoresServico(ValoresServico valoresServico) {
            this.valoresServico = valoresServico;
            return this;
        }

        public Builder valorDescontoIncondicionado(BigDecimal valorDescontoIncondicionado) {
            this.valorDescontoIncondicionado = valorDescontoIncondicionado;
            return this;
        }

        public Builder valorDescontoCondicionado(BigDecimal valorDescontoCondicionado) {
            this.valorDescontoCondicionado = valorDescontoCondicionado;
            return this;
        }

        public Builder deducao(Deducao deducao) {
            this.deducao = deducao;
            return this;
        }

        public Builder tributacao(Tributacao tributacao) {
            this.tributacao = tributacao;
            return this;
        }

        public Valores build() {
            return new Valores(this);
        }
    }
}
