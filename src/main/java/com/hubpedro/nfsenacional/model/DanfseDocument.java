package com.hubpedro.nfsenacional.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Objects;

/**
 * Representa o Documento Auxiliar da NFS-e (DANFSE em formato PDF).
 */
public record DanfseDocument(
        String chaveAcesso,
        byte[] pdfBytes
) {

    public DanfseDocument {
        Objects.requireNonNull(chaveAcesso, "Chave de acesso é obrigatória");
        Objects.requireNonNull(pdfBytes, "Bytes do PDF são obrigatórios");
    }

    public InputStream openStream() {
        return new ByteArrayInputStream(pdfBytes);
    }

    public String toBase64() {
        return Base64.getEncoder().encodeToString(pdfBytes);
    }

    public void salvarEmArquivo(Path destino) throws IOException {
        Objects.requireNonNull(destino, "Caminho de destino não pode ser nulo");
        Files.write(destino, pdfBytes);
    }

    /**
     * Salva o PDF em disco organizado automaticamente em subpastas ano/mes/dia/{chaveAcesso}.pdf.
     */
    public Path salvarEmDiretorio(Path baseDir) throws IOException {
        return com.hubpedro.nfsenacional.storage.XmlStorageHelper.salvarBinario(
                baseDir, chaveAcesso, pdfBytes, ".pdf", java.time.LocalDate.now()
        );
    }

    public int getTamanhoBytes() {
        return pdfBytes.length;
    }
}
