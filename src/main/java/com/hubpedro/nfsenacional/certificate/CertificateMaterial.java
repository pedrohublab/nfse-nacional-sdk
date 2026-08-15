package com.hubpedro.nfsenacional.certificate;

import com.hubpedro.nfsenacional.domain.exception.CertificateException;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Objects;

/**
 * Encapsula o material criptográfico do certificado A1 em memória.
 * Suporta inicialização a partir de byte[], InputStream ou KeyStore existente.
 * Garante a limpeza da senha da memória quando fechado.
 */
public final class CertificateMaterial implements Closeable {

    private final byte[] certificateBytes;
    private final KeyStore preloadedKeyStore;
    private final char[] passwordChars;

    private CertificateMaterial(byte[] certificateBytes, KeyStore preloadedKeyStore, char[] passwordChars) {
        this.certificateBytes = certificateBytes != null ? certificateBytes.clone() : null;
        this.preloadedKeyStore = preloadedKeyStore;
        this.passwordChars = passwordChars != null ? passwordChars.clone() : new char[0];
    }

    public static CertificateMaterial fromBytes(byte[] bytes, String password) {
        Objects.requireNonNull(bytes, "Bytes do certificado não podem ser nulos");
        return new CertificateMaterial(bytes, null, password != null ? password.toCharArray() : null);
    }

    public static CertificateMaterial fromBytes(byte[] bytes, char[] password) {
        Objects.requireNonNull(bytes, "Bytes do certificado não podem ser nulos");
        return new CertificateMaterial(bytes, null, password);
    }

    public static CertificateMaterial fromStream(InputStream stream, String password) throws CertificateException {
        Objects.requireNonNull(stream, "Stream do certificado não pode ser nulo");
        try (stream) {
            byte[] bytes = stream.readAllBytes();
            return fromBytes(bytes, password);
        } catch (Exception e) {
            throw new CertificateException("Erro ao ler bytes do InputStream do certificado", e);
        }
    }

    public static CertificateMaterial fromStream(InputStream stream, char[] password) throws CertificateException {
        Objects.requireNonNull(stream, "Stream do certificado não pode ser nulo");
        try (stream) {
            byte[] bytes = stream.readAllBytes();
            return fromBytes(bytes, password);
        } catch (Exception e) {
            throw new CertificateException("Erro ao ler bytes do InputStream do certificado", e);
        }
    }

    public static CertificateMaterial fromKeyStore(KeyStore keyStore, String password) {
        Objects.requireNonNull(keyStore, "KeyStore não pode ser nulo");
        return new CertificateMaterial(null, keyStore, password != null ? password.toCharArray() : null);
    }

    public static CertificateMaterial fromKeyStore(KeyStore keyStore, char[] password) {
        Objects.requireNonNull(keyStore, "KeyStore não pode ser nulo");
        return new CertificateMaterial(null, keyStore, password);
    }

    public InputStream openStream() {
        if (certificateBytes != null) {
            return new ByteArrayInputStream(certificateBytes);
        }
        return null;
    }

    public byte[] getCertificateBytes() {
        return certificateBytes != null ? certificateBytes.clone() : null;
    }

    public KeyStore getPreloadedKeyStore() {
        return preloadedKeyStore;
    }

    public char[] getPasswordChars() {
        return passwordChars != null ? passwordChars.clone() : null;
    }

    public boolean hasPreloadedKeyStore() {
        return preloadedKeyStore != null;
    }

    @Override
    public void close() {
        if (passwordChars != null) {
            Arrays.fill(passwordChars, ' ');
        }
        if (certificateBytes != null) {
            Arrays.fill(certificateBytes, (byte) 0);
        }
    }
}
