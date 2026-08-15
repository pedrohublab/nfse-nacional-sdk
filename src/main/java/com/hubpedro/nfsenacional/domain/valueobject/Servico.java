package com.hubpedro.nfsenacional.domain.valueobject;

import java.util.Objects;

/**
 * Value Object que representa o serviço prestado.
 * Imutável, construído via Builder, auto-validado.
 */
public final class Servico {

    private final CodigoServico codigoServico;
    private final LocalPrestacao localPrestacao;

    private Servico(Builder builder) {
        this.codigoServico = Objects.requireNonNull(builder.codigoServico, "Código do serviço é obrigatório");
        this.localPrestacao = Objects.requireNonNull(builder.localPrestacao, "Local de prestação é obrigatório");
    }

    public CodigoServico getCodigoServico() {
        return codigoServico;
    }

    public LocalPrestacao getLocalPrestacao() {
        return localPrestacao;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CodigoServico codigoServico;
        private LocalPrestacao localPrestacao;

        public Builder codigoServico(CodigoServico codigoServico) {
            this.codigoServico = codigoServico;
            return this;
        }

        public Builder localPrestacao(LocalPrestacao localPrestacao) {
            this.localPrestacao = localPrestacao;
            return this;
        }

        public Servico build() {
            return new Servico(this);
        }
    }
}
