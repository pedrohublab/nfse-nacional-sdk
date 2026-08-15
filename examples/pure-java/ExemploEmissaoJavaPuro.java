package com.hubpedro.nfsenacional.examples;

import com.hubpedro.nfsenacional.NFSeNacionalClient;
import com.hubpedro.nfsenacional.domain.enums.*;
import com.hubpedro.nfsenacional.domain.model.CpfCnpj;
import com.hubpedro.nfsenacional.domain.model.DPS;
import com.hubpedro.nfsenacional.domain.valueobject.*;
import com.hubpedro.nfsenacional.model.DanfseDocument;
import com.hubpedro.nfsenacional.model.RetornoEmissaoDps;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Exemplo completo de emissão de NFS-e Nacional em Java SE puro (sem frameworks).
 */
public class ExemploEmissaoJavaPuro {

    public static void main(String[] args) throws Exception {
        // 1. Ler o certificado A1 (PKCS#12) em memória
        byte[] pfxBytes = Files.readAllBytes(Path.of("/caminho/para/seu_certificado.pfx"));
        String senhaCertificado = "sua_senha_aqui";

        // 2. Construir o cliente apontando para Homologação ou Produção
        try (NFSeNacionalClient client = NFSeNacionalClient.builder()
                .withCertificate(pfxBytes, senhaCertificado)
                .withEnvironment(NFSeNacionalClient.Environment.HOMOLOGACAO)
                .withTimeouts(15000, 30000)
                .build()) {

            // 3. Montar a DPS (Declaração de Prestação de Serviços)
            DPS dps = DPS.builder()
                    .serie("1")
                    .numeroDPS(1001L)
                    .tipoAmbiente(TipoAmbiente.HOMOLOGACAO)
                    .dataHoraEmissao(OffsetDateTime.now())
                    .dataCompetencia(LocalDate.now())
                    .tipoEmitente(TipoEmitente.PRESTADOR)
                    .codigoLocalEmissao("3550308") // São Paulo/SP (Código IBGE 7 dígitos)
                    .prestador(Prestador.builder()
                            .cnpj(new CNPJ("11222333000181"))
                            .nomeRazaoSocial("MINHA EMPRESA PRESTADORA LTDA")
                            .inscricaoMunicipal("12345678")
                            .endereco(Endereco.builder()
                                    .logradouro("Avenida Paulista")
                                    .numero("1000")
                                    .bairro("Bela Vista")
                                    .codigoMunicipio("3550308")
                                    .uf("SP")
                                    .cep("01310100")
                                    .build())
                            .regimeTributario(RegimeTributario.builder()
                                    .opcaoSimplesNacional(OpcaoSimplesNacional.SIM)
                                    .regimeApuracaoSN(RegimeApuracaoSN.SIMPLES_NACIONAL_RECEITA_BRUTA_ATE_180K)
                                    .build())
                            .build())
                    .tomador(Tomador.builder()
                            .cpfCnpj(CpfCnpj.of("12345678909"))
                            .nomeRazaoSocial("CLIENTE TOMADOR DO SERVICO")
                            .build())
                    .servico(Servico.builder()
                            .codigoServico(CodigoServico.builder()
                                    .codigoTribNacional("01.07.01")
                                    .descricaoServico("Desenvolvimento de Software Sob Encomenda")
                                    .build())
                            .localPrestacao(LocalPrestacao.builder()
                                    .codigoLocalPrestacao("3550308")
                                    .build())
                            .build())
                    .valores(Valores.builder()
                            .valoresServico(ValoresServico.builder()
                                    .valorServico(new BigDecimal("1500.00"))
                                    .build())
                            .tributacao(Tributacao.builder()
                                    .tributacaoMunicipal(TributacaoMunicipal.builder()
                                            .tributacaoISSQN(TributacaoISSQN.OPERACAO_NORMAL)
                                            .baseCalculo(BaseCalculo.builder()
                                                    .valorBaseCalculo(new BigDecimal("1500.00"))
                                                    .aliquota(new BigDecimal("5.00"))
                                                    .valorISS(new BigDecimal("75.00"))
                                                    .tipoRetencao(TipoRetencaoISSQN.NAO_RETIDO)
                                                    .build())
                                            .build())
                                    .totalTributos(TotalTributos.builder()
                                            .indicadorTotalTributos(IndicadorTotalTributos.SIM)
                                            .percentualTotalTributosSN(new BigDecimal("6.00"))
                                            .build())
                                    .build())
                            .build())
                    .informacaoAdicional(new InformacaoAdicional("Serviço prestado no projeto Alpha"))
                    .build();

            // 4. Emitir sincronamente
            System.out.println("Enviando DPS para SEFIN Nacional...");
            RetornoEmissaoDps retorno = client.emitir(dps);

            if (retorno.sucesso()) {
                System.out.println("NFS-e AUTORIZADA COM SUCESSO!");
                System.out.println("Chave de Acesso: " + retorno.chaveAcesso());
                System.out.println("Protocolo: " + retorno.protocolo());

                // 5. Download do DANFSE (PDF)
                DanfseDocument danfse = client.downloadDanfse(retorno.chaveAcesso());
                danfse.salvarEmArquivo(Path.of("danfse_" + retorno.chaveAcesso() + ".pdf"));
                System.out.println("PDF do DANFSE salvo com sucesso!");
            } else {
                System.err.println("REJEIÇÃO: " + retorno.mensagemErro());
                System.err.println("Código do erro: " + retorno.codigoErro());
            }
        }
    }
}
