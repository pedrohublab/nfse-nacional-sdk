package com.hubpedro.nfsenacional.domain.valueobject;

import java.util.Objects;

/**
 * Value Object que representa informações adicionais da DPS.
 * Imutável e auto-validado.
 */
public final class InformacaoAdicional {

    private final String informacaoAdicional;

    public InformacaoAdicional(String informacaoAdicional) {
        this.informacaoAdicional = Objects.requireNonNull(informacaoAdicional,
                "Informação adicional é obrigatória quando presente");
        validar();
    }

    private void validar() {
        if (informacaoAdicional.isBlank()) {
            throw new IllegalArgumentException("Informação adicional não pode ser vazia");
        }
        if (informacaoAdicional.length() > 2000) {
            throw new IllegalArgumentException("Informação adicional deve ter no máximo 2000 caracteres");
        }
    }

    public String getInformacaoAdicional() {
        return informacaoAdicional;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InformacaoAdicional that = (InformacaoAdicional) o;
        return Objects.equals(informacaoAdicional, that.informacaoAdicional);
    }

    @Override
    public int hashCode() {
        return Objects.hash(informacaoAdicional);
    }

    @Override
    public String toString() {
        return informacaoAdicional;
    }
}
