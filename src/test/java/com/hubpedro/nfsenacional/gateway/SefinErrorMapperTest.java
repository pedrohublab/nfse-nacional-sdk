package com.hubpedro.nfsenacional.gateway;

import com.hubpedro.nfsenacional.domain.enums.DpsEmissionStatus;
import com.hubpedro.nfsenacional.gateway.dto.SefinEmissaoResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes do SefinErrorMapper")
class SefinErrorMapperTest {

    @Test
    @DisplayName("Deve retornar AUTHORIZED quando httpStatus for 200 e dto for null")
    void deveRetornarAuthorizedQuandoHttpStatus200EDtoNull() {
        DpsEmissionStatus status = SefinErrorMapper.mapearStatus(200, null);
        assertThat(status).isEqualTo(DpsEmissionStatus.AUTHORIZED);
    }

    @Test
    @DisplayName("Deve retornar AUTHORIZED quando httpStatus 200 e dto.codigoStatus for null")
    void deveRetornarAuthorizedQuandoHttpStatus200ECodigoStatusNull() {
        SefinEmissaoResponseDTO dto = new SefinEmissaoResponseDTO(
                "123", "456", 1L, "1", "123", "2023-01-01", null, null, null, null
        );
        DpsEmissionStatus status = SefinErrorMapper.mapearStatus(200, dto);
        assertThat(status).isEqualTo(DpsEmissionStatus.AUTHORIZED);
    }

    @Test
    @DisplayName("Deve retornar AUTHORIZED quando codigoStatus for de autorizacao (100)")
    void deveRetornarAuthorizedQuandoCodigoAutorizado() {
        SefinEmissaoResponseDTO dto = new SefinEmissaoResponseDTO(
                "123", "456", 1L, "1", "123", "2023-01-01", "100", "Autorizado", null, null
        );
        DpsEmissionStatus status = SefinErrorMapper.mapearStatus(200, dto);
        assertThat(status).isEqualTo(DpsEmissionStatus.AUTHORIZED);
    }

    @Test
    @DisplayName("Deve retornar REJECTED quando codigoStatus for de duplicidade (204)")
    void deveRetornarRejectedQuandoCodigoDuplicidade() {
        SefinEmissaoResponseDTO dto = new SefinEmissaoResponseDTO(
                "123", "456", 1L, "1", null, "2023-01-01", "204", "Duplicidade", null, null
        );
        DpsEmissionStatus status = SefinErrorMapper.mapearStatus(200, dto);
        assertThat(status).isEqualTo(DpsEmissionStatus.REJECTED);
    }

    @Test
    @DisplayName("Deve retornar REJECTED quando codigoStatus for de erro de validacao (501)")
    void deveRetornarRejectedQuandoCodigoValidacaoSchema() {
        SefinEmissaoResponseDTO dto = new SefinEmissaoResponseDTO(
                null, null, null, null, null, null, "501", "Erro schema", null, null
        );
        DpsEmissionStatus status = SefinErrorMapper.mapearStatus(200, dto);
        assertThat(status).isEqualTo(DpsEmissionStatus.REJECTED);
    }

    @Test
    @DisplayName("Deve retornar TIMEOUT quando httpStatus for 504")
    void deveRetornarTimeoutQuandoHttpStatus504() {
        DpsEmissionStatus status = SefinErrorMapper.mapearStatus(504, null);
        assertThat(status).isEqualTo(DpsEmissionStatus.TIMEOUT);
    }

    @Test
    @DisplayName("Deve retornar UNKNOWN quando httpStatus for 500")
    void deveRetornarUnknownQuandoHttpStatus500() {
        DpsEmissionStatus status = SefinErrorMapper.mapearStatus(500, null);
        assertThat(status).isEqualTo(DpsEmissionStatus.UNKNOWN);
    }

    @Test
    @DisplayName("Deve retornar REJECTED para outros status HTTP (ex: 400)")
    void deveRetornarRejectedParaOutrosStatusHttp() {
        DpsEmissionStatus status = SefinErrorMapper.mapearStatus(400, null);
        assertThat(status).isEqualTo(DpsEmissionStatus.REJECTED);
    }
}
