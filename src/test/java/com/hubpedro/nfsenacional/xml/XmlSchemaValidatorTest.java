package com.hubpedro.nfsenacional.xml;

import com.hubpedro.nfsenacional.certificate.CertificateService;
import com.hubpedro.nfsenacional.domain.DPSTest;
import com.hubpedro.nfsenacional.domain.exception.XmlValidationException;
import com.hubpedro.nfsenacional.domain.model.DPS;
import com.hubpedro.nfsenacional.domain.model.PedidoCancelamento;
import com.hubpedro.nfsenacional.util.TestCertificateGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes do Validador XSD Offline Local")
public class XmlSchemaValidatorTest {

    @Test
    @DisplayName("Deve validar com sucesso XML da DPS contra DPS_v1.00.xsd")
    void deveValidarXmlDpsValido() throws Exception {
        byte[] pfxBytes = TestCertificateGenerator.generateTestPkcs12Bytes("11222333000181", "123");
        CertificateService certService = CertificateService.fromBytes(pfxBytes, "123");

        DPS dps = DPSTest.criarDpsValida();
        DpsXmlGenerator generator = new DpsXmlGenerator();
        String xml = generator.gerar(dps);
        String xmlAssinado = certService.signXmlString(xml);

        XmlSchemaValidator validator = new XmlSchemaValidator();
        assertThatCode(() -> validator.validarDps(xmlAssinado)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve rejeitar XML da DPS com tag ou estrutura inválida")
    void deveRejeitarXmlDpsInvalido() {
        String xmlInvalido = "<DPS xmlns=\"http://www.sped.fazenda.gov.br/nfse\"><infDPS Id=\"DPS123\"><tagInexistente/></infDPS></DPS>";
        XmlSchemaValidator validator = new XmlSchemaValidator();

        assertThatThrownBy(() -> validator.validarDps(xmlInvalido))
                .isInstanceOf(XmlValidationException.class);
    }

    @Test
    @DisplayName("Deve validar com sucesso XML de Cancelamento contra pedRegEvento_v1.00.xsd")
    void deveValidarXmlEventoValido() throws Exception {
        byte[] pfxBytes = TestCertificateGenerator.generateTestPkcs12Bytes("11222333000181", "123");
        CertificateService certService = CertificateService.fromBytes(pfxBytes, "123");

        PedidoCancelamento pedido = PedidoCancelamento.builder()
                .chaveAcessoNfse("35503082608112223330001810000000000000000000000001")
                .cnpjOuCpfAutor("11222333000181")
                .codigoMotivo("1")
                .descricaoMotivo("Erro no preenchimento")
                .build();

        EventoXmlGenerator generator = new EventoXmlGenerator();
        String xml = generator.gerarXmlCancelamento(pedido);
        String xmlAssinado = certService.signXmlString(xml);

        XmlSchemaValidator validator = new XmlSchemaValidator();
        assertThatCode(() -> validator.validarEvento(xmlAssinado)).doesNotThrowAnyException();
    }
}
