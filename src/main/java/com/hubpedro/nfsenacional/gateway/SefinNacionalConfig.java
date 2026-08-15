package com.hubpedro.nfsenacional.gateway;

import java.util.Objects;

/**
 * Configuração imutável para conexão com a SEFIN Nacional / ADN.
 */
public record SefinNacionalConfig(
        String baseUrl,
        int connectTimeoutMs,
        int readTimeoutMs
) {

    public static final String URL_PRODUCAO = "https://sefin.nfse.gov.br/SefinNacional";
    public static final String URL_HOMOLOGACAO = "https://sefin.producaorestrita.nfse.gov.br/SefinNacional";
    public static final int DEFAULT_CONNECT_TIMEOUT_MS = 15_000;
    public static final int DEFAULT_READ_TIMEOUT_MS = 30_000;

    public SefinNacionalConfig {
        baseUrl = normalizeBaseUrl(baseUrl);
    }

    public static SefinNacionalConfig producao() {
        return new SefinNacionalConfig(URL_PRODUCAO, DEFAULT_CONNECT_TIMEOUT_MS, DEFAULT_READ_TIMEOUT_MS);
    }

    public static SefinNacionalConfig homologacao() {
        return new SefinNacionalConfig(URL_HOMOLOGACAO, DEFAULT_CONNECT_TIMEOUT_MS, DEFAULT_READ_TIMEOUT_MS);
    }

    public static SefinNacionalConfig custom(String baseUrl, int connectTimeoutMs, int readTimeoutMs) {
        return new SefinNacionalConfig(baseUrl, connectTimeoutMs, readTimeoutMs);
    }

    private static String normalizeBaseUrl(String url) {
        if (url == null || url.isBlank()) {
            return URL_HOMOLOGACAO;
        }
        String trimmed = url.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }
}
