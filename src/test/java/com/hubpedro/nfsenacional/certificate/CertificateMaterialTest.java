package com.hubpedro.nfsenacional.certificate;

import com.hubpedro.nfsenacional.domain.exception.CertificateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("Testes do CertificateMaterial")
class CertificateMaterialTest {

    @Test
    @DisplayName("Deve criar a partir de bytes com senha String")
    void deveCriarAPartirDeBytesComSenhaString() {
        byte[] bytes = new byte[]{1, 2, 3};
        String password = "senha";

        CertificateMaterial material = CertificateMaterial.fromBytes(bytes, password);

        assertThat(material.getCertificateBytes()).containsExactly(1, 2, 3);
        assertThat(material.getPasswordChars()).containsExactly('s', 'e', 'n', 'h', 'a');
        assertThat(material.hasPreloadedKeyStore()).isFalse();
        assertThat(material.getPreloadedKeyStore()).isNull();
    }

    @Test
    @DisplayName("Deve criar a partir de bytes com senha char[]")
    void deveCriarAPartirDeBytesComSenhaCharArray() {
        byte[] bytes = new byte[]{1, 2, 3};
        char[] password = new char[]{'s', 'e', 'n', 'h', 'a'};

        CertificateMaterial material = CertificateMaterial.fromBytes(bytes, password);

        assertThat(material.getCertificateBytes()).containsExactly(1, 2, 3);
        assertThat(material.getPasswordChars()).containsExactly('s', 'e', 'n', 'h', 'a');
        assertThat(material.hasPreloadedKeyStore()).isFalse();
        assertThat(material.getPreloadedKeyStore()).isNull();
    }

