package com.hubpedro.nfsenacional.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hubpedro.nfsenacional.domain.exception.SefinApiException;
import com.hubpedro.nfsenacional.domain.exception.SefinTimeoutException;
import com.hubpedro.nfsenacional.gateway.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

/**
 * Gateway HTTP REST mTLS para comunicação de baixo nível com os endpoints da SEFIN Nacional / ADN.
 */
public class SefinNacionalGateway {

    private static final Logger log = LoggerFactory.getLogger(SefinNacionalGateway.class);
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String CONTENT_TYPE_PDF = "application/pdf";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    private final SefinNacionalConfig config;
    private final HttpClient httpClient;

    public SefinNacionalGateway(SefinNacionalConfig config, SSLContext sslContext) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
                .sslContext(sslContext)
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofMillis(config.connectTimeoutMs()))
                .build();
    }

    public SefinNacionalGateway(SefinNacionalConfig config, HttpClient httpClient) {
        this.config = config;
        this.httpClient = httpClient;
    }

    /**
     * POST /dps: Envio síncrono da DPS.
     */
    public SefinResponse<SefinEmissaoResponseDTO> enviarDps(String encodedDpsPayload) {
        String url = config.baseUrl() + "/dps";
        String jsonBody = "{\"dpsXmlGZipB64\":\"" + encodedDpsPayload + "\"}";

        log.debug("Enviando DPS para SEFIN: {}", url);
        return executePost(url, jsonBody, SefinEmissaoResponseDTO.class);
    }

    /**
     * GET /dps/{chaveDPS}: Consulta de status e processamento da DPS.
     */
    public SefinResponse<SefinConsultaDpsResponseDTO> consultarDps(String chaveDPS) {
        String url = config.baseUrl() + "/dps/" + chaveDPS;
        log.debug("Consultando DPS na SEFIN: {}", url);
        return executeGet(url, SefinConsultaDpsResponseDTO.class);
    }

    /**
     * HEAD /dps/{chaveDPS}: Checagem rápida de processamento da DPS sem transferir payload.
     */
    public boolean verificarDpsProcessada(String chaveDPS) {
        String url = config.baseUrl() + "/dps/" + chaveDPS;
        log.debug("Verificando HEAD DPS na SEFIN: {}", url);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMillis(config.readTimeoutMs()))
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 200 || response.statusCode() == 204;
        } catch (HttpTimeoutException e) {
            throw new SefinTimeoutException("Timeout na checagem HEAD da DPS: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Requisição interrompida", e);
        } catch (Exception e) {
            throw new SefinApiException("Erro na checagem HEAD da DPS: " + e.getMessage());
        }
    }

    /**
     * GET /nfse/{chaveAcesso}: Consulta da NFS-e emitida.
     */
    public SefinResponse<SefinConsultaNfseResponseDTO> consultarNfse(String chaveAcesso) {
        String url = config.baseUrl() + "/nfse/" + chaveAcesso;
        log.debug("Consultando NFS-e na SEFIN: {}", url);
        return executeGet(url, SefinConsultaNfseResponseDTO.class);
    }

    /**
     * GET /nfse/{chaveAcesso}/danfse: Download do Documento Auxiliar (DANFSE em PDF).
     */
    public SefinResponse<byte[]> downloadDanfse(String chaveAcesso) {
        String url = config.baseUrl() + "/nfse/" + chaveAcesso + "/danfse";
        log.debug("Baixando DANFSE (PDF) na SEFIN: {}", url);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMillis(config.readTimeoutMs()))
                    .header("Accept", CONTENT_TYPE_PDF + ", " + CONTENT_TYPE_JSON)
                    .GET()
                    .build();

            long start = System.currentTimeMillis();
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            long elapsed = System.currentTimeMillis() - start;

            log.info("Resposta Download DANFSE: status={}, elapsed={}ms", response.statusCode(), elapsed);
            return new SefinResponse<>(response.statusCode(), null, response.body(), elapsed);
        } catch (HttpTimeoutException e) {
            throw new SefinTimeoutException("Timeout no download do DANFSE: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Download de DANFSE interrompido", e);
        } catch (Exception e) {
            throw new SefinApiException("Erro no download de DANFSE: " + e.getMessage(), null, 0, null, e);
        }
    }

    /**
     * POST /nfse/{chaveAcesso}/eventos: Registro de eventos (Cancelamento / Substituição).
     */
    public SefinResponse<SefinEventoResponseDTO> registrarEvento(String chaveAcesso, String encodedEventoPayload) {
        String url = config.baseUrl() + "/nfse/" + chaveAcesso + "/eventos";
        String jsonBody = "{\"pedidoRegistroEventoXmlGZipB64\":\"" + encodedEventoPayload + "\"}";

        log.debug("Registrando evento na SEFIN: {}", url);
        return executePost(url, jsonBody, SefinEventoResponseDTO.class);
    }

    private <T> SefinResponse<T> executePost(String url, String jsonBody, Class<T> responseClass) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMillis(config.readTimeoutMs()))
                    .header("Content-Type", CONTENT_TYPE_JSON)
                    .header("Accept", CONTENT_TYPE_JSON)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            long start = System.currentTimeMillis();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            long elapsed = System.currentTimeMillis() - start;

            log.info("Resposta SEFIN POST {}: status={}, elapsed={}ms", url, response.statusCode(), elapsed);

            T parsed = parseResponseBody(response.body(), responseClass);
            return new SefinResponse<>(response.statusCode(), response.body(), parsed, elapsed);
        } catch (HttpTimeoutException e) {
            log.warn("Timeout na requisição POST para SEFIN: {}", e.getMessage());
            throw new SefinTimeoutException("Timeout na comunicação com a SEFIN Nacional: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Requisição SEFIN interrompida", e);
        } catch (Exception e) {
            if (e.getCause() instanceof HttpTimeoutException || (e.getMessage() != null && e.getMessage().toLowerCase().contains("timeout"))) {
                throw new SefinTimeoutException("Timeout na comunicação com a SEFIN Nacional: " + e.getMessage(), e);
            }
            throw new SefinApiException("Erro na comunicação com a SEFIN Nacional: " + e.getMessage(), null, 0, null, e);
        }
    }

    private <T> SefinResponse<T> executeGet(String url, Class<T> responseClass) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMillis(config.readTimeoutMs()))
                    .header("Accept", CONTENT_TYPE_JSON)
                    .GET()
                    .build();

            long start = System.currentTimeMillis();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            long elapsed = System.currentTimeMillis() - start;

            log.info("Resposta SEFIN GET {}: status={}, elapsed={}ms", url, response.statusCode(), elapsed);

            T parsed = parseResponseBody(response.body(), responseClass);
            return new SefinResponse<>(response.statusCode(), response.body(), parsed, elapsed);
        } catch (HttpTimeoutException e) {
            log.warn("Timeout na requisição GET para SEFIN: {}", e.getMessage());
            throw new SefinTimeoutException("Timeout na comunicação com a SEFIN Nacional: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Requisição SEFIN interrompida", e);
        } catch (Exception e) {
            if (e.getCause() instanceof HttpTimeoutException || (e.getMessage() != null && e.getMessage().toLowerCase().contains("timeout"))) {
                throw new SefinTimeoutException("Timeout na comunicação com a SEFIN Nacional: " + e.getMessage(), e);
            }
            throw new SefinApiException("Erro na comunicação com a SEFIN Nacional: " + e.getMessage(), null, 0, null, e);
        }
    }

    private static <T> T parseResponseBody(String body, Class<T> clazz) {
        if (body == null || body.isBlank()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(body, clazz);
        } catch (Exception e) {
            log.debug("Não foi possível desserializar resposta JSON da SEFIN para {}: {}", clazz.getSimpleName(), e.getMessage());
            return null;
        }
    }

    public SefinNacionalConfig getConfig() {
        return config;
    }

    /**
     * Resposta tipada do Gateway SEFIN contendo HTTP status, raw body, objeto DTO parseado e tempo de execução.
     */
    public record SefinResponse<T>(int httpStatus, String rawBody, T parsed, long elapsedMs) {
    }
}
