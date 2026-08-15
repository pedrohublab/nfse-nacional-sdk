package com.hubpedro.nfsenacional.gateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Resposta retornada pela SEFIN Nacional no envio síncrono da DPS.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SefinEmissaoResponseDTO(
        @JsonProperty("chaveAcesso") String chaveAcesso,
        @JsonProperty("chaveDPS") String chaveDPS,
        @JsonProperty("numeroDPS") Long numeroDPS,
        @JsonProperty("serie") String serie,
        @JsonProperty("protocolo") String protocolo,
        @JsonProperty("dhProcessamento") String dhProcessamento,
        @JsonProperty("codigoStatus") String codigoStatus,
        @JsonProperty("descricaoStatus") String descricaoStatus,
        @JsonProperty("mensagens") List<SefinMensagemDTO> mensagens,
        @JsonProperty("nfseXmlGZipB64") String nfseXmlGZipB64
) {

    public boolean isSucesso() {
        return "100".equals(codigoStatus) || (protocolo != null && !protocolo.isBlank());
    }

    public boolean isDuplicidade() {
        return "204".equals(codigoStatus);
    }
}
