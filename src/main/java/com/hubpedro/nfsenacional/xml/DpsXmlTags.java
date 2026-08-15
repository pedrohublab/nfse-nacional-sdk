package com.hubpedro.nfsenacional.xml;

/**
 * Constantes de tags e namespaces do padrão nacional da NFS-e (ADN / Sefin Nacional).
 */
public final class DpsXmlTags {

    private DpsXmlTags() {
    }

    public static final String NAMESPACE = "http://www.sped.fazenda.gov.br/nfse";
    public static final String DSIG_NAMESPACE = "http://www.w3.org/2000/09/xmldsig#";

    // Raiz e nós principais
    public static final String DPS = "DPS";
    public static final String INF_DPS = "infDPS";
    public static final String PED_REG_EVENTO = "pedRegEvento";
    public static final String INF_PED_REG = "infPedReg";

    // Atributos
    public static final String ATTR_ID = "Id";
    public static final String ATTR_VERSAO = "versao";

    // Cabeçalho DPS
    public static final String TP_AMB = "tpAmb";
    public static final String DH_EMI = "dhEmi";
    public static final String VER_APLIC = "verAplic";
    public static final String SERIE = "serie";
    public static final String N_DPS = "nDPS";
    public static final String D_COMPET = "dCompet";
    public static final String TP_EMIT = "tpEmit";
    public static final String C_LOC_EMI = "cLocEmi";

    // Substituição
    public static final String SUBST = "subst";
    public static final String CH_SUBSTDA = "chSubstda";
    public static final String C_MOTIVO = "cMotivo";
    public static final String X_MOTIVO = "xMotivo";

    // Prestador
    public static final String PREST = "prest";
    public static final String CNPJ = "CNPJ";
    public static final String CPF = "CPF";
    public static final String IM = "IM";
    public static final String X_NOME = "xNome";
    public static final String FONE = "fone";
    public static final String EMAIL = "email";

    // Regime Tributário
    public static final String REG_TRIB = "regTrib";
    public static final String OP_SIMP_NAC = "opSimpNac";
    public static final String REG_AP_TRIB_SN = "regApTribSN";
    public static final String REG_ESP_TRIB = "regEspTrib";

    // Tomador e Intermediário
    public static final String TOMA = "toma";
    public static final String INTERM = "interm";

    // Endereço
    public static final String END = "end";
    public static final String END_NAC = "endNac";
    public static final String END_EXT = "endExt";
    public static final String X_LGR = "xLgr";
    public static final String NRO = "nro";
    public static final String X_CPL = "xCpl";
    public static final String X_BAIRRO = "xBairro";
    public static final String C_MUN = "cMun";
    public static final String UF = "UF";
    public static final String CEP = "CEP";
    public static final String C_PAIS = "cPais";

    // Serviço
    public static final String SERV = "serv";
    public static final String C_SERV = "cServ";
    public static final String C_TRIB_NAC = "cTribNac";
    public static final String C_TRIB_MUN = "cTribMun";
    public static final String CNAE = "CNAE";
    public static final String X_DESC_SERV = "xDescServ";
    public static final String LOC_PREST = "locPrest";
    public static final String C_LOC_PRESTACAO = "cLocPrestacao";
    public static final String C_PAIS_PRESTACAO = "cPaisPrestacao";

    // Valores
    public static final String VALORES = "valores";
    public static final String V_SERV_PREST = "vServPrest";
    public static final String V_RECEB = "vReceb";
    public static final String V_SERV = "vServ";
    public static final String V_DESC_INCOND = "vDescIncond";
    public static final String V_DESC_COND = "vDescCond";
    public static final String P_DED = "pDed";
    public static final String V_DED = "vDed";

    // Tributos
    public static final String TRIB = "trib";
    public static final String TRIB_MUN = "tribMun";
    public static final String TRIB_ISSQN = "tribISSQN";
    public static final String C_PAIS_RESULT = "cPaisResult";
    public static final String BM = "bm";
    public static final String V_BC_ISS = "vBCISS";
    public static final String P_ALIQ = "pAliq";
    public static final String V_ISS = "vISS";
    public static final String TP_RET_ISSQN = "tpRetISSQN";

    public static final String TRIB_FED = "tribFed";
    public static final String PISCOFINS = "piscofins";
    public static final String V_PIS = "vPIS";
    public static final String V_COFINS = "vCOFINS";
    public static final String V_RET_CP = "vRetCP";
    public static final String V_RET_IRRF = "vRetIRRF";
    public static final String V_RET_CSLL = "vRetCSLL";

    public static final String TOT_TRIB = "totTrib";
    public static final String IND_TOT_TRIB = "indTotTrib";
    public static final String P_TOT_TRIB_SN = "pTotTribSN";

    // Informação Adicional
    public static final String INF_ADICIONAL = "infAdic";
    public static final String X_INF_ADIC = "xInfAdic";

    // Eventos
    public static final String DH_EVENTO = "dhEvento";
    public static final String TP_EVENTO = "tpEvento";
    public static final String N_SEQ_EVENTO = "nSeqEvento";
    public static final String CH_NFSE = "chNFSe";
    public static final String DET_EVENTO = "detEvento";
    public static final String EV_CANCL_NFSE = "e101101";
    public static final String EV_SUBST_NFSE = "e101103";
    public static final String CH_DPS_SUBST = "chDPSSubst";
}
