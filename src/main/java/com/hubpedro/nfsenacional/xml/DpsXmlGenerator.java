package com.hubpedro.nfsenacional.xml;

import com.hubpedro.nfsenacional.domain.model.CpfCnpj;
import com.hubpedro.nfsenacional.domain.model.DPS;
import com.hubpedro.nfsenacional.domain.valueobject.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Gerador de XML da Declaração de Prestação de Serviços (DPS)
 * conforme o padrão nacional da NFS-e (ADN v1.00).
 */
public final class DpsXmlGenerator {

    private static final Logger log = LoggerFactory.getLogger(DpsXmlGenerator.class);
    private static final DateTimeFormatter OFFSET_DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    private static final DateTimeFormatter LOCAL_DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final int DECIMAL_SCALE = 2;

    private final XmlFactoryProvider xmlFactoryProvider;

    public DpsXmlGenerator() {
        this(new XmlFactoryProvider());
    }

    public DpsXmlGenerator(XmlFactoryProvider xmlFactoryProvider) {
        this.xmlFactoryProvider = Objects.requireNonNull(xmlFactoryProvider, "XmlFactoryProvider não pode ser nulo");
    }

    /**
     * Gera o XML completo do DPS a partir do aggregate de domínio.
     *
     * @param dps o aggregate DPS
     * @return o XML como String
     */
    public String gerar(DPS dps) {
        return toXmlString(gerarDocument(dps));
    }

    public Document gerarDocument(DPS dps) {
        Objects.requireNonNull(dps, "DPS não pode ser nula");
        try {
            Document doc = xmlFactoryProvider.createDocument();

            // Elemento raiz: <DPS xmlns="http://www.sped.fazenda.gov.br/nfse">
            Element dpsElement = doc.createElementNS(DpsXmlTags.NAMESPACE, DpsXmlTags.DPS);
            doc.appendChild(dpsElement);

            // <infDPS Id="DPS..." versao="1.00">
            Element infDps = createNsElement(doc, DpsXmlTags.INF_DPS);
            infDps.setAttribute(DpsXmlTags.ATTR_ID, dps.getChaveDPS().getIdXml());
            infDps.setAttribute(DpsXmlTags.ATTR_VERSAO, dps.getVersao());
            dpsElement.appendChild(infDps);

            // Cabeçalho da DPS
            addElement(doc, infDps, DpsXmlTags.TP_AMB, dps.getTipoAmbiente().getCodigo());
            addElement(doc, infDps, DpsXmlTags.DH_EMI, dps.getDataHoraEmissao().format(OFFSET_DATE_TIME_FMT));
            addElement(doc, infDps, DpsXmlTags.VER_APLIC, dps.getVersaoAplicativo());
            addElement(doc, infDps, DpsXmlTags.SERIE, dps.getSerie());
            addElement(doc, infDps, DpsXmlTags.N_DPS, String.valueOf(dps.getNumeroDPS()));
            addElement(doc, infDps, DpsXmlTags.D_COMPET, dps.getDataCompetencia().format(LOCAL_DATE_FMT));
            addElement(doc, infDps, DpsXmlTags.TP_EMIT, dps.getTipoEmitente().getCodigo());
            addElement(doc, infDps, DpsXmlTags.C_LOC_EMI, dps.getCodigoLocalEmissao());

            // Substituição (opcional)
            adicionarSubstituicao(doc, infDps, dps.getSubstituicao());

            // Prestador (obrigatório)
            adicionarPrestador(doc, infDps, dps.getPrestador());

            // Tomador (opcional)
            adicionarTomador(doc, infDps, dps.getTomador());

            // Intermediário (opcional)
            adicionarIntermediario(doc, infDps, dps.getIntermediario());

            // Serviço (obrigatório)
            adicionarServico(doc, infDps, dps.getServico());

            // Valores (obrigatório)
            adicionarValores(doc, infDps, dps.getValores());

            // Informação Adicional (opcional)
            if (dps.getInformacaoAdicional() != null) {
                Element infAdicional = createNsElement(doc, DpsXmlTags.INF_ADICIONAL);
                infDps.appendChild(infAdicional);
                addElement(doc, infAdicional, DpsXmlTags.X_INF_ADIC,
                        dps.getInformacaoAdicional().getInformacaoAdicional());
            }

            return doc;

        } catch (Exception e) {
            log.error("Erro ao gerar XML da DPS: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar XML da DPS: " + e.getMessage(), e);
        }
    }

