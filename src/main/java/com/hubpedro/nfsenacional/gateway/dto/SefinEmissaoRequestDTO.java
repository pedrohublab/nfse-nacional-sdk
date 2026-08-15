package com.hubpedro.nfsenacional.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload JSON enviado no POST /dps.
 */
public record SefinEmissaoRequestDTO(
        @JsonProperty("dpsXmlGZipB64") String dpsXmlGZipB64
) {
}
