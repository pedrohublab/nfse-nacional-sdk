package com.hubpedro.nfsenacional.homologacao;

import com.hubpedro.nfsenacional.NFSeNacionalClient;
import com.hubpedro.nfsenacional.domain.DPSTest;
import com.hubpedro.nfsenacional.domain.model.DPS;
import com.hubpedro.nfsenacional.model.RetornoEmissaoDps;
import com.hubpedro.nfsenacional.util.TestCertificateGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@DisplayName("Teste de Emissão em Homologação com Certificado Fictício")
public class TesteEmissaoHomologacaoFicticioTest {

    private static final Logger log = LoggerFactory.getLogger(TesteEmissaoHomologacaoFicticioTest.class);

    @Test
    @DisplayName("Tentar emitir nota em Homologação com Certificado Fictício para verificar conexão e recusa")
    public void testarConexaoERecusaHomologacao() {
        String cnpj = "11222333000181";
        String senha = "senhaFicticia123";

        System.out.println("===============================================================================");
        System.out.println("  1. GERANDO CERTIFICADO DIGITAL A1 FICTÍCIO (AUTOASSINADO)");
        System.out.println("===============================================================================");

        byte[] certBytes;
        try {
            certBytes = TestCertificateGenerator.generateTestPkcs12Bytes(cnpj, senha);
            Path tempCert = Path.of("target/certificado_ficticio_homologacao.pfx");
            Files.createDirectories(tempCert.getParent());
            Files.write(tempCert, certBytes);
            System.out.println("✓ Certificado fictício gerado com sucesso: " + tempCert.toAbsolutePath());
            System.out.println("  - CNPJ: " + cnpj);
            System.out.println("  - Senha: " + senha);
            System.out.println("  - Tamanho: " + certBytes.length + " bytes");
        } catch (Exception e) {
            System.err.println("✗ Falha ao gerar certificado fictício: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        System.out.println("\n===============================================================================");
        System.out.println("  2. INICIANDO CLIENTE NFSE NACIONAL NO AMBIENTE DE HOMOLOGAÇÃO");
        System.out.println("  (Endpoint: https://sefin.producaorestrita.nfse.gov.br/SefinNacional)");
        System.out.println("===============================================================================");

        try (NFSeNacionalClient client = NFSeNacionalClient.builder()
                .withCertificate(certBytes, senha)
                .withEnvironment(NFSeNacionalClient.Environment.HOMOLOGACAO)
                .withTimeouts(10000, 15000)
                .build()) {

            System.out.println("✓ Cliente inicializado com mTLS e certificado configurado.");
            System.out.println("  - CNPJ extraído do certificado: " + client.getCnpjCertificado());

            System.out.println("\n===============================================================================");
            System.out.println("  3. CONSTRUINDO E ASSINANDO DPS (DECLARAÇÃO DE PRESTAÇÃO DE SERVIÇO)");
            System.out.println("===============================================================================");

            DPS dps = DPSTest.criarDpsValida();
            System.out.println("✓ DPS criada:");
            System.out.println("  - Chave DPS: " + dps.getChaveDPS().valor());
            System.out.println("  - Prestador: " + dps.getPrestador().getCnpj().getNumero());
            System.out.println("  - Valor Serviço: R$ " + dps.getValores().getValoresServico().getValorServico());

            String xmlAssinado = client.gerarXmlAssinado(dps);
            System.out.println("✓ XML gerado e assinado digitalmente com chave privada RSA:");
            System.out.println("  - Tamanho do XML: " + xmlAssinado.length() + " caracteres");
            System.out.println("  - Contém tag <Signature>: " + xmlAssinado.contains("<Signature"));

            System.out.println("\n===============================================================================");
            System.out.println("  4. ENVIANDO REQUISIÇÃO POST /dps PARA A SEFIN NACIONAL (HOMOLOGAÇÃO)");
            System.out.println("===============================================================================");

            try {
                RetornoEmissaoDps retorno = client.emitir(dps);
                System.out.println("✓ Resposta recebida da SEFIN Nacional:");
                System.out.println("  - HTTP Status: " + retorno.httpStatus());
                System.out.println("  - Sucesso: " + retorno.sucesso());
                System.out.println("  - Chave de Acesso: " + retorno.chaveAcesso());
                System.out.println("  - Mensagem/Erro: " + retorno.mensagemErro());
                System.out.println("  - Resposta Bruta: " + retorno.rawResponseBody());
            } catch (Exception e) {
                System.out.println("ℹ Exceção/Recusa capturada durante a transmissão com a SEFIN:");
                System.out.println("  - Tipo da Exceção: " + e.getClass().getName());
                System.out.println("  - Mensagem: " + e.getMessage());
                if (e.getCause() != null) {
                    System.out.println("  - Causa Raiz: " + e.getCause().getClass().getName() + " -> " + e.getCause().getMessage());
                }
                System.out.println("\n[DETALHES DO STACKTRACE]:");
                e.printStackTrace(System.out);
            }

        } catch (Exception e) {
            System.err.println("✗ Erro na inicialização do cliente ou certificado: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n===============================================================================");
        System.out.println("  FIM DO TESTE DE CONEXÃO E RECUSA");
        System.out.println("===============================================================================");
    }

    public static void main(String[] args) {
        new TesteEmissaoHomologacaoFicticioTest().testarConexaoERecusaHomologacao();
    }
}
