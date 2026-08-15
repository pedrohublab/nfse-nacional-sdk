package com.hubpedro.nfsenacional;

import com.hubpedro.nfsenacional.certificate.CertificateMaterial;
import com.hubpedro.nfsenacional.certificate.CertificateService;
import com.hubpedro.nfsenacional.domain.exception.CertificateException;
import com.hubpedro.nfsenacional.gateway.SefinNacionalGateway;

/**
 * Fábrica estática para instanciação conveniente do NFSeNacionalClient.
 */
public final class NFSeNacionalClientFactory {

    private NFSeNacionalClientFactory() {
    }

    public static NFSeNacionalClient createHomologacao(byte[] pfxBytes, String senha) throws CertificateException {
        return NFSeNacionalClient.builder()
                .withCertificate(pfxBytes, senha)
                .withEnvironment(NFSeNacionalClient.Environment.HOMOLOGACAO)
                .build();
    }

    public static NFSeNacionalClient createProducao(byte[] pfxBytes, String senha) throws CertificateException {
        return NFSeNacionalClient.builder()
                .withCertificate(pfxBytes, senha)
                .withEnvironment(NFSeNacionalClient.Environment.PRODUCAO)
                .build();
    }

    public static NFSeNacionalClient create(CertificateService certificateService, NFSeNacionalClient.Environment env) {
        return new NFSeNacionalClient(certificateService, null, env);
    }
}
