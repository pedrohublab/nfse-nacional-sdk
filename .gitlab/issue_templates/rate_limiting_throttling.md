# [FEAT] Implementação de Rate Limiting e Throttling para Emissões, Consultas e Eventos em Lote

## 🎯 Contexto e Motivação
A SEFIN Nacional / ADN possui limites operacionais de vazão (rate limits) e janelas de processamento por certificado digital (mTLS) e endereço IP. Enviar emissões (`POST /nfse`), consultas (`GET /dps/{id}`, `GET /nfse/{chave}`) ou cancelamentos em alta frequência (dezenas por segundo em loop) pode ocasionar:
1. Respostas `HTTP 429 Too Many Requests`.
2. Bloqueio temporário da sessão TLS/mTLS ou rejeições por concorrência.
3. Descarte de requisições síncronas antes do enfileiramento no ADN.

Precisamos implementar um mecanismo nativo, leve e configurável de **Throttling / Rate Limiting** e **Backoff** no SDK, mantendo a premissa de **Zero Bloat** (sem dependências de frameworks pesados ou Redis).

---

## 🛠️ Especificação Técnica da Solução

### 1. Componentes a Implementar no SDK
- **`RateLimiter` em Java Puro**: Implementação em memória baseada em *Token Bucket* ou *Fixed Window / Leaky Bucket* com `LockSupport`/`Semaphore`.
- **Configuração Fluente no `NFSeNacionalClient.Builder`**:
  ```java
  NFSeNacionalClient client = NFSeNacionalClient.builder()
      .withCertificate(pfxBytes, password)
      .withEnvironment(Environment.PRODUCAO)
      .withRateLimit(5) // Máximo de 5 requisições por segundo
      .withDelayBetweenRequests(Duration.ofMillis(200)) // Delay mínimo entre requisições consecutivas
      .withAutoRetryOnRateLimit(true, 3) // Retry com backoff exponencial se receber HTTP 429
      .build();
  ```
- **Interceptador no `SefinNacionalGateway`**:
  - Garantir o respeito ao intervalo mínimo antes de disparar o `HttpClient.send(...)`.
  - Tratamento do header `Retry-After` da SEFIN quando retornado status HTTP 429.

### 2. Endpoints Afetados
- `POST /nfse` (Emissão de DPS)
- `GET /dps/{id}` e `HEAD /dps/{id}` (Consulta e checagem de status)
- `GET /nfse/{chaveAcesso}` e `GET /danfse/{chaveAcesso}` (Download de XML/PDF)
- `POST /nfse/{chaveAcesso}/eventos` (Cancelamentos e substituições)

---

## 📋 Critérios de Aceite (Definition of Done)
- [ ] Implementação de `RateLimiter` leve sem dependências externas.
- [ ] Configuração opcional (opt-in) no builder do `NFSeNacionalClient` com valores padrão seguros.
- [ ] Suporte a execução concorrente *thread-safe* sem condições de corrida.
- [ ] Tratamento de erro HTTP 429 com exceção tipada `SefinRateLimitExceededException` e política de retry configurável.
- [ ] Testes unitários com simulação de rajada (burst) comprovando o espaçamento temporal adequado das requisições.
- [ ] Atualização do `README.md` com exemplos de processamento em lote e boas práticas de vazão.
