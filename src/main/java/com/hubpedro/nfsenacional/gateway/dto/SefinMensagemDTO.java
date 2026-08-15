package com.hubpedro.nfsenacional.gateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa uma mensagem de retorno (alerta ou rejeição) da SEFIN Nacional.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SefinMensagemDTO(
        @JsonProperty("codigo") String codigo,
        @JsonProperty("descricao") String descricao,
        @JsonProperty("complemento") String complemento
) {
}
