package com.hubpedro.nfsenacional.xml;

import com.hubpedro.nfsenacional.domain.exception.XmlValidationException;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Validador XSD offline local embutido como recurso no JAR para validação pré-envio.
 * Evita rejeições e tráfego desnecessário para a SEFIN Nacional.
 */
public final class XmlSchemaValidator {

    private static final String SCHEMA_PATH_DPS = "/schemas/DPS_v1.00.xsd";
    private static final String SCHEMA_PATH_EVENTO = "/schemas/pedRegEvento_v1.00.xsd";

    private final Schema dpsSchema;
    private final Schema eventoSchema;

    public XmlSchemaValidator() {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            factory.setResourceResolver(new ClasspathResourceResolver());
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            this.dpsSchema = loadSchema(factory, SCHEMA_PATH_DPS);
            this.eventoSchema = loadSchema(factory, SCHEMA_PATH_EVENTO);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inicializar validador de Schemas XSD da NFS-e Nacional: " + e.getMessage(), e);
        }
    }

    private static Schema loadSchema(SchemaFactory factory, String resourcePath) throws Exception {
        try (InputStream is = XmlSchemaValidator.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalStateException("Schema XSD não encontrado no classpath: " + resourcePath);
            }
            return factory.newSchema(new StreamSource(is, resourcePath));
        }
    }

    /**
     * Valida um XML de DPS contra o Schema XSD oficial (DPS_v1.00.xsd).
     *
     * @param xml string contendo o XML da DPS (assinado ou não)
     * @throws XmlValidationException se o XML violar as regras estruturais do Schema XSD
     */
    public void validarDps(String xml) throws XmlValidationException {
        validarComSchema(dpsSchema, xml, "DPS");
    }

    /**
     * Valida um XML de Pedido de Registro de Evento contra o Schema XSD oficial (pedRegEvento_v1.00.xsd).
     *
     * @param xml string contendo o XML do evento (assinado ou não)
     * @throws XmlValidationException se o XML violar as regras estruturais do Schema XSD
     */
    public void validarEvento(String xml) throws XmlValidationException {
        validarComSchema(eventoSchema, xml, "Evento");
    }

    private void validarComSchema(Schema schema, String xml, String documentType) throws XmlValidationException {
        if (xml == null || xml.isBlank()) {
            throw new IllegalArgumentException("XML não pode ser nulo ou vazio para validação XSD");
        }

        List<String> erros = new ArrayList<>();
        try {
            Validator validator = schema.newValidator();
            validator.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) {
                    // warnings ignorados
                }

                @Override
                public void error(SAXParseException exception) {
                    erros.add(formatarErro(exception));
                }

                @Override
                public void fatalError(SAXParseException exception) {
                    erros.add(formatarErro(exception));
                }
            });

            Source source = new StreamSource(new StringReader(xml));
            validator.validate(source);

            if (!erros.isEmpty()) {
                throw new XmlValidationException(
                        "XML do " + documentType + " é inválido perante o Schema XSD: " + String.join("; ", erros),
                        erros
                );
            }
        } catch (XmlValidationException e) {
            throw e;
        } catch (SAXException e) {
            String msg = "Erro de sintaxe/validação no XML do " + documentType + ": " + e.getMessage();
            erros.add(msg);
            throw new XmlValidationException(msg, erros);
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado durante validação XSD do " + documentType + ": " + e.getMessage(), e);
        }
    }

    private static String formatarErro(SAXParseException e) {
        return String.format("[Linha %d, Coluna %d]: %s", e.getLineNumber(), e.getColumnNumber(), e.getMessage());
    }

    /**
     * Resolver que busca schemas incluídos/importados dentro de /schemas/ no classpath.
     */
    private static class ClasspathResourceResolver implements LSResourceResolver {
        @Override
        public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
            if (systemId == null) {
                return null;
            }

            String path = systemId;
            if (!path.startsWith("/")) {
                path = "/schemas/" + path;
            }

            InputStream is = XmlSchemaValidator.class.getResourceAsStream(path);
            if (is == null) {
                // Tentar basename simples
                String fileName = systemId.contains("/") ? systemId.substring(systemId.lastIndexOf("/") + 1) : systemId;
                is = XmlSchemaValidator.class.getResourceAsStream("/schemas/" + fileName);
            }

            if (is == null) {
                return null;
            }

            final InputStream stream = is;
            return new LSInput() {
                @Override public Reader getCharacterStream() { return new InputStreamReader(stream, StandardCharsets.UTF_8); }
                @Override public void setCharacterStream(Reader characterStream) {}
                @Override public InputStream getByteStream() { return stream; }
                @Override public void setByteStream(InputStream byteStream) {}
                @Override public String getStringData() { return null; }
                @Override public void setStringData(String stringData) {}
                @Override public String getSystemId() { return systemId; }
                @Override public void setSystemId(String systemId) {}
                @Override public String getPublicId() { return publicId; }
                @Override public void setPublicId(String publicId) {}
                @Override public String getBaseURI() { return baseURI; }
                @Override public void setBaseURI(String baseURI) {}
                @Override public String getEncoding() { return "UTF-8"; }
                @Override public void setEncoding(String encoding) {}
                @Override public boolean getCertifiedText() { return false; }
                @Override public void setCertifiedText(boolean certifiedText) {}
            };
        }
    }
}
