package com.hubpedro.nfsenacional.homologacao;

import com.hubpedro.nfsenacional.NFSeNacionalClient;
import com.hubpedro.nfsenacional.domain.DPSTest;
import com.hubpedro.nfsenacional.domain.model.DPS;
import com.hubpedro.nfsenacional.model.RetornoEmissaoDps;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Teste de integração real contra o Ambiente de Dados Nacional (SEFIN Nacional - Produção Restrita / Homologação).
 * Executado na pipeline do GitLab CI quando as variáveis de ambiente com o Certificado A1 de Homologação estiverem configuradas.
 */
@DisplayName("Teste de Fumaça em Homologação Real (SEFIN Nacional - Produção Restrita)")
public class HomologacaoSmokeTest {

    private static final Logger log = LoggerFactory.getLogger(HomologacaoSmokeTest.class);

    private static final String ENV_CERT_B64 = "HOMOLOGACAO_CERT_PFX_BASE64";
    private static final String ENV_CERT_PATH = "HOMOLOGACAO_CERT_PATH";
    private static final String ENV_CERT_PASS = "HOMOLOGACAO_CERT_PASSWORD";

    @Test
    @DisplayName("Deve conectar e validar integração real no ambiente de Homologação da SEFIN Nacional")
    public void testarConexaoHomologacaoReal() throws Exception {
        byte[] certBytes = obterBytesCertificado();
        String certPassword = obterSenhaCertificado();

        // Pula o teste caso as credenciais reais de homologação não estejam presentes no ambiente
        assumeTrue(certBytes != null && certPassword != null,
                "Credenciais de homologação não encontradas (Defina HOMOLOGACAO_CERT_PFX_BASE64 e HOMOLOGACAO_CERT_PASSWORD)");

        log.info("Iniciando teste de integração real no ambiente de Homologação SEFIN...");

        try (NFSeNacionalClient client = NFSeNacionalClient.builder()
                .withCertificate(certBytes, certPassword)
                .withEnvironment(NFSeNacionalClient.Environment.HOMOLOGACAO)
                .withTimeouts(15000, 30000)
                .build()) {

            String cnpj = client.getCnpjCertificado();
            log.info("Certificado de Homologação carregado com sucesso. CNPJ do titular: {}", cnpj);
            assertThat(cnpj).isNotEmpty();

            // Gerar XML de teste
            DPS dps = DPSTest.criarDpsValida();
            String xmlAssinado = client.gerarXmlAssinado(dps);
            assertThat(xmlAssinado).contains("<Signature");

            log.info("XML da DPS gerado e assinado com sucesso. Chave DPS: {}", dps.getChaveDPS().valor());

            // Envio para Produção Restrita (SEFIN Homologação)
            RetornoEmissaoDps retorno = client.emitir(dps);
            log.info("Resposta da SEFIN Nacional Homologação: HTTP {}, Sucesso={}, Mensagem={}",
                    retorno.httpStatus(), retorno.sucesso(), retorno.mensagemErro());

            assertThat(retorno.httpStatus()).isGreaterThan(0);
        }
    }

    private static byte[] obterBytesCertificado() {
        String base64 = System.getenv(ENV_CERT_B64);
        if (base64 == null) {
            base64 = System.getProperty(ENV_CERT_B64);
        }
        if (base64 != null && !base64.isBlank()) {
            return Base64.getDecoder().decode(base64.trim());
        }

        String pathStr = System.getenv(ENV_CERT_PATH);
        if (pathStr == null) {
            pathStr = System.getProperty(ENV_CERT_PATH);
        }
        if (pathStr != null && !pathStr.isBlank()) {
            Path path = Path.of(pathStr);
            if (Files.exists(path)) {
                try {
                    return Files.readAllBytes(path);
                } catch (Exception e) {
                    log.warn("Falha ao ler arquivo de certificado em {}: {}", pathStr, e.getMessage());
                }
            }
        }
        return null;
    }

    private static String obterSenhaCertificado() {
        String pass = System.getenv(ENV_CERT_PASS);
        if (pass == null) {
            pass = System.getProperty(ENV_CERT_PASS);
        }
        return pass;
    }
}
