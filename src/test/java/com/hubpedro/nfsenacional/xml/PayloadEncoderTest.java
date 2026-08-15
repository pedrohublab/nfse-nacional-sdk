package com.hubpedro.nfsenacional.xml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes de Compressão GZip e Codificação Base64")
public class PayloadEncoderTest {

    @Test
    @DisplayName("Deve compactar e descompactar XML com integridade total")
    void deveComprimirEDescomprimirComSucesso() {
        String xmlOriginal = "<DPS versao=\"1.00\"><infDPS Id=\"DPS3550308\"><conteudo>Teste de acentuação: Concluído & Aprovado</conteudo></infDPS></DPS>";

        String encoded = PayloadEncoder.encodeForSefin(xmlOriginal);
        assertThat(encoded).isNotNull().isNotBlank();

        String decoded = PayloadEncoder.decodeFromSefin(encoded);
        assertThat(decoded).isEqualTo(xmlOriginal);
    }

    @Test
    @DisplayName("Deve decodificar bytes binários (ex: PDF DANFSE)")
    void deveDecodificarBytesBinarios() {
        byte[] original = new byte[]{1, 2, 3, 4, 5, 10, 20, 30};
        byte[] gzipped = PayloadEncoder.gzip(original);
        String base64 = java.util.Base64.getEncoder().encodeToString(gzipped);

        byte[] decoded = PayloadEncoder.decodeBytesFromSefin(base64);
        assertThat(decoded).containsExactly(original);
    }
}
