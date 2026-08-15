package com.hubpedro.nfsenacional.domain.valueobject;

import com.hubpedro.nfsenacional.domain.enums.TributacaoISSQN;

import java.util.Objects;

/**
 * Value Object que representa a tributação municipal (ISSQN).
 * Imutável, construído via Builder, auto-validado.
 */
public final class TributacaoMunicipal {

    private final TributacaoISSQN tributacaoISSQN;
    private final String codigoPaisResultado;
    private final BaseCalculo baseCalculo;

    private TributacaoMunicipal(Builder builder) {
        this.tributacaoISSQN = Objects.requireNonNull(builder.tributacaoISSQN,
                "Tributação ISSQN é obrigatória");
        this.codigoPaisResultado = builder.codigoPaisResultado;
        this.baseCalculo = builder.baseCalculo;
    }

    public TributacaoISSQN getTributacaoISSQN() {
        return tributacaoISSQN;
    }

    public String getCodigoPaisResultado() {
        return codigoPaisResultado;
    }

    public BaseCalculo getBaseCalculo() {
        return baseCalculo;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TributacaoISSQN tributacaoISSQN;
        private String codigoPaisResultado;
        private BaseCalculo baseCalculo;

        public Builder tributacaoISSQN(TributacaoISSQN tributacaoISSQN) {
            this.tributacaoISSQN = tributacaoISSQN;
            return this;
        }

        public Builder codigoPaisResultado(String codigoPaisResultado) {
            this.codigoPaisResultado = codigoPaisResultado;
            return this;
        }

        public Builder baseCalculo(BaseCalculo baseCalculo) {
            this.baseCalculo = baseCalculo;
            return this;
        }

        public TributacaoMunicipal build() {
            return new TributacaoMunicipal(this);
        }
    }
}
