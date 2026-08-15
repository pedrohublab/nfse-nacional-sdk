package com.hubpedro.nfsenacional.xml;

import com.hubpedro.nfsenacional.domain.model.EventoNfse;
import com.hubpedro.nfsenacional.domain.model.PedidoCancelamento;
import com.hubpedro.nfsenacional.domain.model.PedidoSubstituicao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Gerador de XML para Pedido de Registro de Eventos da NFS-e Nacional (Cancelamento, Substituição, etc.).
 */
public final class EventoXmlGenerator {

    private static final Logger log = LoggerFactory.getLogger(EventoXmlGenerator.class);
    private static final DateTimeFormatter OFFSET_DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    private static final String VERSAO_APLIC = "NFSENACIONAL-SDK-1.0";

    private final XmlFactoryProvider xmlFactoryProvider;

    public EventoXmlGenerator() {
        this(new XmlFactoryProvider());
    }

    public EventoXmlGenerator(XmlFactoryProvider xmlFactoryProvider) {
        this.xmlFactoryProvider = Objects.requireNonNull(xmlFactoryProvider, "XmlFactoryProvider não pode ser nulo");
    }

    /**
     * Gera o XML completo para o Pedido de Cancelamento (e101101).
     */
    public String gerarXmlCancelamento(PedidoCancelamento pedido) {
        return toXmlString(gerarDocumentCancelamento(pedido));
    }

    public Document gerarDocumentCancelamento(PedidoCancelamento pedido) {
        Objects.requireNonNull(pedido, "Pedido de cancelamento não pode ser nulo");
        try {
            Document doc = xmlFactoryProvider.createDocument();

            Element pedRegEvento = doc.createElementNS(DpsXmlTags.NAMESPACE, DpsXmlTags.PED_REG_EVENTO);
            pedRegEvento.setAttribute(DpsXmlTags.ATTR_VERSAO, "1.00");
            doc.appendChild(pedRegEvento);

            Element infPedReg = createNsElement(doc, DpsXmlTags.INF_PED_REG);
            infPedReg.setAttribute(DpsXmlTags.ATTR_ID, pedido.getIdXml());
            pedRegEvento.appendChild(infPedReg);

            addElement(doc, infPedReg, DpsXmlTags.TP_AMB, pedido.getTipoAmbiente().getCodigo());
            addElement(doc, infPedReg, DpsXmlTags.VER_APLIC, VERSAO_APLIC);
            addElement(doc, infPedReg, DpsXmlTags.DH_EVENTO, pedido.getDataHoraEvento().format(OFFSET_DATE_TIME_FMT));
            addElement(doc, infPedReg, DpsXmlTags.TP_EVENTO, pedido.getTipoEvento().getCodigo());
            addElement(doc, infPedReg, DpsXmlTags.N_SEQ_EVENTO, String.valueOf(pedido.getSequencialEvento()));

            adicionarAutor(doc, infPedReg, pedido.getCnpjOuCpfAutor());
            addElement(doc, infPedReg, DpsXmlTags.CH_NFSE, pedido.getChaveAcessoNfse());

            // Detalhe do evento: e101101
            Element detEvento = createNsElement(doc, DpsXmlTags.DET_EVENTO);
            infPedReg.appendChild(detEvento);

            Element evCancl = createNsElement(doc, DpsXmlTags.EV_CANCL_NFSE);
            detEvento.appendChild(evCancl);
            addElement(doc, evCancl, DpsXmlTags.C_MOTIVO, pedido.getCodigoMotivo());
            if (pedido.getDescricaoMotivo() != null) {
                addElement(doc, evCancl, DpsXmlTags.X_MOTIVO, pedido.getDescricaoMotivo());
            }

            return doc;
        } catch (Exception e) {
            log.error("Erro ao gerar XML de cancelamento de NFS-e: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar XML de cancelamento: " + e.getMessage(), e);
        }
    }

    /**
     * Gera o XML completo para o Pedido de Substituição (e101103).
     */
    public String gerarXmlSubstituicao(PedidoSubstituicao pedido) {
        return toXmlString(gerarDocumentSubstituicao(pedido));
    }

    public Document gerarDocumentSubstituicao(PedidoSubstituicao pedido) {
        Objects.requireNonNull(pedido, "Pedido de substituição não pode ser nulo");
        try {
            Document doc = xmlFactoryProvider.createDocument();

            Element pedRegEvento = doc.createElementNS(DpsXmlTags.NAMESPACE, DpsXmlTags.PED_REG_EVENTO);
            pedRegEvento.setAttribute(DpsXmlTags.ATTR_VERSAO, "1.00");
            doc.appendChild(pedRegEvento);

            Element infPedReg = createNsElement(doc, DpsXmlTags.INF_PED_REG);
            infPedReg.setAttribute(DpsXmlTags.ATTR_ID, pedido.getIdXml());
            pedRegEvento.appendChild(infPedReg);

            addElement(doc, infPedReg, DpsXmlTags.TP_AMB, pedido.getTipoAmbiente().getCodigo());
            addElement(doc, infPedReg, DpsXmlTags.VER_APLIC, VERSAO_APLIC);
            addElement(doc, infPedReg, DpsXmlTags.DH_EVENTO, pedido.getDataHoraEvento().format(OFFSET_DATE_TIME_FMT));
            addElement(doc, infPedReg, DpsXmlTags.TP_EVENTO, pedido.getTipoEvento().getCodigo());
            addElement(doc, infPedReg, DpsXmlTags.N_SEQ_EVENTO, String.valueOf(pedido.getSequencialEvento()));

            adicionarAutor(doc, infPedReg, pedido.getCnpjOuCpfAutor());
            addElement(doc, infPedReg, DpsXmlTags.CH_NFSE, pedido.getChaveAcessoNfse());

            // Detalhe do evento: e101103
            Element detEvento = createNsElement(doc, DpsXmlTags.DET_EVENTO);
            infPedReg.appendChild(detEvento);

            Element evSubst = createNsElement(doc, DpsXmlTags.EV_SUBST_NFSE);
            detEvento.appendChild(evSubst);
            addElement(doc, evSubst, DpsXmlTags.C_MOTIVO, pedido.getCodigoMotivo());
            if (pedido.getDescricaoMotivo() != null) {
                addElement(doc, evSubst, DpsXmlTags.X_MOTIVO, pedido.getDescricaoMotivo());
            }
            addElement(doc, evSubst, DpsXmlTags.CH_DPS_SUBST, pedido.getChaveDpsSubstituta());

            return doc;
        } catch (Exception e) {
            log.error("Erro ao gerar XML de substituição de NFS-e: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar XML de substituição: " + e.getMessage(), e);
        }
    }

    private void adicionarAutor(Document doc, Element parent, String autor) {
        String digits = autor.replaceAll("[^0-9]", "");
        if (digits.length() == 14) {
            addElement(doc, parent, "CNPJAutor", digits);
        } else {
            addElement(doc, parent, "CPFAutor", digits);
        }
    }

    private Element createNsElement(Document doc, String tagName) {
        return doc.createElementNS(DpsXmlTags.NAMESPACE, tagName);
    }

    private void addElement(Document doc, Element parent, String tag, String value) {
        if (value == null) {
            return;
        }
        Element element = createNsElement(doc, tag);
        element.setTextContent(value);
        parent.appendChild(element);
    }

    private String toXmlString(Document doc) {
        try {
            Transformer transformer = xmlFactoryProvider.createTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            log.error("Erro ao converter Document de evento para String XML: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao converter Document para String XML", e);
        }
    }
}
