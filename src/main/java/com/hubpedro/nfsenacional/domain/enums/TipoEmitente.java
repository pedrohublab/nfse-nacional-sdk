package com.hubpedro.nfsenacional.domain.enums;

/**
 * Tipo de emitente da DPS.
 */
public enum TipoEmitente {

    PRESTADOR("1", "Prestador de serviço"),
    TOMADOR("2", "Tomador de serviço"),
    INTERMEDIARIO("3", "Intermediário");

    private final String codigo;
    private final String descricao;

    TipoEmitente(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoEmitente fromCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código do tipo de emitente não pode ser vazio");
        }
        for (TipoEmitente valor : values()) {
            if (valor.codigo.equals(codigo.trim())) {
                return valor;
            }
        }
        throw new IllegalArgumentException("Tipo de emitente inválido: " + codigo);
    }
}
