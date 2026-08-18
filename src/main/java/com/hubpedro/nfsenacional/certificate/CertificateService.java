package com.hubpedro.nfsenacional.certificate;

import com.hubpedro.nfsenacional.domain.exception.CertificateException;
import com.hubpedro.nfsenacional.domain.exception.CertificateExpiredException;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERUTF8String;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Serviço central e seguro para manipulação em memória de certificados digitais A1 (PKCS#12).
 * Não persiste certificados em disco ou banco.
 */
public final class CertificateService implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(CertificateService.class);
    private static final Pattern NON_DIGITS = Pattern.compile("[^0-9]");
    private static final String KEYSTORE_TYPE = "PKCS12";
    private static final String OID_ICP_BRASIL_PJ = "2.16.76.1.3.3";
    private static final String OID_ICP_BRASIL_PF = "2.16.76.1.3.1";

    private final CertificateMaterial material;
    private final KeyStore keyStore;
    private final String alias;
    private final PrivateKey privateKey;
    private final X509Certificate certificate;
    private final AssinadorArquivoXml xmlSigner;
    private final SSLContext sslContext;

    private CertificateService(CertificateMaterial material) throws CertificateException {
        this.material = material;
        char[] password = material.getPasswordChars();

        try {
            if (material.hasPreloadedKeyStore()) {
                this.keyStore = material.getPreloadedKeyStore();
            } else {
                this.keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
                try (InputStream stream = material.openStream()) {
                    this.keyStore.load(stream, password);
                }
            }

            this.alias = findAlias(this.keyStore);
            this.privateKey = (PrivateKey) this.keyStore.getKey(this.alias, password);
            this.certificate = (X509Certificate) this.keyStore.getCertificate(this.alias);

            if (this.privateKey == null || this.certificate == null) {
                throw new CertificateException("Não foi possível carregar a chave privada ou certificado do KeyStore");
            }

            this.xmlSigner = new AssinadorArquivoXml();
            this.sslContext = SslContextFactory.createContext(this.keyStore, password);

            // Validação imediata de vigência
            validarVigencia();

        } catch (CertificateException e) {
            throw e;
        } catch (Exception e) {
            throw new CertificateException("Erro ao inicializar certificado digital A1: " + e.getMessage(), e);
        }
    }

    public static CertificateService fromMaterial(CertificateMaterial material) throws CertificateException {
        return new CertificateService(material);
    }

    public static CertificateService fromBytes(byte[] bytes, String password) throws CertificateException {
        return new CertificateService(CertificateMaterial.fromBytes(bytes, password));
    }

    public static CertificateService fromBytes(byte[] bytes, char[] password) throws CertificateException {
        return new CertificateService(CertificateMaterial.fromBytes(bytes, password));
    }

    public static CertificateService fromStream(InputStream stream, String password) throws CertificateException {
        return new CertificateService(CertificateMaterial.fromStream(stream, password));
    }

    public static CertificateService fromStream(InputStream stream, char[] password) throws CertificateException {
        return new CertificateService(CertificateMaterial.fromStream(stream, password));
    }

    public static CertificateService fromKeyStore(KeyStore keyStore, String password) throws CertificateException {
        return new CertificateService(CertificateMaterial.fromKeyStore(keyStore, password));
    }

    public static CertificateService fromKeyStore(KeyStore keyStore, char[] password) throws CertificateException {
        return new CertificateService(CertificateMaterial.fromKeyStore(keyStore, password));
    }

    private static String findAlias(KeyStore ks) throws Exception {
        Enumeration<String> aliases = ks.aliases();
        while (aliases.hasMoreElements()) {
            String a = aliases.nextElement();
            if (ks.isKeyEntry(a)) {
                return a;
            }
        }
        throw new CertificateException("Nenhuma entrada de chave privada encontrada no KeyStore");
    }

    /**
     * Valida se o certificado está dentro da vigência temporal.
     */
    public void validarVigencia() throws CertificateException {
        try {
            certificate.checkValidity();
        } catch (java.security.cert.CertificateExpiredException e) {
            throw new CertificateExpiredException("Certificado digital A1 expirado em " + certificate.getNotAfter());
        } catch (CertificateNotYetValidException e) {
            throw new CertificateException("Certificado digital A1 ainda não é válido. Válido a partir de " + certificate.getNotBefore(), e);
        } catch (Exception e) {
            throw new CertificateException("Erro ao validar vigência do certificado: " + e.getMessage(), e);
        }
    }

    /**
     * Extrai o CNPJ do certificado digital ICP-Brasil (da extensão OID 2.16.76.1.3.3 ou Subject).
     */
    public String getCnpjFromCertificate() throws CertificateException {
        // 1. Tentar extrair via Subject Alternative Names (SAN) OID ICP-Brasil
        try {
            Collection<List<?>> sans = certificate.getSubjectAlternativeNames();
            if (sans != null) {
                for (List<?> san : sans) {
                    if (san.size() >= 2 && Integer.valueOf(0).equals(san.get(0))) {
                        Object data = san.get(1);
                        if (data instanceof byte[] bytes) {
                            String cnpj = extractCnpjFromSanBytes(bytes);
                            if (cnpj != null && cnpj.length() == 14) {
                                return cnpj;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Não foi possível ler extensão SAN do certificado: {}", e.getMessage());
        }

        // 2. Fallback: Subject X500 Principal (SERIALNUMBER= ou CN=)
        String subject = certificate.getSubjectX500Principal().getName();
        String[] parts = subject.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.regionMatches(true, 0, "SERIALNUMBER=", 0, 13)) {
                String serial = trimmed.substring(13).trim();
                String digits = NON_DIGITS.matcher(serial).replaceAll("");
                if (digits.length() == 14) {
                    return digits;
                }
            }
            if (trimmed.regionMatches(true, 0, "CN=", 0, 3)) {
                String cn = trimmed.substring(3).trim();
                // Procura padrão :11222333000181 no final do CN
                Matcher matcher = Pattern.compile("(\\d{14})").matcher(cn);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        }

        throw new CertificateException.CnpjNotFoundException("CNPJ não encontrado nas extensões ICP-Brasil ou Subject do certificado");
    }

    private static String extractCnpjFromSanBytes(byte[] bytes) {
        try (ASN1InputStream asn1In = new ASN1InputStream(new ByteArrayInputStream(bytes))) {
            ASN1Primitive primitive = asn1In.readObject();
            if (primitive instanceof ASN1Sequence seq) {
                for (int i = 0; i < seq.size(); i++) {
                    ASN1Primitive item = seq.getObjectAt(i).toASN1Primitive();
                    if (item instanceof ASN1TaggedObject tagged) {
                        ASN1Primitive inner = tagged.getBaseObject().toASN1Primitive();
                        if (inner instanceof ASN1OctetString octets) {
                            String s = new String(octets.getOctets());
                            String digits = NON_DIGITS.matcher(s).replaceAll("");
                            if (digits.length() >= 14) {
                                return digits.substring(0, 14);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Erro de parsing de baixo nível no SAN bytes, tentando outros métodos: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Assina a raiz de um documento DOM XML.
     */
    public void signXmlRoot(Document document) throws Exception {
        if (document == null) {
            throw new IllegalArgumentException("Documento XML não pode ser nulo para assinar");
        }
        xmlSigner.assinarElemento(document, document.getDocumentElement(), privateKey, certificate);
    }

    /**
     * Assina um elemento XML específico do documento DOM.
     */
    public void signXmlElement(Document document, Element elementToSign) throws Exception {
        if (document == null || elementToSign == null) {
            throw new IllegalArgumentException("Document e Element não podem ser nulos para assinar");
        }
        xmlSigner.assinarElemento(document, elementToSign, privateKey, certificate);
    }

    /**
     * Assina uma String XML diretamente e retorna a String XML assinada.
     */
    public String signXmlString(String xml) throws Exception {
        return xmlSigner.assinar(xml, privateKey, certificate);
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public SSLContext getSslContext() {
        return sslContext;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public void close() {
        if (material != null) {
            material.close();
        }
    }
}
