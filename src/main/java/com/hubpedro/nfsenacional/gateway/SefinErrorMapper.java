package com.hubpedro.nfsenacional.gateway;

import com.hubpedro.nfsenacional.domain.enums.DpsEmissionStatus;
import com.hubpedro.nfsenacional.domain.enums.SefinErrorCode;
import com.hubpedro.nfsenacional.domain.exception.*;
import com.hubpedro.nfsenacional.gateway.dto.SefinEmissaoResponseDTO;

/**
 * Mapper estritamente tipado que converte status HTTP e códigos da SEFIN em
 * exceções de domínio semânticas e status normalizados.
 */
public final class SefinErrorMapper {

    private SefinErrorMapper() {
    }

    public static DpsEmissionStatus mapearStatus(int httpStatus, SefinEmissaoResponseDTO dto) {
        if (httpStatus >= 200 && httpStatus < 300) {
            if (dto != null && dto.codigoStatus() != null) {
                SefinErrorCode code = SefinErrorCode.fromCodigo(dto.codigoStatus());
                if (code == SefinErrorCode.AUTORIZADO || dto.isSucesso()) {
                    return DpsEmissionStatus.AUTHORIZED;
                }
                if (code == SefinErrorCode.DUPLICIDADE || code == SefinErrorCode.ERRO_VALIDACAO_SCHEMA) {
                    return DpsEmissionStatus.REJECTED;
                }
            }
            return DpsEmissionStatus.AUTHORIZED;
        }

        if (httpStatus == 504) {
            return DpsEmissionStatus.TIMEOUT;
        }

        if (httpStatus >= 500) {
            return DpsEmissionStatus.UNKNOWN;
        }

        return DpsEmissionStatus.REJECTED;
    }

    public static SefinApiException mapearExcecao(int httpStatus, SefinEmissaoResponseDTO dto, String responseBody) {
        String codigoStr = dto != null && dto.codigoStatus() != null ? dto.codigoStatus() : String.valueOf(httpStatus);
        String descricao = dto != null && dto.descricaoStatus() != null ? dto.descricaoStatus() : responseBody;
        if (descricao == null || descricao.isBlank()) {
            descricao = "HTTP Status " + httpStatus;
        }

        SefinErrorCode errorCode = SefinErrorCode.fromCodigo(codigoStr);

        return switch (errorCode) {
            case DUPLICIDADE -> new SefinDuplicatedEmissionException(descricao, codigoStr, httpStatus, responseBody);
            case ERRO_VALIDACAO_SCHEMA -> new SefinValidationException(descricao, codigoStr, httpStatus, responseBody);
            case SERVICO_INDISPONIVEL -> new SefinUnavailableException(descricao, codigoStr, httpStatus, responseBody);
            default -> {
                if (httpStatus == 504) {
                    yield new SefinTimeoutException("Timeout de comunicação com o gateway SEFIN (HTTP 504)");
                }
                if (httpStatus >= 500) {
                    yield new SefinUnavailableException("Serviço SEFIN indisponível: " + descricao, codigoStr, httpStatus, responseBody);
                }
                if (httpStatus == 400 || httpStatus == 422) {
                    yield new SefinValidationException(descricao, codigoStr, httpStatus, responseBody);
                }
                yield new SefinApiException(descricao, codigoStr, httpStatus, responseBody);
            }
        };
    }
}
