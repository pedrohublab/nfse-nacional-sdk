import re

content = """package com.hubpedro.nfsenacional.gateway;

import com.hubpedro.nfsenacional.domain.enums.DpsEmissionStatus;
import com.hubpedro.nfsenacional.domain.exception.*;
import com.hubpedro.nfsenacional.gateway.dto.SefinEmissaoResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class SefinErrorMapperTest {

    @Test
    void mapearStatus_WhenCodigo100_ShouldReturnAuthorized() {
        SefinEmissaoResponseDTO dto = new SefinEmissaoResponseDTO(
                null, null, null, null, null, null, "100", null, null, null
        );
        assertEquals(DpsEmissionStatus.AUTHORIZED, SefinErrorMapper.mapearStatus(200, dto));
    }

    @Test
    void mapearStatus_WhenProtocoloIsPresent_ShouldReturnAuthorized() {
        SefinEmissaoResponseDTO dto = new SefinEmissaoResponseDTO(
                null, null, null, null, "PROT123", null, null, null, null, null
        );
        assertEquals(DpsEmissionStatus.AUTHORIZED, SefinErrorMapper.mapearStatus(200, dto));
    }

    @Test
    void mapearStatus_WhenCodigo204_ShouldReturnRejected() {
        SefinEmissaoResponseDTO dto = new SefinEmissaoResponseDTO(
                null, null, null, null, null, null, "204", null, null, null
        );
        assertEquals(DpsEmissionStatus.REJECTED, SefinErrorMapper.mapearStatus(200, dto));
    }

    @Test
    void mapearStatus_WhenCodigo501_ShouldReturnRejected() {
        SefinEmissaoResponseDTO dto = new SefinEmissaoResponseDTO(
                null, null, null, null, null, null, "501", null, null, null
        );
        assertEquals(DpsEmissionStatus.REJECTED, SefinErrorMapper.mapearStatus(200, dto));
    }

    @Test
    void mapearStatus_WhenDtoIsNull_ShouldReturnAuthorized() {
        assertEquals(DpsEmissionStatus.AUTHORIZED, SefinErrorMapper.mapearStatus(200, null));
    }

    @Test
    void mapearStatus_WhenCodigoIsUnknown_ShouldReturnAuthorized() {
        SefinEmissaoResponseDTO dto = new SefinEmissaoResponseDTO(
                null, null, null, null, null, null, "999", null, null, null
        );
        assertEquals(DpsEmissionStatus.AUTHORIZED, SefinErrorMapper.mapearStatus(200, dto));
    }

    @Test
    void mapearStatus_WhenHttpStatus504_ShouldReturnTimeout() {
        assertEquals(DpsEmissionStatus.TIMEOUT, SefinErrorMapper.mapearStatus(504, null));
    }

    @Test
    void mapearStatus_WhenHttpStatus500_ShouldReturnUnknown() {
        assertEquals(DpsEmissionStatus.UNKNOWN, SefinErrorMapper.mapearStatus(500, null));
    }

    @Test
    void mapearStatus_WhenHttpStatus400_ShouldReturnRejected() {
        assertEquals(DpsEmissionStatus.REJECTED, SefinErrorMapper.mapearStatus(400, null));
    }

    @Test
    void mapearExcecao_WhenCodigo204_ShouldReturnSefinDuplicatedEmissionException() {
        SefinEmissaoResponseDTO dto = new SefinEmissaoResponseDTO(
                null, null, null, null, null, null, "204", "Duplicidade", null, null
        );
        SefinApiException exception = SefinErrorMapper.mapearExcecao(400, dto, "body");
        assertInstanceOf(SefinDuplicatedEmissionException.class, exception);
        assertEquals("204", exception.getCodigoError());
        assertEquals(400, exception.getHttpStatus());
    }

    @Test
    void mapearExcecao_WhenCodigo501_ShouldReturnSefinValidationException() {
        SefinEmissaoResponseDTO dto = new SefinEmissaoResponseDTO(
                null, null, null, null, null, null, "501", "Erro Schema", null, null
        );
        SefinApiException exception = SefinErrorMapper.mapearExcecao(400, dto, "body");
        assertInstanceOf(SefinValidationException.class, exception);
        assertEquals("501", exception.getCodigoError());
    }

    @Test
    void mapearExcecao_WhenCodigo500_ShouldReturnSefinUnavailableException() {
        SefinEmissaoResponseDTO dto = new SefinEmissaoResponseDTO(
                null, null, null, null, null, null, "500", "Indisponivel", null, null
        );
        SefinApiException exception = SefinErrorMapper.mapearExcecao(500, dto, "body");
        assertInstanceOf(SefinUnavailableException.class, exception);
        assertEquals("500", exception.getCodigoError());
    }

    @Test
    void mapearExcecao_WhenHttpStatus504_ShouldReturnSefinTimeoutException() {
        SefinApiException exception = SefinErrorMapper.mapearExcecao(504, null, "timeout body");
        assertInstanceOf(SefinTimeoutException.class, exception);
        assertEquals("Timeout de comunicação com o gateway SEFIN (HTTP 504)", exception.getMessage());
    }

    @Test
    void mapearExcecao_WhenHttpStatus502_ShouldReturnSefinUnavailableException() {
        SefinApiException exception = SefinErrorMapper.mapearExcecao(502, null, "Bad Gateway");
        assertInstanceOf(SefinUnavailableException.class, exception);
        assertTrue(exception.getMessage().contains("Bad Gateway"));
    }

    @ParameterizedTest
    @CsvSource({"400", "422"})
    void mapearExcecao_WhenHttpStatusIsValidation_ShouldReturnSefinValidationException(int httpStatus) {
        SefinApiException exception = SefinErrorMapper.mapearExcecao(httpStatus, null, "Validation Error");
        assertInstanceOf(SefinValidationException.class, exception);
        assertEquals(httpStatus, exception.getHttpStatus());
    }

    @Test
    void mapearExcecao_WhenOtherHttpStatus_ShouldReturnSefinApiException() {
        SefinApiException exception = SefinErrorMapper.mapearExcecao(403, null, "Forbidden");
        assertInstanceOf(SefinApiException.class, exception);
        assertEquals(403, exception.getHttpStatus());
        assertEquals("Forbidden", exception.getMessage());
    }

    @Test
    void mapearExcecao_WhenNullDescription_ShouldGenerateDefaultDescription() {
        SefinApiException exception = SefinErrorMapper.mapearExcecao(404, null, "");
        assertInstanceOf(SefinApiException.class, exception);
        assertEquals("HTTP Status 404", exception.getMessage());
    }
}
"""

with open("src/test/java/com/hubpedro/nfsenacional/gateway/SefinErrorMapperTest.java", "w") as f:
    f.write(content)
