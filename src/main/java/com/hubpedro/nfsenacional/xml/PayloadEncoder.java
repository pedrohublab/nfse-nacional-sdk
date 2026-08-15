package com.hubpedro.nfsenacional.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Utilitário de alta performance para compressão e codificação de payloads exigidos pela SEFIN Nacional.
 * Fluxo de envio: XML UTF-8 -> GZip -> Base64.
 * Fluxo de retorno: Base64 -> GZip decompress -> XML / Dados.
 */
public final class PayloadEncoder {

    private PayloadEncoder() {
    }

    /**
     * Codifica XML para envio à SEFIN Nacional: UTF-8 -> GZip -> Base64.
     *
     * @param xml o XML como String
     * @return String Base64 do conteúdo comprimido com GZip
     */
    public static String encodeForSefin(String xml) {
        if (xml == null || xml.isBlank()) {
            throw new IllegalArgumentException("XML não pode ser nulo ou vazio para codificação");
        }
        byte[] utf8Bytes = xml.getBytes(StandardCharsets.UTF_8);
        byte[] gzipped = gzip(utf8Bytes);
        return Base64.getEncoder().encodeToString(gzipped);
    }

    /**
     * Decodifica retorno Base64 GZip da SEFIN para String UTF-8.
     *
     * @param base64Gzip o conteúdo em Base64 recebido da SEFIN
     * @return String descompactada
     */
    public static String decodeFromSefin(String base64Gzip) {
        byte[] decompressed = decodeBytesFromSefin(base64Gzip);
        return new String(decompressed, StandardCharsets.UTF_8);
    }

    /**
     * Decodifica retorno Base64 GZip da SEFIN para array de bytes descompactados.
     *
     * @param base64Gzip o conteúdo em Base64 recebido da SEFIN
     * @return bytes descompactados (ex: PDF DANFSE ou XML bruto)
     */
    public static byte[] decodeBytesFromSefin(String base64Gzip) {
        if (base64Gzip == null || base64Gzip.isBlank()) {
            throw new IllegalArgumentException("Conteúdo Base64 não pode ser nulo ou vazio para decodificação");
        }
        try {
            byte[] gzippedBytes = Base64.getDecoder().decode(base64Gzip.trim());
            return gunzip(gzippedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao decodificar payload GZip Base64 da SEFIN: " + e.getMessage(), e);
        }
    }

    /**
     * Comprime bytes com GZip.
     */
    public static byte[] gzip(byte[] data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
             GZIPOutputStream gzos = new GZIPOutputStream(baos)) {
            gzos.write(data);
            gzos.finish();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao comprimir payload com GZip", e);
        }
    }

    /**
     * Descomprime bytes com GZip.
     */
    public static byte[] gunzip(byte[] compressed) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
             GZIPInputStream gzis = new GZIPInputStream(bais);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = gzis.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao descomprimir payload GZip", e);
        }
    }
}
