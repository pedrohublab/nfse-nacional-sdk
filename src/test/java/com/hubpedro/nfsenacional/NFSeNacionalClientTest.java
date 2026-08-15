package com.hubpedro.nfsenacional;

import com.hubpedro.nfsenacional.domain.DPSTest;
import com.hubpedro.nfsenacional.domain.model.DPS;
import com.hubpedro.nfsenacional.domain.model.PedidoCancelamento;
import com.hubpedro.nfsenacional.domain.model.PedidoSubstituicao;
import com.hubpedro.nfsenacional.gateway.MockSefinServer;
import com.hubpedro.nfsenacional.model.*;
import com.hubpedro.nfsenacional.util.TestCertificateGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes da Fachada Fluente NFSeNacionalClient")
class NFSeNacionalClientTest {

    private MockSefinServer mockServer;
    private byte[] certBytes;
    private final String certPassword = "senhaTeste123";

    @BeforeEach
    void setUp() throws Exception {
        mockServer = new MockSefinServer();
        certBytes = TestCertificateGenerator.generateTestPkcs12Bytes("11222333000181", certPassword);
    }

    @AfterEach
    void tearDown() {
        mockServer.close();
    }

    @Test
    @DisplayName("Deve emitir DPS síncrona com sucesso usando builder fluente")
    void deveEmitirDpsComSucesso() throws Exception {
        try (NFSeNacionalClient client = NFSeNacionalClient.builder()
                .withCertificate(certBytes, certPassword)
                .withBaseUrl(mockServer.getBaseUrl())
                .withEnvironment(NFSeNacionalClient.Environment.HOMOLOGACAO)
                .build()) {

            DPS dps = DPSTest.criarDpsValida();
            RetornoEmissaoDps retorno = client.emitir(dps);

            assertThat(retorno.sucesso()).isTrue();
            assertThat(retorno.chaveDPS()).isEqualTo(dps.getChaveDPS().valor());
            assertThat(retorno.chaveAcesso()).isNotEmpty();
            assertThat(retorno.protocolo()).isEqualTo("PROT123456");
            assertThat(retorno.xmlAssinado()).contains("<Signature");
        }
    }