    private void adicionarSubstituicao(Document doc, Element parent, Substituicao substituicao) {
        if (substituicao == null) {
            return;
        }
        Element subst = createNsElement(doc, DpsXmlTags.SUBST);
        parent.appendChild(subst);
        addElement(doc, subst, DpsXmlTags.CH_SUBSTDA, substituicao.getChaveSubstituida());
        addElement(doc, subst, DpsXmlTags.C_MOTIVO, substituicao.getCodigoMotivo());
        addElement(doc, subst, DpsXmlTags.X_MOTIVO, substituicao.getDescricaoMotivo());
    }

    private void adicionarPrestador(Document doc, Element parent, Prestador prestador) {
        Element prest = createNsElement(doc, DpsXmlTags.PREST);
        parent.appendChild(prest);

        addElement(doc, prest, DpsXmlTags.CNPJ, prestador.getCnpj().getNumero());
        addElement(doc, prest, DpsXmlTags.IM, prestador.getInscricaoMunicipal());
        addElement(doc, prest, DpsXmlTags.X_NOME, prestador.getNomeRazaoSocial());

        adicionarEndereco(doc, prest, prestador.getEndereco());

        addElement(doc, prest, DpsXmlTags.FONE, prestador.getTelefone());
        addElement(doc, prest, DpsXmlTags.EMAIL, prestador.getEmail());

        // Regime Tributário
        RegimeTributario regTrib = prestador.getRegimeTributario();
        if (regTrib != null) {
            Element regTribEl = createNsElement(doc, DpsXmlTags.REG_TRIB);
            prest.appendChild(regTribEl);
            addElement(doc, regTribEl, DpsXmlTags.OP_SIMP_NAC,
                    regTrib.getOpcaoSimplesNacional().getCodigo());
            if (regTrib.getRegimeApuracaoSN() != null) {
                addElement(doc, regTribEl, DpsXmlTags.REG_AP_TRIB_SN,
                        regTrib.getRegimeApuracaoSN().getCodigo());
            }
            if (regTrib.getRegimeEspecialTributacao() != null) {
                addElement(doc, regTribEl, DpsXmlTags.REG_ESP_TRIB,
                        regTrib.getRegimeEspecialTributacao().getCodigo());
            }
        }
    }

    private void adicionarTomador(Document doc, Element parent, Tomador tomador) {
        if (tomador == null) {
            return;
        }
        Element toma = createNsElement(doc, DpsXmlTags.TOMA);
        parent.appendChild(toma);

        adicionarCpfCnpj(doc, toma, tomador.getCpfCnpj());
        addElement(doc, toma, DpsXmlTags.X_NOME, tomador.getNomeRazaoSocial());
        adicionarEndereco(doc, toma, tomador.getEndereco());
        addElement(doc, toma, DpsXmlTags.FONE, tomador.getTelefone());
        addElement(doc, toma, DpsXmlTags.EMAIL, tomador.getEmail());
    }

    private void adicionarIntermediario(Document doc, Element parent, Intermediario intermediario) {
        if (intermediario == null) {
            return;
        }
        Element interm = createNsElement(doc, DpsXmlTags.INTERM);
        parent.appendChild(interm);

        adicionarCpfCnpj(doc, interm, intermediario.getCpfCnpj());
        addElement(doc, interm, DpsXmlTags.X_NOME, intermediario.getNomeRazaoSocial());
    }

