package com.hubpedro.nfsenacional.util;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * Utilitário de testes para gerar certificados PKCS#12 A1 válidos em memória.
 */
public final class TestCertificateGenerator {

    private TestCertificateGenerator() {
    }

    public static byte[] generateTestPkcs12Bytes(String cnpj, String password) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        long now = System.currentTimeMillis();
        Date startDate = new Date(now - 1000L * 60 * 60 * 24); // Ontem
        Date endDate = new Date(now + 1000L * 60 * 60 * 24 * 365); // 1 ano

        BigInteger serial = BigInteger.valueOf(now);
        X500Name issuer = new X500Name("CN=EMPRESA DE TESTE LTDA:" + cnpj + ", OU=Certificado A1 Teste, O=ICP-Brasil, C=BR");
        X500Name subject = issuer;

        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuer,
                serial,
                startDate,
                endDate,
                subject,
                keyPair.getPublic()
        );

        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSA").build(keyPair.getPrivate());
        X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certBuilder.build(signer));

        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(null, password.toCharArray());
        ks.setKeyEntry("teste-cert", keyPair.getPrivate(), password.toCharArray(), new java.security.cert.Certificate[]{cert});

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ks.store(baos, password.toCharArray());
        return baos.toByteArray();
    }
}
