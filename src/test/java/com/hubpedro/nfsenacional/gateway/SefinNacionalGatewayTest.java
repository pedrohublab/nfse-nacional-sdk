package com.hubpedro.nfsenacional.gateway;

import com.hubpedro.nfsenacional.domain.exception.SefinTimeoutException;
import com.hubpedro.nfsenacional.gateway.dto.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.time.Duration;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes do SefinNacionalGateway com Mock HTTP Server")
public class SefinNacionalGatewayTest {

    private MockSefinServer mockServer;
    private SefinNacionalGateway gateway;

    @BeforeEach
    void setUp() {
        mockServer = new MockSefinServer();
        SefinNacionalConfig config = SefinNacionalConfig.custom(mockServer.getBaseUrl(), 2000, 2000);
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(2000)).build();
        gateway = new SefinNacionalGateway(config, client);
    }

    @AfterEach
    void tearDown() {
        mockServer.close();
    }

    @Test
    @DisplayName("Deve enviar DPS com sucesso via POST /dps")
    void deveEnviarDpsComSucesso() {
        SefinNacionalGateway.SefinResponse<SefinEmissaoResponseDTO> resp = gateway.enviarDps("H4sIAAAAAAAA/8vPAAAH7N/vAwAAAA==");

        assertThat(resp.httpStatus()).isEqualTo(200);
        assertThat(resp.parsed()).isNotNull();
        assertThat(resp.parsed().isSucesso()).isTrue();
        assertThat(resp.parsed().chaveAcesso()).isNotEmpty();
    }

    @Test
    @DisplayName("Deve consultar status da DPS via GET /dps/{chave}")
    void deveConsultarDps() {
        SefinNacionalGateway.SefinResponse<SefinConsultaDpsResponseDTO> resp = gateway.consultarDps("35503082608112223330001810000000000000000000000001");

        assertThat(resp.httpStatus()).isEqualTo(200);
        assertThat(resp.parsed()).isNotNull();
    }

    @Test
    @DisplayName("Deve checar processamento via HEAD /dps/{chave}")
    void deveChecarHeadDps() {
        boolean processada = gateway.verificarDpsProcessada("35503082608112223330001810000000000000000000000001");
        assertThat(processada).isTrue();
    }

    @Test
    @DisplayName("Deve consultar NFS-e via GET /nfse/{chave}")
    void deveConsultarNfse() {
        SefinNacionalGateway.SefinResponse<SefinConsultaNfseResponseDTO> resp = gateway.consultarNfse("35503082608112223330001810000000000000000000000001");

        assertThat(resp.httpStatus()).isEqualTo(200);
        assertThat(resp.parsed()).isNotNull();
        assertThat(resp.parsed().numeroNFSe()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve baixar PDF do DANFSE via GET /nfse/{chave}/danfse")
    void deveBaixarDanfse() {
        SefinNacionalGateway.SefinResponse<byte[]> resp = gateway.downloadDanfse("35503082608112223330001810000000000000000000000001");

        assertThat(resp.httpStatus()).isEqualTo(200);
        assertThat(resp.parsed()).isNotEmpty();
        assertThat(new String(resp.parsed())).contains("%PDF");
    }

    @Test
    @DisplayName("Deve registrar evento via POST /nfse/{chave}/eventos")
    void deveRegistrarEvento() {
        SefinNacionalGateway.SefinResponse<SefinEventoResponseDTO> resp = gateway.registrarEvento("35503082608112223330001810000000000000000000000001", "H4sIAAAAAAAA/8vPAAAH7N/vAwAAAA==");

        assertThat(resp.httpStatus()).isEqualTo(200);
        assertThat(resp.parsed()).isNotNull();
        assertThat(resp.parsed().isSucesso()).isTrue();
    }

    @Test
    @DisplayName("Deve lançar SefinTimeoutException quando ocorrer timeout")
    void deveLancarTimeoutException() {
        mockServer.setDelayMs(500);
        SefinNacionalConfig timeoutConfig = SefinNacionalConfig.custom(mockServer.getBaseUrl(), 100, 100);
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(100)).build();
        SefinNacionalGateway timeoutGateway = new SefinNacionalGateway(timeoutConfig, client);

        assertThatThrownBy(() -> timeoutGateway.enviarDps("H4sIAAAAAAAA/8vPAAAH7N/vAwAAAA=="))
                .isInstanceOf(SefinTimeoutException.class);
    }
}
