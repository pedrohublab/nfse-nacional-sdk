package com.hubpedro.nfsenacional.examples.quarkus;

import com.hubpedro.nfsenacional.NFSeNacionalClient;
import com.hubpedro.nfsenacional.model.DanfseDocument;
import com.hubpedro.nfsenacional.model.RetornoConsultaNfse;

/**
 * Exemplo de Endpoint / Resource Quarkus ou Micronaut para consulta e download.
 */
public class NfseResource {

    public byte[] downloadDanfsePdf(String chaveAcesso, byte[] pfxBytes, String senha) {
        try (NFSeNacionalClient client = NFSeNacionalClient.builder()
                .withCertificate(pfxBytes, senha)
                .build()) {

            DanfseDocument doc = client.downloadDanfse(chaveAcesso);
            return doc.pdfBytes();
        }
    }

    public RetornoConsultaNfse consultarNfse(String chaveAcesso, byte[] pfxBytes, String senha) {
        try (NFSeNacionalClient client = NFSeNacionalClient.builder()
                .withCertificate(pfxBytes, senha)
                .build()) {

            return client.consultarNfse(chaveAcesso);
        }
    }
}
