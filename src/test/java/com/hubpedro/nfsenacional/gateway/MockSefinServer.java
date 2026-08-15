package com.hubpedro.nfsenacional.gateway;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Servidor HTTP embutido para mockar a API SEFIN Nacional nos testes automatizados sem dependência externa.
 */
public final class MockSefinServer implements AutoCloseable {

    private final HttpServer server;
    private int responseStatusDps = 200;
    private String responseBodyDps = "{\"chaveAcesso\":\"35503082608112223330001810000000000000000000000001\",\"chaveDPS\":\"35503082608112223330001810000000000000000000000001\",\"numeroDPS\":101,\"protocolo\":\"PROT123456\",\"dhProcessamento\":\"2026-08-15T10:00:05-03:00\",\"codigoStatus\":\"100\",\"descricaoStatus\":\"DPS Autorizada com Sucesso\"}";
    private int delayMs = 0;

    public MockSefinServer() {
        try {
            this.server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            configurarRotas();
            this.server.start();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao iniciar MockSefinServer", e);
        }
    }

    private void configurarRotas() {
        // POST /dps e GET /dps/{chave} e HEAD /dps/{chave}
        server.createContext("/dps", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if (delayMs > 0) {
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                String method = exchange.getRequestMethod();
                if ("HEAD".equalsIgnoreCase(method)) {
                    exchange.sendResponseHeaders(200, -1);
                    exchange.close();
                    return;
                }

                byte[] bytes = responseBodyDps.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(responseStatusDps, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
                exchange.close();
            }
        });

        // GET /nfse/{chave}, GET /nfse/{chave}/danfse, POST /nfse/{chave}/eventos
        server.createContext("/nfse", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String path = exchange.getRequestURI().getPath();
                String method = exchange.getRequestMethod();

                if (path.endsWith("/danfse")) {
                    byte[] pdfMock = "%PDF-1.4 Mock PDF Content".getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "application/pdf");
                    exchange.sendResponseHeaders(200, pdfMock.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(pdfMock);
                    }
                    exchange.close();
                    return;
                }

                if (path.endsWith("/eventos") && "POST".equalsIgnoreCase(method)) {
                    String evtJson = "{\"idEvento\":\"EVT123\",\"chaveAcesso\":\"35503082608112223330001810000000000000000000000001\",\"tipoEvento\":\"e101101\",\"codigoStatus\":\"101\",\"descricaoStatus\":\"Cancelamento Homologado\"}";
                    byte[] bytes = evtJson.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, bytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(bytes);
                    }
                    exchange.close();
                    return;
                }

                // Consulta NFS-e normal
                String nfseJson = "{\"chaveAcesso\":\"35503082608112223330001810000000000000000000000001\",\"numeroNFSe\":1,\"serie\":\"1\",\"dhEmissao\":\"2026-08-15T10:00:00-03:00\",\"protocolo\":\"PROT123456\",\"codigoStatus\":\"100\",\"descricaoStatus\":\"NFS-e Autorizada\"}";
                byte[] bytes = nfseJson.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
                exchange.close();
            }
        });
    }

    public String getBaseUrl() {
        return "http://127.0.0.1:" + server.getAddress().getPort();
    }

    public void setResponseDps(int status, String jsonBody) {
        this.responseStatusDps = status;
        this.responseBodyDps = jsonBody;
    }

    public void setDelayMs(int delayMs) {
        this.delayMs = delayMs;
    }

    @Override
    public void close() {
        server.stop(0);
    }
}