    @Test
    @DisplayName("Deve gerar XML assinado e validado offline sem envio à rede")
    void deveGerarXmlAssinadoOffline() throws Exception {
        try (NFSeNacionalClient client = NFSeNacionalClient.builder()
                .withCertificate(certBytes, certPassword)
                .withBaseUrl(mockServer.getBaseUrl())
                .build()) {

            DPS dps = DPSTest.criarDpsValida();
            String xmlAssinado = client.gerarXmlAssinado(dps);

            assertThat(xmlAssinado).startsWith("<?xml version=\"1.0\"");
            assertThat(xmlAssinado).contains("<DPS xmlns=\"http://www.sped.fazenda.gov.br/nfse\">");
            assertThat(xmlAssinado).contains("<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">");
        }
    }

    @Test
    @DisplayName("Deve consultar status da DPS previamente enviada")
    void deveConsultarStatusDps() throws Exception {
        try (NFSeNacionalClient client = NFSeNacionalClient.builder()
                .withCertificate(certBytes, certPassword)
                .withBaseUrl(mockServer.getBaseUrl())
                .build()) {

            RetornoConsultaDps retorno = client.consultarDps("35503082608112223330001810000000000000000000000001");
            assertThat(retorno.processada()).isTrue();
            assertThat(retorno.chaveDPS()).isEqualTo("35503082608112223330001810000000000000000000000001");
        }
    }

    @Test
    @DisplayName("Deve checar DPS processada via HEAD ultrarrápido")
    void deveChecarHeadDps() throws Exception {
        try (NFSeNacionalClient client = NFSeNacionalClient.builder()
                .withCertificate(certBytes, certPassword)
                .withBaseUrl(mockServer.getBaseUrl())
                .build()) {

            boolean processada = client.verificarDpsProcessada("35503082608112223330001810000000000000000000000001");
            assertThat(processada).isTrue();
        }
    }

    @Test
    @DisplayName("Deve consultar NFS-e por Chave de Acesso")
    void deveConsultarNfse() throws Exception {
        try (NFSeNacionalClient client = NFSeNacionalClient.builder()
                .withCertificate(certBytes, certPassword)
                .withBaseUrl(mockServer.getBaseUrl())
                .build()) {

            RetornoConsultaNfse retorno = client.consultarNfse("35503082608112223330001810000000000000000000000001");
            assertThat(retorno.autorizada()).isTrue();
            assertThat(retorno.numeroNFSe()).isEqualTo(1L);
        }
    }

    @Test
    @DisplayName("Deve baixar PDF do DANFSE")
    void deveBaixarDanfse() throws Exception {
        try (NFSeNacionalClient client = NFSeNacionalClient.builder()
                .withCertificate(certBytes, certPassword)
                .withBaseUrl(mockServer.getBaseUrl())
                .build()) {

            DanfseDocument doc = client.downloadDanfse("35503082608112223330001810000000000000000000000001");
            assertThat(doc).isNotNull();
            assertThat(doc.getTamanhoBytes()).isGreaterThan(0);
            assertThat(doc.toBase64()).isNotEmpty();
        }
    }

    @Test
    @DisplayName("Deve cancelar NFS-e com evento e101101")
    void deveCancelarNfse() throws Exception {
        try (NFSeNacionalClient client = NFSeNacionalClient.builder()
                .withCertificate(certBytes, certPassword)
                .withBaseUrl(mockServer.getBaseUrl())
                .build()) {

            PedidoCancelamento pedido = PedidoCancelamento.builder()
                    .chaveAcessoNfse("35503082608112223330001810000000000000000000000001")
                    .cnpjOuCpfAutor("11222333000181")
                    .codigoMotivo("1")
                    .descricaoMotivo("Erro no calculo de valores")
                    .build();

            RetornoEventoNfse retorno = client.cancelarNfse(pedido);
            assertThat(retorno.sucesso()).isTrue();
            assertThat(retorno.tipoEvento()).isEqualTo("e101101");
        }
    }

    @Test
    @DisplayName("Deve substituir NFS-e com evento e101103")
    void deveSubstituirNfse() throws Exception {
        try (NFSeNacionalClient client = NFSeNacionalClient.builder()
                .withCertificate(certBytes, certPassword)
                .withBaseUrl(mockServer.getBaseUrl())
                .build()) {

            PedidoSubstituicao pedido = PedidoSubstituicao.builder()
                    .chaveAcessoNfse("35503082608112223330001810000000000000000000000001")
                    .cnpjOuCpfAutor("11222333000181")
                    .codigoMotivo("1")
                    .descricaoMotivo("Alteracao de tomador")
                    .chaveDpsSubstituta("35503082608112223330001810000000000000000000000002")
                    .build();

            RetornoEventoNfse retorno = client.substituirNfse(pedido);
            assertThat(retorno.sucesso()).isTrue();
            assertThat(retorno.tipoEvento()).isEqualTo("e101103");
        }
    }

    @Test
    @DisplayName("Deve tratar resposta de rejeição da SEFIN")
    void deveTratarRejeicao() throws Exception {
        mockServer.setResponseDps(422, "{\"codigoStatus\":\"E123\",\"descricaoStatus\":\"Aliquota invalida para o servico informado\"}");

        try (NFSeNacionalClient client = NFSeNacionalClient.builder()
                .withCertificate(certBytes, certPassword)
                .withBaseUrl(mockServer.getBaseUrl())
                .build()) {

            DPS dps = DPSTest.criarDpsValida();
            RetornoEmissaoDps retorno = client.emitir(dps);

            assertThat(retorno.sucesso()).isFalse();
            assertThat(retorno.mensagemErro()).contains("Aliquota invalida");
        }
    }
}
