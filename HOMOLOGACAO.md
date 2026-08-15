# 🧪 Guia de Homologação & Certificação SEFIN Nacional

Este documento orienta desenvolvedores e integradores sobre o processo de testes e homologação no **Ambiente de Dados Nacional (ADN / SEFIN Nacional - Produção Restrita)** utilizando o `nfse-nacional-sdk`.

---

## 🌐 Endpoints Oficiais da SEFIN Nacional

| Ambiente | Finalidade | Base URL |
|---|---|---|
| **Homologação** (Produção Restrita) | Testes e validação fiscal sem valor legal | `https://sefin.producaorestrita.nfse.gov.br/SefinNacional` |
| **Produção** | Emissão real com validade jurídica | `https://sefin.nfse.gov.br/SefinNacional` |

---

## 🔑 1. Requisitos para Homologação

1. **Certificado Digital A1 ICP-Brasil (Pessoa Jurídica - e-CNPJ)**:
   - Formato `.pfx` ou `.p12`.
   - Pode ser o certificado de produção da sua software house ou da empresa emissora. No ambiente de Produção Restrita, o ADN valida a estrutura e assinatura, mas os documentos gerados **não possuem efeito fiscal**.
2. **Cadastro no Portal Nacional da NFS-e**:
   - Acesso ao portal de Produção Restrita da NFS-e Nacional com o certificado digital para verificação de permissões do prestador.

---

## 🚀 2. Como Configurar as Variáveis no GitLab CI/CD

Para que a pipeline do GitLab execute testes ponta a ponta contra a SEFIN Produção Restrita:

1. Acesse seu projeto no GitLab: **Settings > CI/CD > Variables**.
2. Adicione as seguintes variáveis:
   - `HOMOLOGACAO_CERT_PFX_BASE64`: Conteúdo do arquivo `.pfx` convertido em Base64 (marcar como **Masked** e **Protected**).
   - `HOMOLOGACAO_CERT_PASSWORD`: Senha do certificado digital A1 (marcar como **Masked**).

> **Como gerar o Base64 do certificado localmente:**
> ```bash
> base64 -w 0 seu_certificado.pfx
> ```

---

## 💻 3. Execução de Testes de Homologação Localmente

Você pode executar o teste de homologação real na sua máquina antes de subir para o GitLab:

### Via Linha de Comando (com variáveis de ambiente):
```bash
export HOMOLOGACAO_CERT_PATH="/caminho/para/certificado.pfx"
export HOMOLOGACAO_CERT_PASSWORD="sua_senha_aqui"

mvn test-compile
java -cp "target/classes:target/test-classes:$(find ~/.m2/repository -name '*.jar' | tr '\n' ':')" com.hubpedro.nfsenacional.TestRunner
```

### Via Código Java:
```java
byte[] pfxBytes = Files.readAllBytes(Path.of("/caminho/certificado.pfx"));

try (NFSeNacionalClient client = NFSeNacionalClient.builder()
        .withCertificate(pfxBytes, "senha123")
        .withEnvironment(NFSeNacionalClient.Environment.HOMOLOGACAO)
        .build()) {

    // Emissão de teste
    RetornoEmissaoDps retorno = client.emitir(dps);
    System.out.println("Status: " + retorno.status());
    System.out.println("Chave de Acesso: " + retorno.chaveAcesso());
}
```

---

## 📋 4. Roteiro de Testes Padrão (Checklist de Homologação)

Recomendamos executar o seguinte roteiro para garantir 100% de conformidade com o ADN:

- [x] **Cenário 01: Emissão Síncrona Regular**
  - Enviar DPS com prestador optante pelo Simples Nacional.
  - Verificar retorno com HTTP 200 e `chaveAcesso` de 50 dígitos gerada.
- [x] **Cenário 02: Download do DANFSE (PDF)**
  - Obter PDF do DANFSE através da chave de acesso e validar visualização.
- [x] **Cenário 03: Consulta por Chave de DPS**
  - Consultar status via `GET /dps/{chave}` e `HEAD /dps/{chave}`.
- [x] **Cenário 04: Cancelamento de NFS-e (Evento e101101)**
  - Registrar pedido de cancelamento informando motivo.
  - Verificar retorno com status `101` (Cancelamento Homologado).
- [x] **Cenário 05: Substituição de NFS-e (Evento e101103)**
  - Emitir nova DPS substituta e vincular no evento `e101103`.
- [x] **Cenário 06: Teste de Resiliência e Rejeição**
  - Enviar DPS duplicada ou com alíquota incorreta e verificar se o SDK lança/trata `SefinDuplicatedEmissionException` ou `SefinValidationException`.

---

## 📑 5. Resolução de Rejeições Comuns da SEFIN

| Código SEFIN | Causa Comum | Como Resolver |
|---|---|---|
| **E123** | Alíquota de ISS incompatível com o item da LC 116 no município | Verificar alíquota mínima (2%) e máxima (5%) ou regime de tributação. |
| **204** | Duplicidade de DPS (já emitida com mesmo número e série) | Incrementar o `numeroDPS` na emissão ou consultar a chave existente. |
| **501** | Erro no Schema XSD do XML | O validador offline do SDK impede o envio de XMLs com erros estruturais. Verifique se todos os campos obrigatórios foram preenchidos. |
| **403 / 401** | Certificado digital não autorizado ou não reconhecido pela SEFIN | Verificar se o certificado é e-CNPJ válido ICP-Brasil e se está na vigência. |
