package com.hubpedro.nfsenacional.gateway;

import com.hubpedro.nfsenacional.domain.enums.DpsEmissionStatus;
import com.hubpedro.nfsenacional.domain.exception.*;
import com.hubpedro.nfsenacional.gateway.dto.SefinEmissaoResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class SefinErrorMapperTest {

    @Test
    void mapearStatus_HttpStatus200_WithSuccessCode_ShouldReturnAuthorized() {
        SefinEmissaoResponseDTO dto = new SefinEmissaoResponseDTO(
                "chave", "dps", 1L, "1", "prot", "dh", "100", "Autorizado", Collections.emptyList(), "xml"
        );
        DpsEmissionStatus status = SefinErrorMapper.mapearStatus(200, dto);
        assertEquals(DpsEmissionStatus.AUTHORIZED, status);
    }

    @Test
    void mapearStatus_HttpStatus200_WithDuplicidadeCode_ShouldReturnRejected() {
        SefinEmissaoResponseDTO dto = new SefinEmissaoResponseDTO(
                "chave", "dps", 1L, "1", "", "dh", "204", "Duplicidade", Collections.emptyList(), "xml"
        );
        DpsEmissionStatus status = SefinErrorMapper.mapearStatus(200, dto);
        assertEquals(DpsEmissionStatus.REJECTED, status);
    }

    @Test
    void mapearStatus_HttpStatus200_WithErroSchemaCode_ShouldReturnRejected() {
        SefinEmissaoResponseDTO dto = new SefinEmissaoResponseDTO(
                "chave", "dps", 1L, "1", "", "dh", "501", "Erro schema", Collections.emptyList(), "xml"
        );
        DpsEmissionStatus status = SefinErrorMapper.mapearStatus(200, dto);
        assertEquals(DpsEmissionStatus.REJECTED, status);
    }

    @Test
    void mapearStatus_HttpStatus200_WithNullDto_ShouldReturnAuthorized() {
        DpsEmissionStatus status = SefinErrorMapper.mapearStatus(200, null);
        assertEquals(DpsEmissionStatus.AUTHORIZED, status);
    }

    @Test
    void mapearStatus_HttpStatus200_WithUnknownCode_ShouldReturnAuthorized() {
        SefinEmissaoResponseDTO dto = new SefinEmissaoResponseDTO(
                "chave", "dps", 1L, "1", "prot", "dh", "999", "Outro", Collections.emptyList(), "xml"
        );
        DpsEmissionStatus status = SefinErrorMapper.mapearStatus(200, dto);
        assertEquals(DpsEmissionStatus.AUTHORIZED, status);
    }

    @Test
    void mapearStatus_HttpStatus504_ShouldReturnTimeout() {
        DpsEmissionStatus status = SefinErrorMapper.mapearStatus(504, null);
        assertEquals(DpsEmissionStatus.TIMEOUT, status);
    }

    @Test
    void mapearStatus_HttpStatus500_ShouldReturnUnknown() {
        DpsEmissionStatus status = SefinErrorMapper.mapearStatus(500, null);
        assertEquals(DpsEmissionStatus.UNKNOWN, status);
    }

    @Test
    void mapearStatus_HttpStatus400_ShouldReturnRejected() {
        DpsEmissionStatus status = SefinErrorMapper.mapearStatus(400, null);
        assertEquals(DpsEmissionStatus.REJECTED, status);
    }

    @Test
    void mapearExcecao_Duplicidade_ShouldReturnSefinDuplicatedEmissionException() {
        SefinEmissaoResponseDTO dto = new SefinEmissaoResponseDTO(
                null, null, null, null, null, null, "204", "Duplicidade", null, null
        );
        SefinApiException exception = SefinErrorMapper.mapearExcecao(400, dto, "body");
        assertInstanceOf(SefinDuplicatedEmissionException.class, exception);
        assertEquals("204", exception.getCodigoError());
        assertEquals(400, exception.getHttpStatus());
    }

    @Test
    void mapearExcecao_ErroValidacaoSchema_ShouldReturnSefinValidationException() {
        SefinEmissaoResponseDTO dto = new SefinEmissaoResponseDTO(
                null, null, null, null, null, null, "501", "Erro Schema", null, null
        );
        SefinApiException exception = SefinErrorMapper.mapearExcecao(400, dto, "body");
        assertInstanceOf(SefinValidationException.class, exception);
        assertEquals("501", exception.getCodigoError());
    }

    @Test
    void mapearExcecao_ServicoIndisponivel_ShouldReturnSefinUnavailableException() {
        SefinEmissaoResponseDTO dto = new SefinEmissaoResponseDTO(
                null, null, null, null, null, null, "500", "Indisponivel", null, null
        );
        SefinApiException exception = SefinErrorMapper.mapearExcecao(500, dto, "body");
        assertInstanceOf(SefinUnavailableException.class, exception);
        assertEquals("500", exception.getCodigoError());
    }

    @Test
    void mapearExcecao_Timeout_ShouldReturnSefinTimeoutException() {
        SefinApiException exception = SefinErrorMapper.mapearExcecao(504, null, "timeout body");
        assertInstanceOf(SefinTimeoutException.class, exception);
        assertEquals("Timeout de comunicação com o gateway SEFIN (HTTP 504)", exception.getMessage());
    }

    @Test
    void mapearExcecao_ServerError_ShouldReturnSefinUnavailableException() {
        SefinApiException exception = SefinErrorMapper.mapearExcecao(502, null, "Bad Gateway");
        assertInstanceOf(SefinUnavailableException.class, exception);
        assertTrue(exception.getMessage().contains("Bad Gateway"));
    }

    @ParameterizedTest
    @CsvSource({"400", "422"})
    void mapearExcecao_ValidationError_ShouldReturnSefinValidationException(int httpStatus) {
        SefinApiException exception = SefinErrorMapper.mapearExcecao(httpStatus, null, "Validation Error");
        assertInstanceOf(SefinValidationException.class, exception);
        assertEquals(httpStatus, exception.getHttpStatus());
    }

    @Test
    void mapearExcecao_OtherError_ShouldReturnSefinApiException() {
        SefinApiException exception = SefinErrorMapper.mapearExcecao(403, null, "Forbidden");
        assertInstanceOf(SefinApiException.class, exception);
        assertEquals(403, exception.getHttpStatus());
        assertEquals("Forbidden", exception.getMessage());
    }

    @Test
    void mapearExcecao_NullDescription_ShouldGenerateDefaultDescription() {
        SefinApiException exception = SefinErrorMapper.mapearExcecao(404, null, "");
        assertInstanceOf(SefinApiException.class, exception);
        assertEquals("HTTP Status 404", exception.getMessage());
    }
}
