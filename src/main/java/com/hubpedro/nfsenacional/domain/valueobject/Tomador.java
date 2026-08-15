package com.hubpedro.nfsenacional.domain.valueobject;

import com.hubpedro.nfsenacional.domain.model.CpfCnpj;

/**
 * Value Object que representa o tomador de serviços.
 * Todos os campos são opcionais.
 * Imutável, construído via Builder, auto-validado.
 */
public final class Tomador {

    private final CpfCnpj cpfCnpj;
    private final String nomeRazaoSocial;
    private final Endereco endereco;
    private final String telefone;
    private final String email;

    private Tomador(Builder builder) {
        this.cpfCnpj = builder.cpfCnpj;
        this.nomeRazaoSocial = builder.nomeRazaoSocial;
        this.endereco = builder.endereco;
        this.telefone = builder.telefone;
        this.email = builder.email;
        validar();
    }

    private void validar() {
        if (nomeRazaoSocial != null && nomeRazaoSocial.length() > 150) {
            throw new IllegalArgumentException("Nome/Razão Social do tomador deve ter no máximo 150 caracteres");
        }
    }

    public CpfCnpj getCpfCnpj() {
        return cpfCnpj;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CpfCnpj cpfCnpj;
        private String nomeRazaoSocial;
        private Endereco endereco;
        private String telefone;
        private String email;

        public Builder cpfCnpj(CpfCnpj cpfCnpj) {
            this.cpfCnpj = cpfCnpj;
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

        public Tomador build() {
            return new Tomador(this);
        }
    }
}
