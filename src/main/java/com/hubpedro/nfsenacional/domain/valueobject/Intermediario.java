package com.hubpedro.nfsenacional.domain.valueobject;

import com.hubpedro.nfsenacional.domain.model.CpfCnpj;

import java.util.Objects;

/**
 * Value Object que representa o intermediário de serviços.
 * Imutável, construído via Builder, auto-validado.
 */
public final class Intermediario {

    private final CpfCnpj cpfCnpj;
    private final String nomeRazaoSocial;

    private Intermediario(Builder builder) {
        this.cpfCnpj = Objects.requireNonNull(builder.cpfCnpj, "CPF/CNPJ do intermediário é obrigatório");
        this.nomeRazaoSocial = Objects.requireNonNull(builder.nomeRazaoSocial,
                "Nome/Razão Social do intermediário é obrigatório");
        validar();
    }

    private void validar() {
        if (nomeRazaoSocial.isBlank()) {
            throw new IllegalArgumentException("Nome/Razão Social do intermediário não pode ser vazio");
        }
        if (nomeRazaoSocial.length() > 150) {
            throw new IllegalArgumentException(
                    "Nome/Razão Social do intermediário deve ter no máximo 150 caracteres");
        }
    }

    public CpfCnpj getCpfCnpj() {
        return cpfCnpj;
    }

    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CpfCnpj cpfCnpj;
        private String nomeRazaoSocial;

        public Builder cpfCnpj(CpfCnpj cpfCnpj) {
            this.cpfCnpj = cpfCnpj;
            return this;
        }

        public Builder nomeRazaoSocial(String nomeRazaoSocial) {
            this.nomeRazaoSocial = nomeRazaoSocial;
            return this;
        }

        public Intermediario build() {
            return new Intermediario(this);
        }
    }
}
