package com.hubpedro.nfsenacional.storage;

import com.hubpedro.nfsenacional.domain.enums.DpsEmissionStatus;
import com.hubpedro.nfsenacional.model.DanfseDocument;
import com.hubpedro.nfsenacional.model.RetornoEmissaoDps;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes do Utilitário de Armazenamento Estruturado (ano/mes/dia)")
public class XmlStorageHelperTest {

    private Path tempDir;

    @org.junit.jupiter.api.BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("nfse_storage_test_");
    }

    @Test
    @DisplayName("Deve salvar XML particionado por ano/mes/dia com sucesso")
    void deveSalvarXmlParticionadoPorData() throws IOException {
        LocalDate data = LocalDate.of(2026, 8, 15);
        String chave = "35503082608112223330001810000000000000000000000001";
        String xml = "<DPS xmlns=\"http://www.sped.fazenda.gov.br/nfse\"><infDPS></infDPS></DPS>";

        Path arquivo = XmlStorageHelper.salvarXml(tempDir, chave, xml, data);

        assertThat(arquivo).isNotNull();
        assertThat(Files.exists(arquivo)).isTrue();
        assertThat(arquivo.toString()).contains("2026/08/15");
        assertThat(arquivo.getFileName().toString()).isEqualTo(chave + ".xml");
        assertThat(Files.readString(arquivo, StandardCharsets.UTF_8)).isEqualTo(xml);
    }

    @Test
    @DisplayName("Deve salvar XML diretamente a partir de RetornoEmissaoDps")
    void deveSalvarXmlDiretoDoRetornoEmissao() throws IOException {
        LocalDate data = LocalDate.of(2026, 8, 15);
        String chaveAcesso = "35503082608112223330001810000000000000000000000001";
        String xmlAutorizado = "<NFSe><infNFSe>AUTORIZADO</infNFSe></NFSe>";

        RetornoEmissaoDps retorno = new RetornoEmissaoDps(
                true, "DPS123", chaveAcesso, "PROT123", DpsEmissionStatus.AUTHORIZED,
                "<DPS></DPS>", xmlAutorizado, null, null, null, 200, 50
        );

        Path arquivo = retorno.salvarXml(tempDir, data);

        assertThat(arquivo).isNotNull();
        assertThat(Files.exists(arquivo)).isTrue();
        assertThat(arquivo.toString()).contains("2026/08/15");
        assertThat(Files.readString(arquivo, StandardCharsets.UTF_8)).isEqualTo(xmlAutorizado);
    }

    @Test
    @DisplayName("Deve salvar PDF do DANFSE na estrutura particionada")
    void deveSalvarPdfDanfseParticionado() throws IOException {
        String chaveAcesso = "35503082608112223330001810000000000000000000000001";
        byte[] pdfFake = "%PDF-1.4 Fake PDF Content".getBytes(StandardCharsets.UTF_8);

        DanfseDocument doc = new DanfseDocument(chaveAcesso, pdfFake);
        Path arquivo = doc.salvarEmDiretorio(tempDir);

        assertThat(arquivo).isNotNull();
        assertThat(Files.exists(arquivo)).isTrue();
        assertThat(arquivo.getFileName().toString()).isEqualTo(chaveAcesso + ".pdf");
        assertThat(Files.readAllBytes(arquivo)).isEqualTo(pdfFake);
    }
}
