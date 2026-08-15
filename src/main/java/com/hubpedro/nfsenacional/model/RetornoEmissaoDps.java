package com.hubpedro.nfsenacional.model;

import com.hubpedro.nfsenacional.domain.enums.DpsEmissionStatus;

/**
 * Resultado da emissão de DPS retornado pela fachada pública do SDK.
 */
public record RetornoEmissaoDps(
        boolean sucesso,
        String chaveDPS,
        String chaveAcesso,
        String protocolo,
        DpsEmissionStatus status,
        String xmlAssinado,
        String xmlAutorizado,
        String mensagemErro,
        String codigoErro,
        String rawResponseBody,
        int httpStatus,
        long tempoRespostaMs
) {

    public static RetornoEmissaoDps sucesso(String chaveDPS, String chaveAcesso, String protocolo,
                                            String xmlAssinado, String xmlAutorizado,
                                            String rawResponseBody, int httpStatus, long tempoRespostaMs) {
        return new RetornoEmissaoDps(
                true, chaveDPS, chaveAcesso, protocolo, DpsEmissionStatus.AUTHORIZED,
                xmlAssinado, xmlAutorizado, null, null,
                rawResponseBody, httpStatus, tempoRespostaMs
        );
    }

    public static RetornoEmissaoDps rejeitado(String chaveDPS, String mensagemErro, String codigoErro,
                                              String xmlAssinado, String rawResponseBody,
                                              int httpStatus, long tempoRespostaMs) {
        return new RetornoEmissaoDps(
                false, chaveDPS, null, null, DpsEmissionStatus.REJECTED,
                xmlAssinado, null, mensagemErro, codigoErro,
                rawResponseBody, httpStatus, tempoRespostaMs
        );
    }

    public static RetornoEmissaoDps indeterminado(String chaveDPS, String mensagemErro, String xmlAssinado,
                                                  int httpStatus, long tempoRespostaMs) {
        return new RetornoEmissaoDps(
                false, chaveDPS, null, null, DpsEmissionStatus.UNKNOWN,
                xmlAssinado, null, mensagemErro, "TIMEOUT_OU_INDETERMINADO",
                null, httpStatus, tempoRespostaMs
        );
    }

    public static RetornoEmissaoDps erro(String chaveDPS, String mensagemErro, String codigoErro,
                                         String xmlAssinado, int httpStatus, long tempoRespostaMs) {
        return new RetornoEmissaoDps(
                false, chaveDPS, null, null, DpsEmissionStatus.REJECTED,
                xmlAssinado, null, mensagemErro, codigoErro,
                null, httpStatus, tempoRespostaMs
        );
    }
}
