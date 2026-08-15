package com.hubpedro.nfsenacional.model;

/**
 * Resultado do registro de evento (Cancelamento, Substituição) na NFS-e.
 */
public record RetornoEventoNfse(
        boolean sucesso,
        String idEvento,
        String chaveAcesso,
        String tipoEvento,
        String protocolo,
        String codigoStatus,
        String descricaoStatus,
        String rawResponseBody,
        int httpStatus,
        long tempoRespostaMs
) {
}
