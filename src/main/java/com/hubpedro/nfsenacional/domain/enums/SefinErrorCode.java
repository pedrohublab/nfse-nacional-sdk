package com.hubpedro.nfsenacional.domain.enums;

import java.util.Arrays;

/**
 * Códigos numéricos oficiais de resposta/status e rejeição da SEFIN Nacional.
 */
public enum SefinErrorCode {
    AUTORIZADO("100", "DPS Autorizada com sucesso", true),
    CANCELADO("101", "NFS-e Cancelada com sucesso", true),
    SUBSTITUIDO("102", "NFS-e Substituída com sucesso", true),
    DUPLICIDADE("204", "Duplicidade de DPS já emitida", false),
    ERRO_VALIDACAO_SCHEMA("501", "Erro na validação do schema XML", false),
    CERTIFICADO_INVALIDO("502", "Certificado digital inválido, expirado ou revogado", false),
    SERVICO_INDISPONIVEL("500", "Serviço SEFIN indisponível", false),
    NAO_ENCONTRADO("404", "Documento fiscal não encontrado", false),
    OUTRO_ERRO("999", "Erro genérico ou não catalogado", false);

    private final String codigo;
    private final String descricao;
    private final boolean sucesso;

    SefinErrorCode(String codigo, String descricao, boolean sucesso) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.sucesso = sucesso;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public static SefinErrorCode fromCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return OUTRO_ERRO;
        }
        return Arrays.stream(values())
                .filter(e -> e.codigo.equalsIgnoreCase(codigo.trim()))
                .findFirst()
                .orElse(OUTRO_ERRO);
    }
}
