package com.hubpedro.nfsenacional.domain.valueobject;

import java.util.Objects;

/**
 * Value Object que representa o prestador de serviços.
 * Imutável, construído via Builder, auto-validado.
 */
public final class Prestador {

    private final CNPJ cnpj;
    private final String inscricaoMunicipal;
    private final String nomeRazaoSocial;
    private final Endereco endereco;
    private final String telefone;
    private final String email;
    private final RegimeTributario regimeTributario;

    private Prestador(Builder builder) {
        this.cnpj = Objects.requireNonNull(builder.cnpj, "CNPJ do prestador é obrigatório");
        this.inscricaoMunicipal = builder.inscricaoMunicipal;
        this.nomeRazaoSocial = Objects.requireNonNull(builder.nomeRazaoSocial,
                "Nome/Razão Social do prestador é obrigatório");
        this.endereco = builder.endereco;
        this.telefone = builder.telefone;
        this.email = builder.email;
        this.regimeTributario = Objects.requireNonNull(builder.regimeTributario,
                "Regime tributário do prestador é obrigatório");
        validar();
    }

    private void validar() {
        if (nomeRazaoSocial.isBlank()) {
            throw new IllegalArgumentException("Nome/Razão Social do prestador não pode ser vazio");
        }
        if (nomeRazaoSocial.length() > 150) {
            throw new IllegalArgumentException("Nome/Razão Social do prestador deve ter no máximo 150 caracteres");
        }
    }

    public CNPJ getCnpj() {
        return cnpj;
    }

    public String getInscricaoMunicipal() {
        return inscricaoMunicipal;
    }

    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public RegimeTributario getRegimeTributario() {
        return regimeTributario;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CNPJ cnpj;
        private String inscricaoMunicipal;
        private String nomeRazaoSocial;
        private Endereco endereco;
        private String telefone;
        private String email;
        private RegimeTributario regimeTributario;

        public Builder cnpj(CNPJ cnpj) {
            this.cnpj = cnpj;
            return this;
        }

        public Builder inscricaoMunicipal(String inscricaoMunicipal) {
            this.inscricaoMunicipal = inscricaoMunicipal;
            return this;
        }

        public Builder nomeRazaoSocial(String nomeRazaoSocial) {
            this.nomeRazaoSocial = nomeRazaoSocial;
            return this;
        }

        public Builder endereco(Endereco endereco) {
            this.endereco = endereco;
            return this;
        }

        public Builder telefone(String telefone) {
            this.telefone = telefone;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder regimeTributario(RegimeTributario regimeTributario) {
            this.regimeTributario = regimeTributario;
            return this;
        }

        public Prestador build() {
            return new Prestador(this);
        }
    }
}
