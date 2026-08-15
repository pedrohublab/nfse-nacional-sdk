package com.hubpedro.nfsenacional.domain.valueobject;

import java.util.Objects;

/**
 * Value Object que representa o local de prestação de serviço.
 * Imutável, construído via Builder, auto-validado.
 */
public final class LocalPrestacao {

    private final String codigoLocalPrestacao;
    private final String codigoPaisPrestacao;

    private LocalPrestacao(Builder builder) {
        this.codigoLocalPrestacao = Objects.requireNonNull(builder.codigoLocalPrestacao,
                "Código do local de prestação é obrigatório");
        this.codigoPaisPrestacao = builder.codigoPaisPrestacao;
        validar();
    }

    private void validar() {
        if (!codigoLocalPrestacao.matches("\\d{7}")) {
            throw new IllegalArgumentException(
                    "Código do local de prestação deve ter 7 dígitos (IBGE): " + codigoLocalPrestacao);
        }
    }

    public String getCodigoLocalPrestacao() {
        return codigoLocalPrestacao;
    }

    public String getCodigoPaisPrestacao() {
        return codigoPaisPrestacao;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String codigoLocalPrestacao;
        private String codigoPaisPrestacao;

        public Builder codigoLocalPrestacao(String codigoLocalPrestacao) {
            this.codigoLocalPrestacao = codigoLocalPrestacao;
            return this;
        }

        public Builder codigoPaisPrestacao(String codigoPaisPrestacao) {
            this.codigoPaisPrestacao = codigoPaisPrestacao;
            return this;
        }

        public LocalPrestacao build() {
            return new LocalPrestacao(this);
        }
    }
}
