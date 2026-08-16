package com.hubpedro.nfsenacional.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Utilitário de persistência estruturada em disco para arquivos fiscais (XML e PDF).
 * Salva automaticamente organizando em subpastas: {baseDir}/ano/mes/dia/{nomeArquivo}.
 */
public final class XmlStorageHelper {

    private static final DateTimeFormatter YEAR_FMT = DateTimeFormatter.ofPattern("yyyy");
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("MM");
    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("dd");

    private XmlStorageHelper() {
    }

    /**
     * Salva o conteúdo de um XML em disco na estrutura de pastas baseDir/ano/mes/dia/nomeArquivo.xml.
     *
     * @param baseDir diretório raiz de armazenamento
     * @param nomeArquivoSemExtensao nome base do arquivo (geralmente chave de acesso ou chave DPS)
     * @param conteudoXml string com o XML
     * @param dataReferencia data para particionamento (se nula, usa LocalDate.now())
     * @return Path do arquivo salvo
     * @throws IOException se houver erro ao criar diretórios ou gravar arquivo
     */
    public static Path salvarXml(Path baseDir, String nomeArquivoSemExtensao, String conteudoXml, LocalDate dataReferencia) throws IOException {
        Objects.requireNonNull(baseDir, "Diretório base não pode ser nulo");
        Objects.requireNonNull(nomeArquivoSemExtensao, "Nome do arquivo não pode ser nulo");
        Objects.requireNonNull(conteudoXml, "Conteúdo XML não pode ser nulo");

        LocalDate data = dataReferencia != null ? dataReferencia : LocalDate.now();
        Path pastaDestino = resolverDiretorioPorData(baseDir, data);
        Files.createDirectories(pastaDestino);

        String nomeFinal = nomeArquivoSemExtensao.endsWith(".xml") ? nomeArquivoSemExtensao : nomeArquivoSemExtensao + ".xml";
        Path arquivoFinal = pastaDestino.resolve(nomeFinal);
        Files.writeString(arquivoFinal, conteudoXml, StandardCharsets.UTF_8);

        return arquivoFinal;
    }

    /**
     * Salva bytes binários (ex: PDF do DANFSE) na estrutura baseDir/ano/mes/dia/nomeArquivo.pdf.
     */
    public static Path salvarBinario(Path baseDir, String nomeArquivoSemExtensao, byte[] bytes, String extensao, LocalDate dataReferencia) throws IOException {
        Objects.requireNonNull(baseDir, "Diretório base não pode ser nulo");
        Objects.requireNonNull(nomeArquivoSemExtensao, "Nome do arquivo não pode ser nulo");
        Objects.requireNonNull(bytes, "Bytes não podem ser nulos");

        LocalDate data = dataReferencia != null ? dataReferencia : LocalDate.now();
        Path pastaDestino = resolverDiretorioPorData(baseDir, data);
        Files.createDirectories(pastaDestino);

        String ext = extensao != null && !extensao.startsWith(".") ? "." + extensao : (extensao != null ? extensao : ".bin");
        String nomeFinal = nomeArquivoSemExtensao.endsWith(ext) ? nomeArquivoSemExtensao : nomeArquivoSemExtensao + ext;
        Path arquivoFinal = pastaDestino.resolve(nomeFinal);
        Files.write(arquivoFinal, bytes);

        return arquivoFinal;
    }

    private static Path resolverDiretorioPorData(Path baseDir, LocalDate data) {
        String ano = data.format(YEAR_FMT);
        String mes = data.format(MONTH_FMT);
        String dia = data.format(DAY_FMT);
        return baseDir.resolve(ano).resolve(mes).resolve(dia);
    }
}
