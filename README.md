# 🚀 NFS-e Nacional SDK (Java)

[![Java](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://openjdk.org/)
[![License](https://img.shields.io/badge/License-Commercial%20%2F%20One--Off-green.svg)](LICENSE)
[![Build](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()
[![Padrão](https://img.shields.io/badge/ADN-SEFIN%20Nacional%20v1.00-orange.svg)](https://www.gov.br/nfse)

**Biblioteca Java desacoplada, leve e cirúrgica para emissão, consulta, listagem e cancelamento de NFS-e no Ambiente de Dados Nacional (ADN / Sefin Nacional REST API).**

Desenvolvida especialmente para **ERPs, CRMs, Software Houses e prestadores de serviço** que desejam integrar emissão fiscal diretamente em seus sistemas sem intermediários SaaS, sem mensalidades recorrentes e com **custo único de aquisição (One-Off)**.

---

## 💎 Diferenciais Arquiteturais

- **Zero Bloat / Zero Lock-in**: Não depende de Spring, Hibernate, bancos de dados ou servidores de aplicação. Roda em qualquer ambiente Java 17+ (Java SE puro, Spring Boot 3, Quarkus, Micronaut, AWS Lambda, Docker).
- **Criptografia A1 Segura em Memória**: Carrega certificados digitais PKCS#12 (`.pfx` / `.p12`) via `byte[]`, `InputStream` ou `KeyStore` diretamente na RAM. Limpeza proativa de senhas e chaves privadas após o uso (`AutoCloseable`).
- **Extração Automática de CNPJ ICP-Brasil**: Lê extensões OID ICP-Brasil (`2.16.76.1.3.3`) e Subject X500 sem configurações manuais.
- **Assinador Digital XMLDSig Integrado**: Assinatura digital no padrão oficial W3C XMLDSig (SHA-256 com RSA, canonicalização C14N e Enveloped Transform).
- **Validador XSD Offline Local**: Schemas oficiais (`DPS_v1.00.xsd`, `pedRegEvento_v1.00.xsd`, `xmldsig-core-schema.xsd`) embutidos no JAR. Validação pré-envio em milissegundos sem tráfego desnecessário de rede.
- **Compressão & Codificação Automática**: Pipeline de alta performance UTF-8 ➔ GZip ➔ Base64 para envio e Base64 ➔ GZip ➔ XML/PDF no retorno.
- **Tratamento Semântico de Erros**: Converte retornos HTTP e códigos de erro da SEFIN em exceções tipadas (`SefinDuplicatedEmissionException`, `SefinValidationException`, `SefinUnavailableException`, `SefinTimeoutException`).

---

## 📦 Instalação

Adicione o SDK ao seu `pom.xml`:

```xml
<dependency>
    <groupId>com.hubpedro</groupId>
    <artifactId>nfse-nacional-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## ⚡ Quickstart em 5 Linhas

```java
try (NFSeNacionalClient client = NFSeNacionalClient.builder()
        .withCertificate(pfxBytes, "senhaCertificado")
        .withEnvironment(NFSeNacionalClient.Environment.HOMOLOGACAO)
        .build()) {

    RetornoEmissaoDps retorno = client.emitir(dps);
    if (retorno.sucesso()) {
        System.out.println("Autorizada! Chave de Acesso: " + retorno.chaveAcesso());
    }
}
```

---

## 🛠️ Guia de Uso Completo

### 1. Construção da DPS (Declaração de Prestação de Serviços)

```java
DPS dps = DPS.builder()
    .serie("1")
    .numeroDPS(101L)
    .tipoAmbiente(TipoAmbiente.HOMOLOGACAO)
    .dataHoraEmissao(OffsetDateTime.now())
    .dataCompetencia(LocalDate.now())
    .tipoEmitente(TipoEmitente.PRESTADOR)
    .codigoLocalEmissao("3550308") // Código IBGE (São Paulo/SP)
    .versaoAplicativo("MEU-ERP-1.0")
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
        .nomeRazaoSocial("CLIENTE TOMADOR LTDA")
        .build())
    .servico(Servico.builder()
        .codigoServico(CodigoServico.builder()
            .codigoTribNacional("01.07.01")
            .descricaoServico("Desenvolvimento de Software")
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
    .informacaoAdicional(new InformacaoAdicional("Ref. Contrato 2026/08"))
    .build();
```

---

### 2. Emissão Síncrona da NFS-e

```java
RetornoEmissaoDps retorno = client.emitir(dps);

if (retorno.sucesso()) {
    String chaveAcesso = retorno.chaveAcesso();
    String protocolo = retorno.protocolo();
    String xmlAutorizado = retorno.xmlAutorizado();
    System.out.println("NFS-e Autorizada com sucesso: " + chaveAcesso);
} else {
    System.err.println("Falha na emissão: " + retorno.mensagemErro());
    System.err.println("Código do erro: " + retorno.codigoErro());
}
```

---

### 3. Download do DANFSE (PDF) e XML

```java
// Download do DANFSE em formato PDF
DanfseDocument danfse = client.downloadDanfse(chaveAcesso);
danfse.salvarEmArquivo(Path.of("/tmp/danfse_" + chaveAcesso + ".pdf"));

// Obter Base64 para envio via API ou e-mail
String base64Pdf = danfse.toBase64();

// Download do XML completo autorizado
String xmlAutorizado = client.downloadXml(chaveAcesso);
```

---

### 4. Consulta de Status da DPS & Verificação Rápida

```java
// Consulta completa do processamento da DPS
RetornoConsultaDps consulta = client.consultarDps(chaveDPS);
if (consulta.processada()) {
    System.out.println("DPS processada. Chave de Acesso: " + consulta.chaveAcesso());
}

// Checagem ultrarrápida via HTTP HEAD (sem tráfego de payload)
boolean processada = client.verificarDpsProcessada(chaveDPS);
```

---

### 5. Cancelamento e Substituição de NFS-e

```java
// Cancelamento com motivo
RetornoEventoNfse cancelamento = client.cancelarNfse(
    chaveAcesso,
    "Erro no cálculo do imposto informado pelo prestador",
    "1" // 1 - Erro na emissão
);

if (cancelamento.sucesso()) {
    System.out.println("Cancelamento homologado pelo ADN!");
}

// Substituição de NFS-e
PedidoSubstituicao substituicao = PedidoSubstituicao.builder()
    .chaveAcessoNfse(chaveAcessoOriginal)
    .cnpjOuCpfAutor(cnpjPrestador)
    .codigoMotivo("1")
    .descricaoMotivo("Substituição de tomador de serviço")
    .chaveDpsSubstituta(chaveDpsNova)
    .build();

RetornoEventoNfse retornoSubst = client.substituirNfse(substituicao);
```

---

### 6. Geração de XML Assinado Offline (Conferência / Auditoria)

```java
// Gera e assina o XML localmente sem envio à SEFIN
String xmlAssinado = client.gerarXmlAssinado(dps);
```

---

## ⚙️ Opções de Configuração do Cliente

O `NFSeNacionalClient.builder()` suporta personalização completa:

```java
NFSeNacionalClient client = NFSeNacionalClient.builder()
    // Certificado digital A1 PKCS#12
    .withCertificate(pfxBytes, "senha123")
    // Ambiente: PRODUCAO ou HOMOLOGACAO
    .withEnvironment(NFSeNacionalClient.Environment.PRODUCAO)
    // Timeouts HTTP customizados (em milissegundos)
    .withTimeouts(10000, 20000)
    // Validação local XSD (padrão: true)
    .withXsdValidation(true)
    // URL customizada (opcional / testes locais)
    // .withBaseUrl("https://seu-gateway-ou-proxy.com")
    .build();
```

---

## 🏛️ Estrutura de Pacotes

```
com.hubpedro.nfsenacional
 ├── certificate            # Gerenciamento de certificados A1, mTLS e XMLDSig
 ├── domain
 │    ├── enums             # Enums do padrão ADN (Ambiente, Tributação, Simples)
 │    ├── exception         # Hierarquia rica de exceções de domínio e SEFIN
 │    ├── model             # Agregados (DPS, PedidoCancelamento, PedidoSubstituicao)
 │    └── valueobject       # Value Objects auto-validados (CNPJ, CPF, ChaveDPS, etc.)
 ├── gateway                # Cliente HTTP REST mTLS, DTOs e mapeador de erros
 ├── model                  # Records de retorno da API pública (RetornoEmissaoDps, etc.)
 └── xml                    # Geradores XML DOM, Validador XSD offline e PayloadEncoder
```

---

## 🧪 Testes Automatizados

O projeto conta com suíte completa de testes unitários e de integração com servidor HTTP embutido:

```bash
# Compilar projeto
mvn clean compile

# Compilar testes
mvn test-compile

# Executar suíte completa de testes
mvn clean test
```

---

## 📄 Licença e Suporte

Desenvolvido para venda como componente comercial de integração (One-off purchase).
Para licenças comerciais e suporte corporativo, entre em contato através de [suporte@hubpedro.com](mailto:suporte@hubpedro.com).
