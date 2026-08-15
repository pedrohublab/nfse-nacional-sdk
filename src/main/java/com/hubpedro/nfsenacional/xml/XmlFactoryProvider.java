package com.hubpedro.nfsenacional.xml;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import org.w3c.dom.Document;

/**
 * Provedor seguro e de alta performance de fábricas de parsing e transformação XML.
 * Protegido contra ataques XXE e injeções de DTD externa.
 */
public final class XmlFactoryProvider {

    private final DocumentBuilderFactory documentBuilderFactory;
    private final TransformerFactory transformerFactory;

    public XmlFactoryProvider() {
        try {
            this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
            this.documentBuilderFactory.setNamespaceAware(true);
            this.documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            this.documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

            this.transformerFactory = TransformerFactory.newInstance();
            this.transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (ParserConfigurationException | TransformerConfigurationException e) {
            throw new RuntimeException("Erro ao configurar fábricas XML seguras", e);
        }
    }

    public Document createDocument() {
        try {
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            return builder.newDocument();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Erro ao criar novo Document DOM", e);
        }
    }

    public DocumentBuilder createDocumentBuilder() {
        try {
            return documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Erro ao instanciar DocumentBuilder", e);
        }
    }

    public Transformer createTransformer() {
        try {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            return transformer;
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException("Erro ao instanciar Transformer", e);
        }
    }

    public Transformer createTransformerWithoutDeclaration() {
        try {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            return transformer;
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException("Erro ao instanciar Transformer sem declaração", e);
        }
    }
}
