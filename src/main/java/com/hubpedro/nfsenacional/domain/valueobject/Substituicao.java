package com.hubpedro.nfsenacional.domain.valueobject;

import java.util.Objects;

/**
 * Value Object que representa uma substituição de DPS/NFS-e.
 * Imutável, construído via Builder, auto-validado.
 */
public final class Substituicao {

    private final String chaveSubstituida;
    private final String codigoMotivo;
    private final String descricaoMotivo;

    private Substituicao(Builder builder) {
        this.chaveSubstituida = Objects.requireNonNull(builder.chaveSubstituida,
                "Chave substituída é obrigatória");
        this.codigoMotivo = Objects.requireNonNull(builder.codigoMotivo, "Código do motivo é obrigatório");
        this.descricaoMotivo = builder.descricaoMotivo;
        validar();
    }

    private void validar() {
        if (chaveSubstituida.length() > 50) {
            throw new IllegalArgumentException("Chave substituída deve ter no máximo 50 caracteres");
        }
        if (chaveSubstituida.isBlank()) {
            throw new IllegalArgumentException("Chave substituída não pode ser vazia");
        }
        if (codigoMotivo.isBlank()) {
            throw new IllegalArgumentException("Código do motivo não pode ser vazio");
        }
    }

    public String getChaveSubstituida() {
        return chaveSubstituida;
    }

    public String getCodigoMotivo() {
        return codigoMotivo;
    }

    public String getDescricaoMotivo() {
        return descricaoMotivo;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String chaveSubstituida;
        private String codigoMotivo;
        private String descricaoMotivo;

        public Builder chaveSubstituida(String chaveSubstituida) {
            this.chaveSubstituida = chaveSubstituida;
            return this;
        }

        public Builder codigoMotivo(String codigoMotivo) {
            this.codigoMotivo = codigoMotivo;
            return this;
        }

        public Builder descricaoMotivo(String descricaoMotivo) {
            this.descricaoMotivo = descricaoMotivo;
            return this;
        }

        public Substituicao build() {
            return new Substituicao(this);
        }
    }
}
