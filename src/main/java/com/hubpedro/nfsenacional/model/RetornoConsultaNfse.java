package com.hubpedro.nfsenacional.model;

/**
 * Resultado da consulta de NFS-e emitida por Chave de Acesso.
 */
public record RetornoConsultaNfse(
        boolean autorizada,
        boolean cancelada,
        String chaveAcesso,
        Long numeroNFSe,
        String serie,
        String protocolo,
        String codigoStatus,
        String descricaoStatus,
        String xmlAutorizado,
        byte[] danfsePdfBytes,
        String rawResponseBody,
        int httpStatus,
        long tempoRespostaMs
) {
}
