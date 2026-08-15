package com.hubpedro.nfsenacional;

import com.hubpedro.nfsenacional.certificate.CertificateMaterial;
import com.hubpedro.nfsenacional.certificate.CertificateService;
import com.hubpedro.nfsenacional.domain.enums.DpsEmissionStatus;
import com.hubpedro.nfsenacional.domain.enums.TipoAmbiente;
import com.hubpedro.nfsenacional.domain.exception.CertificateException;
import com.hubpedro.nfsenacional.domain.exception.SefinApiException;
import com.hubpedro.nfsenacional.domain.exception.SefinTimeoutException;
import com.hubpedro.nfsenacional.domain.exception.XmlValidationException;
import com.hubpedro.nfsenacional.domain.model.DPS;
import com.hubpedro.nfsenacional.domain.model.PedidoCancelamento;
import com.hubpedro.nfsenacional.domain.model.PedidoSubstituicao;
import com.hubpedro.nfsenacional.gateway.SefinErrorMapper;
import com.hubpedro.nfsenacional.gateway.SefinNacionalConfig;
import com.hubpedro.nfsenacional.gateway.SefinNacionalGateway;
import com.hubpedro.nfsenacional.gateway.SefinNacionalGateway.SefinResponse;
import com.hubpedro.nfsenacional.gateway.dto.*;
import com.hubpedro.nfsenacional.model.*;
import com.hubpedro.nfsenacional.xml.DpsXmlGenerator;
import com.hubpedro.nfsenacional.xml.EventoXmlGenerator;
import com.hubpedro.nfsenacional.xml.PayloadEncoder;
import com.hubpedro.nfsenacional.xml.XmlSchemaValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.Closeable;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Objects;

/**
 * Fachada principal (Fluent Client) do SDK NFS-e Nacional.
 * Totalmente desacoplada de frameworks web ou bancos de dados.
 *
 * <p><strong>Exemplo de uso rápido:</strong></p>
 * <pre>{@code
 * try (NFSeNacionalClient client = NFSeNacionalClient.builder()
 *         .withCertificate(pfxBytes, "senha123")
 *         .withEnvironment(Environment.HOMOLOGACAO)
 *         .build()) {
 *
 *     RetornoEmissaoDps retorno = client.emitir(dps);
 *     if (retorno.sucesso()) {
 *         System.out.println("Autorizada: " + retorno.chaveAcesso());
 *     }
 * }
 * }</pre>
 */
