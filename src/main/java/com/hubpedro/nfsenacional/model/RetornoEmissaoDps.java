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
    /**
     * Salva o XML em disco organizado automaticamente em subpastas ano/mes/dia/{chave}.xml.
     * Salva preferencialmente o xmlAutorizado (se disponível) ou o xmlAssinado.
     *
     * @param baseDir diretório raiz de armazenamento (ex: Path.of("/dados/xmls"))
     * @return Path do arquivo salvo
     * @throws java.io.IOException em caso de erro de gravação
     */
    public java.nio.file.Path salvarXml(java.nio.file.Path baseDir) throws java.io.IOException {
        return salvarXml(baseDir, java.time.LocalDate.now());
    }

    /**
     * Salva o XML em disco organizado em subpastas ano/mes/dia/{chave}.xml usando data customizada.
     */
    public java.nio.file.Path salvarXml(java.nio.file.Path baseDir, java.time.LocalDate data) throws java.io.IOException {
        String conteudo = xmlAutorizado != null ? xmlAutorizado : xmlAssinado;
        if (conteudo == null) {
            throw new IllegalStateException("Nenhum conteúdo XML disponível para salvar nesta emissão.");
        }
        String nomeBase = chaveAcesso != null ? chaveAcesso : (chaveDPS != null ? chaveDPS + "_dps" : "nfse_" + System.currentTimeMillis());
        return com.hubpedro.nfsenacional.storage.XmlStorageHelper.salvarXml(baseDir, nomeBase, conteudo, data);
    }
}
