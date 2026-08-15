package com.hubpedro.nfsenacional.certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

/**
 * Fábrica para configuração do contexto de segurança SSL/TLS (mTLS).
 * Configura TLS 1.2 / TLS 1.3 com certificado do cliente.
 */
public final class SslContextFactory {

    private static final String PROTOCOL_TLS12 = "TLSv1.2";
    private static final String PROTOCOL_TLS = "TLS";

    private SslContextFactory() {
    }

    /**
     * Cria um SSLContext configurado para autenticação mútua (mTLS) com a SEFIN Nacional.
     *
     * @param keyStore KeyStore PKCS#12 contendo o certificado A1 e a chave privada
     * @param password Senha do certificado
     * @return SSLContext inicializado
     */
    public static SSLContext createContext(KeyStore keyStore, char[] password) throws Exception {
        if (keyStore == null) {
            throw new IllegalArgumentException("KeyStore não pode ser nulo para criar SSLContext");
        }

        // 1. Configurar KeyManagerFactory com o certificado do cliente
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, password);

        // 2. TrustManager padrão (JDK cacerts)
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init((KeyStore) null);

        // 3. Inicializar SSLContext com TLS 1.2 / TLS
        SSLContext context;
        try {
            context = SSLContext.getInstance(PROTOCOL_TLS12);
        } catch (NoSuchAlgorithmException e) {
            try {
                context = SSLContext.getInstance(PROTOCOL_TLS);
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException("Nenhum protocolo TLS disponível no ambiente", ex);
            }
        }

        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        return context;
    }
}