public final class NFSeNacionalClient implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(NFSeNacionalClient.class);

    private final CertificateService certificateService;
    private final SefinNacionalGateway gateway;
    private final DpsXmlGenerator dpsXmlGenerator;
    private final EventoXmlGenerator eventoXmlGenerator;
    private final XmlSchemaValidator xmlSchemaValidator;
    private final Environment environment;
    private final boolean validateXsd;

    private NFSeNacionalClient(Builder builder) throws CertificateException {
        this.certificateService = builder.buildCertificateService();
        this.environment = builder.environment != null ? builder.environment : Environment.HOMOLOGACAO;
        this.validateXsd = builder.validateXsd;

        SefinNacionalConfig config = resolveConfig(builder);
        this.gateway = new SefinNacionalGateway(config, certificateService.getSslContext());
        this.dpsXmlGenerator = new DpsXmlGenerator();
        this.eventoXmlGenerator = new EventoXmlGenerator();
        this.xmlSchemaValidator = new XmlSchemaValidator();

        log.info("NFSeNacionalClient inicializado com sucesso no ambiente {}", this.environment);
    }

    NFSeNacionalClient(CertificateService certificateService, SefinNacionalGateway gateway, Environment environment) {
        this.certificateService = certificateService;
        this.gateway = gateway;
        this.environment = environment != null ? environment : Environment.HOMOLOGACAO;
        this.validateXsd = true;
        this.dpsXmlGenerator = new DpsXmlGenerator();
        this.eventoXmlGenerator = new EventoXmlGenerator();
        this.xmlSchemaValidator = new XmlSchemaValidator();
    }

    public static Builder builder() {
        return new Builder();
    }

    private static SefinNacionalConfig resolveConfig(Builder builder) {
        if (builder.customBaseUrl != null && !builder.customBaseUrl.isBlank()) {
            return SefinNacionalConfig.custom(builder.customBaseUrl, builder.connectTimeoutMs, builder.readTimeoutMs);
        }
        if (builder.environment == Environment.PRODUCAO) {
            return new SefinNacionalConfig(SefinNacionalConfig.URL_PRODUCAO, builder.connectTimeoutMs, builder.readTimeoutMs);
        }
        return new SefinNacionalConfig(SefinNacionalConfig.URL_HOMOLOGACAO, builder.connectTimeoutMs, builder.readTimeoutMs);
    }

    /**
     * Emissão síncrona de DPS no padrão nacional (Geração XML -> Validação XSD -> Assinatura XMLDSig -> GZip+Base64 -> POST /dps).
     *
     * @param dps aggregate DPS preenchido
     * @return RetornoEmissaoDps contendo status e dados fiscais
     */
    public RetornoEmissaoDps emitir(DPS dps) {
        Objects.requireNonNull(dps, "DPS não pode ser nula para emissão");
        String chaveDPS = dps.getChaveDPS().valor();
        log.info("Iniciando fluxo de emissão da DPS {}", chaveDPS);

        String xmlAssinado = null;
        try {
            // 1. Gerar documento DOM
            Document doc = dpsXmlGenerator.gerarDocument(dps);

            // 2. Assinar digitalmente com certificado A1 em memória
            certificateService.signXmlRoot(doc);
            xmlAssinado = DpsXmlGenerator.class.cast(dpsXmlGenerator).gerar(dps);
            xmlAssinado = certificateService.signXmlString(dpsXmlGenerator.gerar(dps));

            // 3. Validação XSD local pré-envio
            if (validateXsd) {
                xmlSchemaValidator.validarDps(xmlAssinado);
            }

            // 4. Compactação GZIP + Base64
            String encodedPayload = PayloadEncoder.encodeForSefin(xmlAssinado);

            // 5. Envio síncrono para o gateway da SEFIN Nacional
            SefinResponse<SefinEmissaoResponseDTO> response = gateway.enviarDps(encodedPayload);

            // 6. Mapeamento de resposta
            SefinEmissaoResponseDTO parsed = response.parsed();
            DpsEmissionStatus status = SefinErrorMapper.mapearStatus(response.httpStatus(), parsed);

            if (status == DpsEmissionStatus.AUTHORIZED) {
                String chaveAcesso = parsed != null ? parsed.chaveAcesso() : null;
                String protocolo = parsed != null ? parsed.protocolo() : null;
                String xmlAutorizado = parsed != null && parsed.nfseXmlGZipB64() != null
                        ? PayloadEncoder.decodeFromSefin(parsed.nfseXmlGZipB64())
                        : null;

                log.info("DPS {} autorizada com sucesso: chaveAcesso={}, protocolo={}", chaveDPS, chaveAcesso, protocolo);
                return RetornoEmissaoDps.sucesso(chaveDPS, chaveAcesso, protocolo, xmlAssinado, xmlAutorizado,
                        response.rawBody(), response.httpStatus(), response.elapsedMs());
            } else if (status == DpsEmissionStatus.UNKNOWN || status == DpsEmissionStatus.TIMEOUT) {
                log.warn("DPS {} retornou status indeterminado/timeout: httpStatus={}", chaveDPS, response.httpStatus());
                return RetornoEmissaoDps.indeterminado(chaveDPS, "Timeout ou resposta indeterminada da SEFIN",
                        xmlAssinado, response.httpStatus(), response.elapsedMs());
            } else {
                String codErro = parsed != null ? parsed.codigoStatus() : String.valueOf(response.httpStatus());
                String msgErro = parsed != null && parsed.descricaoStatus() != null ? parsed.descricaoStatus() : "Rejeição HTTP " + response.httpStatus();
                log.warn("DPS {} rejeitada: status={}, codErro={}, msg={}", chaveDPS, response.httpStatus(), codErro, msgErro);
                return RetornoEmissaoDps.rejeitado(chaveDPS, msgErro, codErro, xmlAssinado, response.rawBody(),
                        response.httpStatus(), response.elapsedMs());
            }

        } catch (XmlValidationException e) {
            log.error("Validação XSD local falhou para DPS {}: {}", chaveDPS, e.getMessage());
            return RetornoEmissaoDps.rejeitado(chaveDPS, "Erro no Schema XSD: " + e.getMessage(), "501",
                    xmlAssinado, null, 422, 0);
        } catch (SefinTimeoutException e) {
            log.warn("Timeout na comunicação para DPS {}: {}", chaveDPS, e.getMessage());
            return RetornoEmissaoDps.indeterminado(chaveDPS, e.getMessage(), xmlAssinado, 504, 0);
        } catch (SefinApiException e) {
            log.warn("Exceção retornada pela SEFIN para DPS {}: {}", chaveDPS, e.getMessage());
            return RetornoEmissaoDps.rejeitado(chaveDPS, e.getMessage(), e.getCodigoError(), xmlAssinado,
                    e.getResponseBody(), e.getHttpStatus(), 0);
        } catch (Exception e) {
            log.error("Erro inesperado na emissão da DPS {}: {}", chaveDPS, e.getMessage(), e);
            return RetornoEmissaoDps.erro(chaveDPS, e.getMessage(), "999", xmlAssinado, 0, 0);
        }
    }

    /**
     * Gera o XML da DPS devidamente assinado sem enviá-lo à rede (para conferência e testes).
     */
    public String gerarXmlAssinado(DPS dps) throws Exception {
        Objects.requireNonNull(dps, "DPS não pode ser nula");
        String xml = dpsXmlGenerator.gerar(dps);
        String assinado = certificateService.signXmlString(xml);
        if (validateXsd) {
            xmlSchemaValidator.validarDps(assinado);
        }
        return assinado;
    }

    /**
     * Consulta o processamento de uma DPS previamente enviada via GET /dps/{chaveDPS}.
     */
    public RetornoConsultaDps consultarDps(String chaveDPS) {
        Objects.requireNonNull(chaveDPS, "Chave da DPS é obrigatória");
        SefinResponse<SefinConsultaDpsResponseDTO> response = gateway.consultarDps(chaveDPS);
        SefinConsultaDpsResponseDTO parsed = response.parsed();

        boolean processada = parsed != null && parsed.isProcessada();
        String chaveAcesso = parsed != null ? parsed.chaveAcesso() : null;
        String protocolo = parsed != null ? parsed.protocolo() : null;
        String codigoStatus = parsed != null ? parsed.codigoStatus() : String.valueOf(response.httpStatus());
        String descricaoStatus = parsed != null ? parsed.descricaoStatus() : response.rawBody();
        String xmlAutorizado = parsed != null && parsed.nfseXmlGZipB64() != null
                ? PayloadEncoder.decodeFromSefin(parsed.nfseXmlGZipB64())
                : null;

        return new RetornoConsultaDps(
                processada, chaveDPS, chaveAcesso, protocolo, codigoStatus,
                descricaoStatus, xmlAutorizado, response.rawBody(), response.httpStatus(), response.elapsedMs()
        );
    }

    /**
     * Verificação ultrarrápida via HEAD /dps/{chaveDPS} para checar se a DPS já foi processada.
     */
    public boolean verificarDpsProcessada(String chaveDPS) {
        Objects.requireNonNull(chaveDPS, "Chave da DPS é obrigatória");
        return gateway.verificarDpsProcessada(chaveDPS);
    }

    /**
     * Consulta uma NFS-e emitida a partir de sua Chave de Acesso de 50 dígitos via GET /nfse/{chaveAcesso}.
     */
    public RetornoConsultaNfse consultarNfse(String chaveAcesso) {
        Objects.requireNonNull(chaveAcesso, "Chave de acesso é obrigatória");
        SefinResponse<SefinConsultaNfseResponseDTO> response = gateway.consultarNfse(chaveAcesso);
        SefinConsultaNfseResponseDTO parsed = response.parsed();

        boolean autorizada = parsed != null && parsed.isAutorizada();
        boolean cancelada = parsed != null && parsed.isCancelada();
        Long numeroNFSe = parsed != null ? parsed.numeroNFSe() : null;
        String serie = parsed != null ? parsed.serie() : null;
        String protocolo = parsed != null ? parsed.protocolo() : null;
        String codigoStatus = parsed != null ? parsed.codigoStatus() : String.valueOf(response.httpStatus());
        String descricaoStatus = parsed != null ? parsed.descricaoStatus() : response.rawBody();
        String xmlAutorizado = parsed != null && parsed.nfseXmlGZipB64() != null
                ? PayloadEncoder.decodeFromSefin(parsed.nfseXmlGZipB64())
                : null;
        byte[] danfsePdfBytes = parsed != null && parsed.danfsePdfGZipB64() != null
                ? PayloadEncoder.decodeBytesFromSefin(parsed.danfsePdfGZipB64())
                : null;

        return new RetornoConsultaNfse(
                autorizada, cancelada, chaveAcesso, numeroNFSe, serie, protocolo,
                codigoStatus, descricaoStatus, xmlAutorizado, danfsePdfBytes,
                response.rawBody(), response.httpStatus(), response.elapsedMs()
        );
    }

    /**
     * Download do XML autorizado da NFS-e.
     */
    public String downloadXml(String chaveAcesso) {
        RetornoConsultaNfse consulta = consultarNfse(chaveAcesso);
        if (consulta.xmlAutorizado() != null) {
            return consulta.xmlAutorizado();
        }
        throw new SefinApiException("XML não disponível para a chave de acesso informada: " + chaveAcesso);
    }

    /**
     * Download do Documento Auxiliar da NFS-e (DANFSE em formato PDF).
     */
    public DanfseDocument downloadDanfse(String chaveAcesso) {
        Objects.requireNonNull(chaveAcesso, "Chave de acesso é obrigatória");
        SefinResponse<byte[]> response = gateway.downloadDanfse(chaveAcesso);
        if (response.httpStatus() == 200 && response.parsed() != null) {
            return new DanfseDocument(chaveAcesso, response.parsed());
        }

        // Fallback: tentar obter via consulta regular com campo danfsePdfGZipB64
        RetornoConsultaNfse consulta = consultarNfse(chaveAcesso);
        if (consulta.danfsePdfBytes() != null) {
            return new DanfseDocument(chaveAcesso, consulta.danfsePdfBytes());
        }

        throw new SefinApiException("Não foi possível obter o PDF do DANFSE para a chave: " + chaveAcesso);
    }

    /**
     * Cancela uma NFS-e autorizada via registro de evento e101101.
     */
    public RetornoEventoNfse cancelarNfse(PedidoCancelamento pedido) {
        Objects.requireNonNull(pedido, "Pedido de cancelamento não pode ser nulo");
        try {
            String xmlEvento = eventoXmlGenerator.gerarXmlCancelamento(pedido);
            String xmlAssinado = certificateService.signXmlString(xmlEvento);

            if (validateXsd) {
                xmlSchemaValidator.validarEvento(xmlAssinado);
            }

            String encoded = PayloadEncoder.encodeForSefin(xmlAssinado);
            SefinResponse<SefinEventoResponseDTO> response = gateway.registrarEvento(pedido.getChaveAcessoNfse(), encoded);

            SefinEventoResponseDTO parsed = response.parsed();
            boolean sucesso = response.httpStatus() == 200 || (parsed != null && parsed.isSucesso());
            String idEvento = parsed != null ? parsed.idEvento() : pedido.getIdXml();
            String protocolo = parsed != null ? parsed.descricaoStatus() : null;
            String codStatus = parsed != null ? parsed.codigoStatus() : String.valueOf(response.httpStatus());
            String descStatus = parsed != null ? parsed.descricaoStatus() : response.rawBody();

            return new RetornoEventoNfse(
                    sucesso, idEvento, pedido.getChaveAcessoNfse(), "e101101",
                    protocolo, codStatus, descStatus, response.rawBody(), response.httpStatus(), response.elapsedMs()
            );
        } catch (Exception e) {
            log.error("Erro ao cancelar NFS-e {}: {}", pedido.getChaveAcessoNfse(), e.getMessage(), e);
            throw new SefinApiException("Erro ao registrar evento de cancelamento: " + e.getMessage(), null, 0, null, e);
        }
    }

    /**
     * Conveniência para cancelar NFS-e informando apenas chave de acesso e motivo.
     */
    public RetornoEventoNfse cancelarNfse(String chaveAcesso, String motivo, String codigoMotivo) {
        String cnpjAutor = getCnpjCertificado();
        TipoAmbiente amb = environment == Environment.PRODUCAO ? TipoAmbiente.PRODUCAO : TipoAmbiente.HOMOLOGACAO;

        PedidoCancelamento pedido = PedidoCancelamento.builder()
                .chaveAcessoNfse(chaveAcesso)
                .cnpjOuCpfAutor(cnpjAutor)
                .codigoMotivo(codigoMotivo != null ? codigoMotivo : "1")
                .descricaoMotivo(motivo)
                .tipoAmbiente(amb)
                .build();

        return cancelarNfse(pedido);
    }

    /**
     * Substitui uma NFS-e autorizada via registro de evento e101103.
     */
    public RetornoEventoNfse substituirNfse(PedidoSubstituicao pedido) {
        Objects.requireNonNull(pedido, "Pedido de substituição não pode ser nulo");
        try {
            String xmlEvento = eventoXmlGenerator.gerarXmlSubstituicao(pedido);
            String xmlAssinado = certificateService.signXmlString(xmlEvento);

            if (validateXsd) {
                xmlSchemaValidator.validarEvento(xmlAssinado);
            }

            String encoded = PayloadEncoder.encodeForSefin(xmlAssinado);
            SefinResponse<SefinEventoResponseDTO> response = gateway.registrarEvento(pedido.getChaveAcessoNfse(), encoded);

            SefinEventoResponseDTO parsed = response.parsed();
            boolean sucesso = response.httpStatus() == 200 || (parsed != null && parsed.isSucesso());
            String idEvento = parsed != null ? parsed.idEvento() : pedido.getIdXml();
            String protocolo = parsed != null ? parsed.descricaoStatus() : null;
            String codStatus = parsed != null ? parsed.codigoStatus() : String.valueOf(response.httpStatus());
            String descStatus = parsed != null ? parsed.descricaoStatus() : response.rawBody();

            return new RetornoEventoNfse(
                    sucesso, idEvento, pedido.getChaveAcessoNfse(), "e101103",
                    protocolo, codStatus, descStatus, response.rawBody(), response.httpStatus(), response.elapsedMs()
            );
        } catch (Exception e) {
            log.error("Erro ao substituir NFS-e {}: {}", pedido.getChaveAcessoNfse(), e.getMessage(), e);
            throw new SefinApiException("Erro ao registrar evento de substituição: " + e.getMessage(), null, 0, null, e);
        }
    }

    /**
     * Extrai o CNPJ do certificado digital A1 atualmente carregado no cliente.
     */
    public String getCnpjCertificado() {
        try {
            return certificateService.getCnpjFromCertificate();
        } catch (Exception e) {
            log.warn("Não foi possível extrair CNPJ do certificado: {}", e.getMessage());
            return "00000000000000";
        }
    }

    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public void close() {
        if (certificateService != null) {
            certificateService.close();
            log.info("NFSeNacionalClient encerrado - Material criptográfico limpo da memória");
        }
    }

    /**
     * Ambientes de operação da SEFIN Nacional.
     */
    public enum Environment {
        PRODUCAO,
        HOMOLOGACAO
    }

    /**
     * Fluent Builder para construção do NFSeNacionalClient.
     */
    public static final class Builder {
        private CertificateMaterial material;
        private Environment environment = Environment.HOMOLOGACAO;
        private String customBaseUrl;
        private int connectTimeoutMs = SefinNacionalConfig.DEFAULT_CONNECT_TIMEOUT_MS;
        private int readTimeoutMs = SefinNacionalConfig.DEFAULT_READ_TIMEOUT_MS;
        private boolean validateXsd = true;

        public Builder withCertificate(byte[] bytes, String password) {
            this.material = CertificateMaterial.fromBytes(bytes, password);
            return this;
        }

        public Builder withCertificate(byte[] bytes, char[] password) {
            this.material = CertificateMaterial.fromBytes(bytes, password);
            return this;
        }

        public Builder withCertificate(InputStream stream, String password) throws CertificateException {
            this.material = CertificateMaterial.fromStream(stream, password);
            return this;
        }

        public Builder withCertificate(InputStream stream, char[] password) throws CertificateException {
            this.material = CertificateMaterial.fromStream(stream, password);
            return this;
        }

        public Builder withCertificate(KeyStore keyStore, String password) {
            this.material = CertificateMaterial.fromKeyStore(keyStore, password);
            return this;
        }

        public Builder withCertificate(KeyStore keyStore, char[] password) {
            this.material = CertificateMaterial.fromKeyStore(keyStore, password);
            return this;
        }

        public Builder withEnvironment(Environment environment) {
            this.environment = environment;
            return this;
        }

        public Builder withBaseUrl(String baseUrl) {
            this.customBaseUrl = baseUrl;
            return this;
        }

        public Builder withTimeouts(int connectTimeoutMs, int readTimeoutMs) {
            this.connectTimeoutMs = connectTimeoutMs;
            this.readTimeoutMs = readTimeoutMs;
            return this;
        }

        public Builder withXsdValidation(boolean validate) {
            this.validateXsd = validate;
            return this;
        }

        public NFSeNacionalClient build() throws CertificateException {
            if (material == null) {
                throw new IllegalArgumentException("Certificado digital A1 não informado. Use withCertificate()");
            }
            return new NFSeNacionalClient(this);
        }

        CertificateService buildCertificateService() throws CertificateException {
            return CertificateService.fromMaterial(material);
        }
    }
}
