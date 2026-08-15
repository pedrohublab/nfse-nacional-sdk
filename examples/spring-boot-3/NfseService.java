package com.hubpedro.nfsenacional.examples.spring;

import com.hubpedro.nfsenacional.NFSeNacionalClient;
import com.hubpedro.nfsenacional.domain.model.DPS;
import com.hubpedro.nfsenacional.model.DanfseDocument;
import com.hubpedro.nfsenacional.model.RetornoEmissaoDps;
import com.hubpedro.nfsenacional.model.RetornoEventoNfse;

/**
 * Exemplo de Service para aplicações Spring Boot 3 / Jakarta EE.
 * O cliente é gerenciado como recurso seguro sem persistência em disco.
 */
public class NfseService {

    public RetornoEmissaoDps emitirNotaFiscal(DPS dps, byte[] certBytes, String certPassword, boolean isProducao) {
        NFSeNacionalClient.Environment env = isProducao
                ? NFSeNacionalClient.Environment.PRODUCAO
                : NFSeNacionalClient.Environment.HOMOLOGACAO;

        try (NFSeNacionalClient client = NFSeNacionalClient.builder()
                .withCertificate(certBytes, certPassword)
                .withEnvironment(env)
                .build()) {

            return client.emitir(dps);
        } catch (Exception e) {
            throw new RuntimeException("Falha na emissão da NFS-e: " + e.getMessage(), e);
        }
    }

    public byte[] obterPdfDanfse(String chaveAcesso, byte[] certBytes, String certPassword) {
        try (NFSeNacionalClient client = NFSeNacionalClient.builder()
                .withCertificate(certBytes, certPassword)
                .build()) {

            DanfseDocument danfse = client.downloadDanfse(chaveAcesso);
            return danfse.pdfBytes();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao baixar DANFSE: " + e.getMessage(), e);
        }
    }

    public RetornoEventoNfse cancelarNotaFiscal(String chaveAcesso, String motivo, byte[] certBytes, String certPassword) {
        try (NFSeNacionalClient client = NFSeNacionalClient.builder()
                .withCertificate(certBytes, certPassword)
                .build()) {

            return client.cancelarNfse(chaveAcesso, motivo, "1");
        } catch (Exception e) {
            throw new RuntimeException("Falha ao cancelar NFS-e: " + e.getMessage(), e);
        }
    }
}
