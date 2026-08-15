package com.hubpedro.nfsenacional.domain.enums;

/**
 * Tipo de Retenção do ISSQN.
 */
public enum TipoRetencaoISSQN {

    NAO_RETIDO("1", "Não retido"),
    RETIDO_PELO_INTERMEDIARIO("2", "Retido pelo intermediário"),
    RETIDO_PELO_TOMADOR("3", "Retido pelo tomador");

    private final String codigo;
    private final String descricao;

    TipoRetencaoISSQN(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoRetencaoISSQN fromCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código do tipo de retenção não pode ser vazio");
        }
        for (TipoRetencaoISSQN valor : values()) {
            if (valor.codigo.equals(codigo.trim())) {
                return valor;
            }
        }
        throw new IllegalArgumentException("Tipo de retenção ISSQN inválido: " + codigo);
    }
}
