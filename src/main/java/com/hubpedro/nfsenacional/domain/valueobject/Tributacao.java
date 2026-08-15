package com.hubpedro.nfsenacional.domain.valueobject;

import java.util.Objects;

/**
 * Value Object que representa a tributação completa da DPS.
 * Agrupa tributação municipal, federal e total de tributos.
 * Imutável, construído via Builder, auto-validado.
 */
public final class Tributacao {

    private final TributacaoMunicipal tributacaoMunicipal;
    private final TributacaoFederal tributacaoFederal;
    private final TotalTributos totalTributos;

    private Tributacao(Builder builder) {
        this.tributacaoMunicipal = Objects.requireNonNull(builder.tributacaoMunicipal,
                "Tributação municipal é obrigatória");
        this.tributacaoFederal = builder.tributacaoFederal;
        this.totalTributos = builder.totalTributos;
    }

    public TributacaoMunicipal getTributacaoMunicipal() {
        return tributacaoMunicipal;
    }

    public TributacaoFederal getTributacaoFederal() {
        return tributacaoFederal;
    }

    public TotalTributos getTotalTributos() {
        return totalTributos;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TributacaoMunicipal tributacaoMunicipal;
        private TributacaoFederal tributacaoFederal;
        private TotalTributos totalTributos;

        public Builder tributacaoMunicipal(TributacaoMunicipal tributacaoMunicipal) {
            this.tributacaoMunicipal = tributacaoMunicipal;
            return this;
        }

        public Builder tributacaoFederal(TributacaoFederal tributacaoFederal) {
            this.tributacaoFederal = tributacaoFederal;
            return this;
        }

        public Builder totalTributos(TotalTributos totalTributos) {
            this.totalTributos = totalTributos;
            return this;
        }

        public Tributacao build() {
            return new Tributacao(this);
        }
    }
}
