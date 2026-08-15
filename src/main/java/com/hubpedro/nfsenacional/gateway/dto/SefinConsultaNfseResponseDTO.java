package com.hubpedro.nfsenacional.gateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Resposta da consulta de NFS-e emitida via GET /nfse/{chaveAcesso}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SefinConsultaNfseResponseDTO(
        @JsonProperty("chaveAcesso") String chaveAcesso,
        @JsonProperty("numeroNFSe") Long numeroNFSe,
        @JsonProperty("serie") String serie,
        @JsonProperty("dhEmissao") String dhEmissao,
        @JsonProperty("protocolo") String protocolo,
        @JsonProperty("codigoStatus") String codigoStatus,
        @JsonProperty("descricaoStatus") String descricaoStatus,
        @JsonProperty("mensagens") List<SefinMensagemDTO> mensagens,
        @JsonProperty("nfseXmlGZipB64") String nfseXmlGZipB64,
        @JsonProperty("danfsePdfGZipB64") String danfsePdfGZipB64
) {

    public boolean isAutorizada() {
        return "100".equals(codigoStatus) || (nfseXmlGZipB64 != null && !nfseXmlGZipB64.isBlank());
    }

    public boolean isCancelada() {
        return "101".equals(codigoStatus);
    }
}
