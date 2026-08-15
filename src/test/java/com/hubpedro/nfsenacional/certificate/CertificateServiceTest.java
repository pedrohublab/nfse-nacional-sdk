package com.hubpedro.nfsenacional.certificate;

import com.hubpedro.nfsenacional.util.TestCertificateGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes do CertificateService e Criptografia A1")
public class CertificateServiceTest {

    @Test
    @DisplayName("Deve carregar certificado A1 em memória e extrair CNPJ")
    void deveCarregarCertificadoEExtrairCnpj() throws Exception {
        String cnpjEsperado = "11222333000181";
        String senha = "senhaTeste123";
        byte[] pfxBytes = TestCertificateGenerator.generateTestPkcs12Bytes(cnpjEsperado, senha);

        try (CertificateService certService = CertificateService.fromBytes(pfxBytes, senha)) {
            assertThat(certService.getPrivateKey()).isNotNull();
            assertThat(certService.getCertificate()).isNotNull();
            assertThat(certService.getSslContext()).isNotNull();

            String cnpjExtraido = certService.getCnpjFromCertificate();
            assertThat(cnpjExtraido).isEqualTo(cnpjEsperado);
        }
    }

    @Test
    @DisplayName("Deve validar vigência com sucesso para certificado válido")
    void deveValidarVigencia() throws Exception {
        byte[] pfxBytes = TestCertificateGenerator.generateTestPkcs12Bytes("11222333000181", "123");
        try (CertificateService certService = CertificateService.fromBytes(pfxBytes, "123")) {
            assertThatCode(certService::validarVigencia).doesNotThrowAnyException();
        }
    }

    @Test
    @DisplayName("Deve limpar senha e bytes da memória após close()")
    void deveLimparMemoriaAoFechar() throws Exception {
        byte[] pfxBytes = TestCertificateGenerator.generateTestPkcs12Bytes("11222333000181", "123");
        CertificateMaterial material = CertificateMaterial.fromBytes(pfxBytes, "123");
        material.close();
        assertThat(material.getPasswordChars()).containsOnly(' ');
        assertThat(material.getCertificateBytes()).containsOnly((byte) 0);
    }
}
