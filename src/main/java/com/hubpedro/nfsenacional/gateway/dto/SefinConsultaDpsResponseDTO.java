package com.hubpedro.nfsenacional.gateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Resposta da consulta de status da DPS via GET /dps/{chaveDPS}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SefinConsultaDpsResponseDTO(
        @JsonProperty("chaveDPS") String chaveDPS,
        @JsonProperty("chaveAcesso") String chaveAcesso,
        @JsonProperty("protocolo") String protocolo,
        @JsonProperty("dhProcessamento") String dhProcessamento,
        @JsonProperty("codigoStatus") String codigoStatus,
        @JsonProperty("descricaoStatus") String descricaoStatus,
        @JsonProperty("mensagens") List<SefinMensagemDTO> mensagens,
        @JsonProperty("nfseXmlGZipB64") String nfseXmlGZipB64
) {

    public boolean isProcessada() {
        return "100".equals(codigoStatus) || (chaveAcesso != null && !chaveAcesso.isBlank());
    }
}
