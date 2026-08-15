package com.hubpedro.nfsenacional.domain.valueobject;

import java.util.Objects;

/**
 * Value Object que representa um endereço no padrão nacional.
 * Imutável, construído via Builder, auto-validado.
 */
public final class Endereco {

    private final String logradouro;
    private final String numero;
    private final String complemento;
    private final String bairro;
    private final String codigoMunicipio;
    private final String uf;
    private final String cep;
    private final String codigoPais;

    private Endereco(Builder builder) {
        this.logradouro = Objects.requireNonNull(builder.logradouro, "Logradouro é obrigatório");
        this.numero = Objects.requireNonNull(builder.numero, "Número é obrigatório");
        this.complemento = builder.complemento;
        this.bairro = Objects.requireNonNull(builder.bairro, "Bairro é obrigatório");
        this.codigoMunicipio = Objects.requireNonNull(builder.codigoMunicipio, "Código do município é obrigatório");
        this.uf = Objects.requireNonNull(builder.uf, "UF é obrigatória");
        this.cep = Objects.requireNonNull(builder.cep, "CEP é obrigatório");
        this.codigoPais = builder.codigoPais;
        validar();
    }

    private void validar() {
        if (logradouro.length() > 150) {
            throw new IllegalArgumentException("Logradouro deve ter no máximo 150 caracteres");
        }
        if (numero.length() > 10) {
            throw new IllegalArgumentException("Número deve ter no máximo 10 caracteres");
        }
        if (bairro.length() > 60) {
            throw new IllegalArgumentException("Bairro deve ter no máximo 60 caracteres");
        }
        if (!codigoMunicipio.matches("\\d{7}")) {
            throw new IllegalArgumentException("Código do município deve ter 7 dígitos: " + codigoMunicipio);
        }
        if (!uf.matches("[A-Z]{2}")) {
            throw new IllegalArgumentException("UF deve ter 2 letras maiúsculas: " + uf);
        }
        if (!cep.matches("\\d{8}")) {
            throw new IllegalArgumentException("CEP deve ter 8 dígitos: " + cep);
        }
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public String getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public String getUf() {
        return uf;
    }

    public String getCep() {
        return cep;
    }

    public String getCodigoPais() {
        return codigoPais;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String logradouro;
        private String numero;
        private String complemento;
        private String bairro;
        private String codigoMunicipio;
        private String uf;
        private String cep;
        private String codigoPais;

        public Builder logradouro(String logradouro) {
            this.logradouro = logradouro;
            return this;
        }

        public Builder numero(String numero) {
            this.numero = numero;
            return this;
        }

        public Builder complemento(String complemento) {
            this.complemento = complemento;
            return this;
        }

        public Builder bairro(String bairro) {
            this.bairro = bairro;
            return this;
        }

        public Builder codigoMunicipio(String codigoMunicipio) {
            this.codigoMunicipio = codigoMunicipio;
            return this;
        }

        public Builder uf(String uf) {
            this.uf = uf;
            return this;
        }

        public Builder cep(String cep) {
            this.cep = cep;
            return this;
        }

        public Builder codigoPais(String codigoPais) {
            this.codigoPais = codigoPais;
            return this;
        }

        public Endereco build() {
            return new Endereco(this);
        }
    }
}
