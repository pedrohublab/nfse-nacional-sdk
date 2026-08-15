package com.hubpedro.nfsenacional.domain.enums;

/**
 * Tipos de eventos fiscais da NFS-e Nacional.
 */
public enum TipoEventoNfse {

    CANCELAMENTO("e101101", "Cancelamento de NFS-e"),
    CANCELAMENTO_POR_SUBSTITUICAO("e101103", "Cancelamento por Substituição"),
    SOLICITACAO_CANCELAMENTO("e105102", "Solicitação de Cancelamento"),
    CONFIRMACAO_PRESTADOR("e101102", "Confirmação do Prestador"),
    CONFIRMACAO_TOMADOR("e105104", "Confirmação do Tomador"),
    REJEICAO_TOMADOR("e105105", "Rejeição do Tomador");

    private final String codigo;
    private final String descricao;

    TipoEventoNfse(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoEventoNfse fromCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código de evento não pode ser vazio");
        }
        for (TipoEventoNfse e : values()) {
            if (e.codigo.equalsIgnoreCase(codigo.trim())) {
                return e;
            }
        }
        throw new IllegalArgumentException("Tipo de evento inválido: " + codigo);
    }
}
