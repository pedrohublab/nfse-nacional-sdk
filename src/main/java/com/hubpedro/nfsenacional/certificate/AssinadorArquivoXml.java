package com.hubpedro.nfsenacional.certificate;

import com.hubpedro.nfsenacional.domain.exception.XmlSignatureException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Assinador digital XMLDSig no padrão ICP-Brasil e SEFIN Nacional / ADN.
 * Utiliza SHA-256 com RSA, canonicalização C14N e Enveloped Transform.
 */
public final class AssinadorArquivoXml {

    public static final String ALGORITMO_CANONIZACAO = CanonicalizationMethod.INCLUSIVE; // http://www.w3.org/TR/2001/REC-xml-c14n-20010315
    public static final String ALGORITMO_ASSINATURA = SignatureMethod.RSA_SHA256;      // http://www.w3.org/2001/04/xmldsig-more#rsa-sha256
    public static final String ALGORITMO_DIGEST = DigestMethod.SHA256;                  // http://www.w3.org/2001/04/xmlenc#sha256

    private static final DocumentBuilderFactory DBF;
    private static final TransformerFactory TF;
    private static final XMLSignatureFactory XML_SIG_FACTORY;

    static {
        try {
            DBF = DocumentBuilderFactory.newInstance();
            DBF.setNamespaceAware(true);
            DBF.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DBF.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

            TF = TransformerFactory.newInstance();
            TF.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            XML_SIG_FACTORY = XMLSignatureFactory.getInstance("DOM");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inicializar fábricas XML para assinatura digital", e);
        }
    }

    public AssinadorArquivoXml() {
    }

    /**
     * Assina uma string XML completa contendo DPS ou Pedido de Registro de Evento.
     */
    public String assinar(String xml, PrivateKey privateKey, X509Certificate cert) throws XmlSignatureException {
        if (xml == null || xml.isBlank()) {
            throw new IllegalArgumentException("XML não pode ser nulo ou vazio para assinar");
        }
        if (privateKey == null || cert == null) {
            throw new IllegalArgumentException("PrivateKey e X509Certificate são obrigatórios para assinar");
        }

        try {
            DocumentBuilder builder = DBF.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));

            assinarElemento(doc, doc.getDocumentElement(), privateKey, cert);

            return documentToString(doc);
        } catch (XmlSignatureException e) {
            throw e;
        } catch (Exception e) {
            throw new XmlSignatureException("Erro ao assinar documento XML: " + e.getMessage(), e);
        }
    }

    /**
     * Assina digitalmente um elemento do documento DOM.
     */
    public void assinarElemento(Document doc, Element elementToSign, PrivateKey privateKey, X509Certificate cert)
            throws XmlSignatureException {
        if (doc == null || elementToSign == null || privateKey == null || cert == null) {
            throw new IllegalArgumentException("Parâmetros obrigatórios para assinatura não podem ser nulos");
        }

        try {
            // Remover placeholders de assinatura existentes se houver
            removerAssinaturaExistente(elementToSign);

            // Identificar a tag assinada (infDPS, infPedReg, ou o próprio elemento)
            Element signedInfoElement = localizarElementoIdentificado(elementToSign);
            String uriReference = "";

            if (signedInfoElement != null && signedInfoElement.hasAttribute("Id")) {
                String idValue = signedInfoElement.getAttribute("Id");
                signedInfoElement.setIdAttribute("Id", true);
                uriReference = "#" + idValue;
            } else if (elementToSign.hasAttribute("Id")) {
                String idValue = elementToSign.getAttribute("Id");
                elementToSign.setIdAttribute("Id", true);
                uriReference = "#" + idValue;
            }

            // Configurar transformações: Enveloped + C14N
            List<Transform> transforms = new ArrayList<>();
            transforms.add(XML_SIG_FACTORY.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
            transforms.add(XML_SIG_FACTORY.newTransform(ALGORITMO_CANONIZACAO, (TransformParameterSpec) null));

            Reference ref = XML_SIG_FACTORY.newReference(
                    uriReference,
                    XML_SIG_FACTORY.newDigestMethod(ALGORITMO_DIGEST, null),
                    transforms,
                    null,
                    null
            );

            SignedInfo signedInfo = XML_SIG_FACTORY.newSignedInfo(
                    XML_SIG_FACTORY.newCanonicalizationMethod(ALGORITMO_CANONIZACAO, (C14NMethodParameterSpec) null),
                    XML_SIG_FACTORY.newSignatureMethod(ALGORITMO_ASSINATURA, null),
                    Collections.singletonList(ref)
            );

            // KeyInfo com dados do Certificado X509
            KeyInfoFactory kif = XML_SIG_FACTORY.getKeyInfoFactory();
            X509Data x509Data = kif.newX509Data(Collections.singletonList(cert));
            KeyInfo keyInfo = kif.newKeyInfo(Collections.singletonList(x509Data));

            DOMSignContext dsc = new DOMSignContext(privateKey, elementToSign);
            XMLSignature signature = XML_SIG_FACTORY.newXMLSignature(signedInfo, keyInfo);
            signature.sign(dsc);

        } catch (Exception e) {
            throw new XmlSignatureException("Falha na assinatura digital XMLDSig SHA-256: " + e.getMessage(), e);
        }
    }

    private static Element localizarElementoIdentificado(Element root) {
        // Procurar por nós conhecidos com atributo Id: infDPS, infPedReg, infEvento
        String[] tags = {"infDPS", "infPedReg", "infEvento"};
        for (String tag : tags) {
            NodeList list = root.getElementsByTagName(tag);
            if (list.getLength() > 0 && list.item(0) instanceof Element elem && elem.hasAttribute("Id")) {
                return elem;
            }
            NodeList listNs = root.getElementsByTagNameNS("*", tag);
            if (listNs.getLength() > 0 && listNs.item(0) instanceof Element elemNs && elemNs.hasAttribute("Id")) {
                return elemNs;
            }
        }
        return null;
    }

    private static void removerAssinaturaExistente(Element element) {
        NodeList placeholders = element.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        while (placeholders.getLength() > 0) {
            Node node = placeholders.item(0);
            node.getParentNode().removeChild(node);
        }
    }

    public static String documentToString(Document doc) throws Exception {
        Transformer transformer = TF.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }
}