    private void adicionarCpfCnpj(Document doc, Element parent, CpfCnpj cpfCnpj) {
        if (cpfCnpj == null) {
            return;
        }
        if (cpfCnpj.isCpf()) {
            addElement(doc, parent, DpsXmlTags.CPF, cpfCnpj.getNumero());
        } else {
            addElement(doc, parent, DpsXmlTags.CNPJ, cpfCnpj.getNumero());
        }
    }

    private void adicionarEndereco(Document doc, Element parent, Endereco endereco) {
        if (endereco == null) {
            return;
        }
        Element end = createNsElement(doc, DpsXmlTags.END);
        parent.appendChild(end);

        addElement(doc, end, DpsXmlTags.X_LGR, endereco.getLogradouro());
        addElement(doc, end, DpsXmlTags.NRO, endereco.getNumero());
        addElement(doc, end, DpsXmlTags.X_CPL, endereco.getComplemento());
        addElement(doc, end, DpsXmlTags.X_BAIRRO, endereco.getBairro());
        addElement(doc, end, DpsXmlTags.C_MUN, endereco.getCodigoMunicipio());
        addElement(doc, end, DpsXmlTags.UF, endereco.getUf());
        addElement(doc, end, DpsXmlTags.CEP, endereco.getCep());
        addElement(doc, end, DpsXmlTags.C_PAIS, endereco.getCodigoPais());
    }

    private void adicionarServico(Document doc, Element parent, Servico servico) {
        Element serv = createNsElement(doc, DpsXmlTags.SERV);
        parent.appendChild(serv);

        // Código do Serviço
        CodigoServico codServico = servico.getCodigoServico();
        Element cServ = createNsElement(doc, DpsXmlTags.C_SERV);
        serv.appendChild(cServ);
        addElement(doc, cServ, DpsXmlTags.C_TRIB_NAC, codServico.getCodigoTribNacional());
        addElement(doc, cServ, DpsXmlTags.C_TRIB_MUN, codServico.getCodigoTribMunicipal());
        addElement(doc, cServ, DpsXmlTags.CNAE, codServico.getCnae());
        addElement(doc, cServ, DpsXmlTags.X_DESC_SERV, codServico.getDescricaoServico());

        // Local Prestação
        LocalPrestacao locPrest = servico.getLocalPrestacao();
        Element locPrestEl = createNsElement(doc, DpsXmlTags.LOC_PREST);
        serv.appendChild(locPrestEl);
        addElement(doc, locPrestEl, DpsXmlTags.C_LOC_PRESTACAO, locPrest.getCodigoLocalPrestacao());
        addElement(doc, locPrestEl, DpsXmlTags.C_PAIS_PRESTACAO, locPrest.getCodigoPaisPrestacao());
    }

    private void adicionarValores(Document doc, Element parent, Valores valores) {
        Element valoresEl = createNsElement(doc, DpsXmlTags.VALORES);
        parent.appendChild(valoresEl);

        // Valores do Serviço Prestado
        ValoresServico valServico = valores.getValoresServico();
        Element vServPrest = createNsElement(doc, DpsXmlTags.V_SERV_PREST);
        valoresEl.appendChild(vServPrest);
        addDecimalElement(doc, vServPrest, DpsXmlTags.V_RECEB, valServico.getValorRecebido());
        addDecimalElement(doc, vServPrest, DpsXmlTags.V_SERV, valServico.getValorServico());

        // Descontos
        addDecimalElement(doc, valoresEl, DpsXmlTags.V_DESC_INCOND, valores.getValorDescontoIncondicionado());
        addDecimalElement(doc, valoresEl, DpsXmlTags.V_DESC_COND, valores.getValorDescontoCondicionado());

        // Dedução
        Deducao deducao = valores.getDeducao();
        if (deducao != null) {
            addDecimalElement(doc, valoresEl, DpsXmlTags.P_DED, deducao.getPercentualDeducao());
            addDecimalElement(doc, valoresEl, DpsXmlTags.V_DED, deducao.getValorDeducao());
        }

        // Tributação
        adicionarTributacao(doc, valoresEl, valores.getTributacao());
    }

