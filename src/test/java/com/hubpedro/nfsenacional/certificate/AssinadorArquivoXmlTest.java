package com.hubpedro.nfsenacional.certificate;

import com.hubpedro.nfsenacional.domain.DPSTest;
import com.hubpedro.nfsenacional.domain.model.DPS;
import com.hubpedro.nfsenacional.util.TestCertificateGenerator;
import com.hubpedro.nfsenacional.xml.DpsXmlGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes do AssinadorArquivoXml e XMLDSig SHA-256")
public class AssinadorArquivoXmlTest {

    @Test
    @DisplayName("Deve assinar XML da DPS e produzir assinatura digital XMLDSig válida")
    void deveAssinarXmlDpsComSucesso() throws Exception {
        byte[] pfxBytes = TestCertificateGenerator.generateTestPkcs12Bytes("11222333000181", "123456");
        CertificateService certService = CertificateService.fromBytes(pfxBytes, "123456");

        DPS dps = DPSTest.criarDpsValida();
        DpsXmlGenerator generator = new DpsXmlGenerator();
        String xml = generator.gerar(dps);

        String xmlAssinado = certService.signXmlString(xml);

        assertThat(xmlAssinado).isNotNull();
        assertThat(xmlAssinado).contains("<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">");
        assertThat(xmlAssinado).contains("<DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/>");
        assertThat(xmlAssinado).contains("<SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\"/>");
        assertThat(xmlAssinado).contains("<Reference URI=\"#" + dps.getChaveDPS().getIdXml() + "\">");

        // Validação criptográfica da assinatura gerada
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xmlAssinado)));

        NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        assertThat(nl.getLength()).isEqualTo(1);

        DOMValidateContext valContext = new DOMValidateContext(certService.getCertificate().getPublicKey(), nl.item(0));
        // Registrar ID para validação
        org.w3c.dom.Element infDps = (org.w3c.dom.Element) doc.getElementsByTagName("infDPS").item(0);
        valContext.setIdAttributeNS(infDps, null, "Id");

        XMLSignatureFactory sigFactory = XMLSignatureFactory.getInstance("DOM");
        XMLSignature signature = sigFactory.unmarshalXMLSignature(valContext);

        boolean coreValidity = signature.validate(valContext);
        assertThat(coreValidity).isTrue();
    }
}
