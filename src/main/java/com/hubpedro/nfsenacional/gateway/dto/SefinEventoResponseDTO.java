package com.hubpedro.nfsenacional.gateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Resposta do registro de evento retornado pela SEFIN Nacional.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SefinEventoResponseDTO(
        @JsonProperty("idEvento") String idEvento,
        @JsonProperty("chaveAcesso") String chaveAcesso,
        @JsonProperty("tipoEvento") String tipoEvento,
        @JsonProperty("nSeqEvento") Integer nSeqEvento,
        @JsonProperty("dhProcessamento") String dhProcessamento,
        @JsonProperty("codigoStatus") String codigoStatus,
        @JsonProperty("descricaoStatus") String descricaoStatus,
        @JsonProperty("mensagens") List<SefinMensagemDTO> mensagens
) {

    public boolean isSucesso() {
        return "101".equals(codigoStatus) || "102".equals(codigoStatus) || "100".equals(codigoStatus);
    }
}