    private void adicionarTributacao(Document doc, Element parent, Tributacao tributacao) {
        Element trib = createNsElement(doc, DpsXmlTags.TRIB);
        parent.appendChild(trib);

        // Tributação Municipal
        TributacaoMunicipal tribMun = tributacao.getTributacaoMunicipal();
        Element tribMunEl = createNsElement(doc, DpsXmlTags.TRIB_MUN);
        trib.appendChild(tribMunEl);
        addElement(doc, tribMunEl, DpsXmlTags.TRIB_ISSQN, tribMun.getTributacaoISSQN().getCodigo());
        addElement(doc, tribMunEl, DpsXmlTags.C_PAIS_RESULT, tribMun.getCodigoPaisResultado());

        // Base de Cálculo
        BaseCalculo bc = tribMun.getBaseCalculo();
        if (bc != null) {
            Element bmEl = createNsElement(doc, DpsXmlTags.BM);
            tribMunEl.appendChild(bmEl);
            addDecimalElement(doc, bmEl, DpsXmlTags.V_BC_ISS, bc.getValorBaseCalculo());
            addDecimalElement(doc, bmEl, DpsXmlTags.P_ALIQ, bc.getAliquota());
            addDecimalElement(doc, bmEl, DpsXmlTags.V_ISS, bc.getValorISS());
            addElement(doc, bmEl, DpsXmlTags.TP_RET_ISSQN, bc.getTipoRetencao().getCodigo());
        }

        // Tributação Federal
        TributacaoFederal tribFed = tributacao.getTributacaoFederal();
        if (tribFed != null) {
            Element tribFedEl = createNsElement(doc, DpsXmlTags.TRIB_FED);
            trib.appendChild(tribFedEl);

            PisCofins pisCofins = tribFed.getPisCofins();
            if (pisCofins != null) {
                Element pisCofinsEl = createNsElement(doc, DpsXmlTags.PISCOFINS);
                tribFedEl.appendChild(pisCofinsEl);
                addDecimalElement(doc, pisCofinsEl, DpsXmlTags.V_PIS, pisCofins.getValorPIS());
                addDecimalElement(doc, pisCofinsEl, DpsXmlTags.V_COFINS, pisCofins.getValorCOFINS());
            }

            addDecimalElement(doc, tribFedEl, DpsXmlTags.V_RET_CP, tribFed.getValorRetencaoCP());
            addDecimalElement(doc, tribFedEl, DpsXmlTags.V_RET_IRRF, tribFed.getValorRetencaoIRRF());
            addDecimalElement(doc, tribFedEl, DpsXmlTags.V_RET_CSLL, tribFed.getValorRetencaoCSLL());
        }

        // Total de Tributos
        TotalTributos totTrib = tributacao.getTotalTributos();
        if (totTrib != null) {
            Element totTribEl = createNsElement(doc, DpsXmlTags.TOT_TRIB);
            trib.appendChild(totTribEl);
            addElement(doc, totTribEl, DpsXmlTags.IND_TOT_TRIB,
                    totTrib.getIndicadorTotalTributos().getCodigo());
            addDecimalElement(doc, totTribEl, DpsXmlTags.P_TOT_TRIB_SN,
                    totTrib.getPercentualTotalTributosSN());
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

    private void addDecimalElement(Document doc, Element parent, String tag, BigDecimal value) {
        if (value == null) {
            return;
        }
        addElement(doc, parent, tag, value.setScale(DECIMAL_SCALE, RoundingMode.HALF_UP).toPlainString());
    }

    private String toXmlString(Document doc) {
        try {
            Transformer transformer = xmlFactoryProvider.createTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            log.error("Erro ao converter Document para String XML: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao converter Document para String XML", e);
        }
    }
}
