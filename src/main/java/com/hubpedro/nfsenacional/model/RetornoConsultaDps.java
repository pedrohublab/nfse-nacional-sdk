package com.hubpedro.nfsenacional.model;

/**
 * Resultado da consulta de processamento de DPS.
 */
public record RetornoConsultaDps(
        boolean processada,
        String chaveDPS,
        String chaveAcesso,
        String protocolo,
        String codigoStatus,
        String descricaoStatus,
        String xmlAutorizado,
        String rawResponseBody,
        int httpStatus,
        long tempoRespostaMs
) {
}
