package com.hubpedro.nfsenacional.domain.valueobject;

import java.util.Objects;

/**
 * Value Object que representa o código de serviço na DPS.
 * Imutável, construído via Builder, auto-validado.
 */
public final class CodigoServico {

    private final String codigoTribNacional;
    private final String codigoTribMunicipal;
    private final String cnae;
    private final String descricaoServico;

    private CodigoServico(Builder builder) {
        this.codigoTribNacional = Objects.requireNonNull(builder.codigoTribNacional,
                "Código tributação nacional é obrigatório");
        this.codigoTribMunicipal = builder.codigoTribMunicipal;
        this.cnae = builder.cnae;
        this.descricaoServico = Objects.requireNonNull(builder.descricaoServico,
                "Descrição do serviço é obrigatória");
        validar();
    }

    private void validar() {
        if (codigoTribNacional.isBlank()) {
            throw new IllegalArgumentException("Código tributação nacional não pode ser vazio");
        }
        if (descricaoServico.isBlank()) {
            throw new IllegalArgumentException("Descrição do serviço não pode ser vazia");
        }
        if (descricaoServico.length() > 2000) {
            throw new IllegalArgumentException("Descrição do serviço deve ter no máximo 2000 caracteres");
        }
    }

    public String getCodigoTribNacional() {
        return codigoTribNacional;
    }

    public String getCodigoTribMunicipal() {
        return codigoTribMunicipal;
    }

    public String getCnae() {
        return cnae;
    }

    public String getDescricaoServico() {
        return descricaoServico;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String codigoTribNacional;
        private String codigoTribMunicipal;
        private String cnae;
        private String descricaoServico;

        public Builder codigoTribNacional(String codigoTribNacional) {
            this.codigoTribNacional = codigoTribNacional;
            return this;
        }

        public Builder codigoTribMunicipal(String codigoTribMunicipal) {
            this.codigoTribMunicipal = codigoTribMunicipal;
            return this;
        }

        public Builder cnae(String cnae) {
            this.cnae = cnae;
            return this;
        }

        public Builder descricaoServico(String descricaoServico) {
            this.descricaoServico = descricaoServico;
            return this;
        }

        public CodigoServico build() {
            return new CodigoServico(this);
        }
    }
}