    @Test
    @DisplayName("Deve lançar NullPointerException se bytes forem nulos em fromBytes com String")
    void deveLancarExcecaoSeBytesNulosFromBytesString() {
        assertThatThrownBy(() -> CertificateMaterial.fromBytes(null, "senha"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Bytes do certificado não podem ser nulos");
    }

    @Test
    @DisplayName("Deve lançar NullPointerException se bytes forem nulos em fromBytes com char[]")
    void deveLancarExcecaoSeBytesNulosFromBytesCharArray() {
        assertThatThrownBy(() -> CertificateMaterial.fromBytes(null, new char[]{'a'}))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Bytes do certificado não podem ser nulos");
    }

    @Test
    @DisplayName("Deve criar a partir de InputStream com senha String")
    void deveCriarAPartirDeInputStreamComSenhaString() throws Exception {
        byte[] bytes = new byte[]{4, 5, 6};
        InputStream inputStream = new ByteArrayInputStream(bytes);
        String password = "senha";

        CertificateMaterial material = CertificateMaterial.fromStream(inputStream, password);

        assertThat(material.getCertificateBytes()).containsExactly(4, 5, 6);
        assertThat(material.getPasswordChars()).containsExactly('s', 'e', 'n', 'h', 'a');
        assertThat(material.hasPreloadedKeyStore()).isFalse();
    }

    @Test
    @DisplayName("Deve criar a partir de InputStream com senha char[]")
    void deveCriarAPartirDeInputStreamComSenhaCharArray() throws Exception {
        byte[] bytes = new byte[]{4, 5, 6};
        InputStream inputStream = new ByteArrayInputStream(bytes);
        char[] password = new char[]{'s', 'e', 'n', 'h', 'a'};

        CertificateMaterial material = CertificateMaterial.fromStream(inputStream, password);

        assertThat(material.getCertificateBytes()).containsExactly(4, 5, 6);
        assertThat(material.getPasswordChars()).containsExactly('s', 'e', 'n', 'h', 'a');
        assertThat(material.hasPreloadedKeyStore()).isFalse();
    }

    @Test
    @DisplayName("Deve lançar NullPointerException se InputStream for nulo")
    void deveLancarExcecaoSeInputStreamNulo() {
        assertThatThrownBy(() -> CertificateMaterial.fromStream(null, "senha"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Stream do certificado não pode ser nulo");
    }

    @Test
    @DisplayName("Deve lançar CertificateException se ocorrer erro ao ler InputStream")
    void deveLancarExcecaoSeErroAoLerInputStream() throws Exception {
        InputStream mockInputStream = mock(InputStream.class);
        when(mockInputStream.readAllBytes()).thenThrow(new IOException("Erro de I/O"));

        assertThatThrownBy(() -> CertificateMaterial.fromStream(mockInputStream, "senha"))
                .isInstanceOf(CertificateException.class)
                .hasMessage("Erro ao ler bytes do InputStream do certificado")
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    @DisplayName("Deve criar a partir de KeyStore com senha String")
    void deveCriarAPartirDeKeyStoreComSenhaString() {
        KeyStore keyStore = mock(KeyStore.class);
        String password = "senha";

        CertificateMaterial material = CertificateMaterial.fromKeyStore(keyStore, password);

        assertThat(material.getCertificateBytes()).isNull();
        assertThat(material.getPasswordChars()).containsExactly('s', 'e', 'n', 'h', 'a');
        assertThat(material.hasPreloadedKeyStore()).isTrue();
        assertThat(material.getPreloadedKeyStore()).isSameAs(keyStore);
    }

    @Test
    @DisplayName("Deve criar a partir de KeyStore com senha char[]")
    void deveCriarAPartirDeKeyStoreComSenhaCharArray() {
        KeyStore keyStore = mock(KeyStore.class);
        char[] password = new char[]{'s', 'e', 'n', 'h', 'a'};

        CertificateMaterial material = CertificateMaterial.fromKeyStore(keyStore, password);

        assertThat(material.getCertificateBytes()).isNull();
        assertThat(material.getPasswordChars()).containsExactly('s', 'e', 'n', 'h', 'a');
        assertThat(material.hasPreloadedKeyStore()).isTrue();
        assertThat(material.getPreloadedKeyStore()).isSameAs(keyStore);
    }

    @Test
    @DisplayName("Deve lançar NullPointerException se KeyStore for nulo em fromKeyStore com String")
    void deveLancarExcecaoSeKeyStoreNuloFromKeyStoreString() {
        assertThatThrownBy(() -> CertificateMaterial.fromKeyStore(null, "senha"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("KeyStore não pode ser nulo");
    }

    @Test
    @DisplayName("Deve lançar NullPointerException se KeyStore for nulo em fromKeyStore com char[]")
    void deveLancarExcecaoSeKeyStoreNuloFromKeyStoreCharArray() {
        assertThatThrownBy(() -> CertificateMaterial.fromKeyStore(null, new char[]{'a'}))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("KeyStore não pode ser nulo");
    }

    @Test
    @DisplayName("openStream deve retornar InputStream correto se bytes estiverem presentes")
    void openStreamDeveRetornarInputStreamCorreto() throws IOException {
        byte[] bytes = new byte[]{10, 20, 30};
        CertificateMaterial material = CertificateMaterial.fromBytes(bytes, "senha");

        try (InputStream stream = material.openStream()) {
            assertThat(stream).isNotNull();
            assertThat(stream.readAllBytes()).containsExactly(10, 20, 30);
        }
    }

    @Test
    @DisplayName("openStream deve retornar nulo se bytes não estiverem presentes")
    void openStreamDeveRetornarNuloSeSemBytes() {
        KeyStore keyStore = mock(KeyStore.class);
        CertificateMaterial material = CertificateMaterial.fromKeyStore(keyStore, "senha");

        assertThat(material.openStream()).isNull();
    }

    @Test
    @DisplayName("getCertificateBytes deve retornar um clone e não vazar a referência interna")
    void getCertificateBytesDeveRetornarClone() {
        byte[] originalBytes = new byte[]{1, 2, 3};
        CertificateMaterial material = CertificateMaterial.fromBytes(originalBytes, "senha");

        byte[] returnedBytes = material.getCertificateBytes();
        assertThat(returnedBytes).containsExactly(1, 2, 3);

        // Modifica os bytes originais e os retornados, não deve afetar internamente
        originalBytes[0] = 99;
        returnedBytes[0] = 88;

        assertThat(material.getCertificateBytes()).containsExactly(1, 2, 3);
    }

    @Test
    @DisplayName("getPasswordChars deve retornar um clone e não vazar a referência interna")
    void getPasswordCharsDeveRetornarClone() {
        char[] originalPassword = new char[]{'a', 'b', 'c'};
        CertificateMaterial material = CertificateMaterial.fromBytes(new byte[]{1}, originalPassword);

        char[] returnedPassword = material.getPasswordChars();
        assertThat(returnedPassword).containsExactly('a', 'b', 'c');

        // Modifica a senha original e a retornada, não deve afetar internamente
        originalPassword[0] = 'z';
        returnedPassword[0] = 'y';

        assertThat(material.getPasswordChars()).containsExactly('a', 'b', 'c');
    }

    @Test
    @DisplayName("hasPreloadedKeyStore deve retornar corretamente")
    void hasPreloadedKeyStoreDeveRetornarCorretamente() {
        CertificateMaterial materialComBytes = CertificateMaterial.fromBytes(new byte[]{1}, "senha");
        assertThat(materialComBytes.hasPreloadedKeyStore()).isFalse();

        KeyStore keyStore = mock(KeyStore.class);
        CertificateMaterial materialComKeystore = CertificateMaterial.fromKeyStore(keyStore, "senha");
        assertThat(materialComKeystore.hasPreloadedKeyStore()).isTrue();
    }

    @Test
    @DisplayName("close deve limpar arrays de memória (bytes e senha)")
    void closeDeveLimparArraysDeMemoria() {
        byte[] bytes = new byte[]{1, 2, 3};
        char[] password = new char[]{'s', 'e', 'n', 'h', 'a'};

        CertificateMaterial material = CertificateMaterial.fromBytes(bytes, password);

        // Referências para os clones internos para verificar se foram preenchidos
        byte[] internalBytes = material.getCertificateBytes();
        char[] internalPassword = material.getPasswordChars();

        material.close();

        // O close() limpa os arrays internos originais que estão no objeto CertificateMaterial.
        // Já que ele expõe o clone através do get() (e não o array em si), não conseguimos
        // ver diretamente o preenchimento se pegarmos os clones.
        // Contudo, se chamarmos os métodos após o close(), devemos ver o clone contendo tudo nulo/limpo.

        byte[] bytesPosClose = material.getCertificateBytes();
        assertThat(bytesPosClose).containsExactly(0, 0, 0);

        char[] passwordPosClose = material.getPasswordChars();
        assertThat(passwordPosClose).containsExactly(' ', ' ', ' ', ' ', ' ');
    }

    @Test
    @DisplayName("close não deve falhar se inicializado sem senha")
    void closeNaoDeveFalharSeSenhaNula() {
        byte[] bytes = new byte[]{1, 2, 3};
        CertificateMaterial material = CertificateMaterial.fromBytes(bytes, (String) null);

        material.close();

        byte[] bytesPosClose = material.getCertificateBytes();
        assertThat(bytesPosClose).containsExactly(0, 0, 0);
        assertThat(material.getPasswordChars()).isEmpty(); // Array vazio é o comportamento atual
    }

}
