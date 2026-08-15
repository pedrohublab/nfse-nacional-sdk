package com.hubpedro.nfsenacional.domain.valueobject;

import com.hubpedro.nfsenacional.domain.enums.OpcaoSimplesNacional;
import com.hubpedro.nfsenacional.domain.enums.RegimeApuracaoSN;
import com.hubpedro.nfsenacional.domain.enums.RegimeEspecialTributacao;

import java.util.Objects;

/**
 * Value Object que representa o regime tributário do prestador.
 * Imutável, construído via Builder, auto-validado.
 */
public final class RegimeTributario {

    private final OpcaoSimplesNacional opcaoSimplesNacional;
    private final RegimeApuracaoSN regimeApuracaoSN;
    private final RegimeEspecialTributacao regimeEspecialTributacao;

    private RegimeTributario(Builder builder) {
        this.opcaoSimplesNacional = Objects.requireNonNull(builder.opcaoSimplesNacional,
                "Opção do Simples Nacional é obrigatória");
        this.regimeApuracaoSN = builder.regimeApuracaoSN;
        this.regimeEspecialTributacao = builder.regimeEspecialTributacao;
        validar();
    }

    private void validar() {
        if (opcaoSimplesNacional == OpcaoSimplesNacional.NAO && regimeApuracaoSN != null) {
            throw new IllegalArgumentException(
                    "Regime de apuração do Simples Nacional não pode ser informado quando não optante");
        }
    }

    public OpcaoSimplesNacional getOpcaoSimplesNacional() {
        return opcaoSimplesNacional;
    }

    public RegimeApuracaoSN getRegimeApuracaoSN() {
        return regimeApuracaoSN;
    }

    public RegimeEspecialTributacao getRegimeEspecialTributacao() {
        return regimeEspecialTributacao;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private OpcaoSimplesNacional opcaoSimplesNacional;
        private RegimeApuracaoSN regimeApuracaoSN;
        private RegimeEspecialTributacao regimeEspecialTributacao;

        public Builder opcaoSimplesNacional(OpcaoSimplesNacional opcaoSimplesNacional) {
            this.opcaoSimplesNacional = opcaoSimplesNacional;
            return this;
        }

        public Builder regimeApuracaoSN(RegimeApuracaoSN regimeApuracaoSN) {
            this.regimeApuracaoSN = regimeApuracaoSN;
            return this;
        }

        public Builder regimeEspecialTributacao(RegimeEspecialTributacao regimeEspecialTributacao) {
            this.regimeEspecialTributacao = regimeEspecialTributacao;
            return this;
        }

        public RegimeTributario build() {
            return new RegimeTributario(this);
        }
    }
}
