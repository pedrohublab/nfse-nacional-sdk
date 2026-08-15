package com.hubpedro.nfsenacional.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload para registro de evento em POST /nfse/{chaveAcesso}/eventos.
 */
public record SefinEventoRequestDTO(
        @JsonProperty("pedidoRegistroEventoXmlGZipB64") String pedidoRegistroEventoXmlGZipB64
) {
}
