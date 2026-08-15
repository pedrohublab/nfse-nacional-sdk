package com.hubpedro.nfsenacional.xml;

import com.hubpedro.nfsenacional.domain.DPSTest;
import com.hubpedro.nfsenacional.domain.model.DPS;
import com.hubpedro.nfsenacional.domain.model.PedidoCancelamento;
import com.hubpedro.nfsenacional.domain.model.PedidoSubstituicao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes de Geração de XML da DPS e Eventos")
public class DpsXmlGeneratorTest {

    @Test
    @DisplayName("Deve gerar XML da DPS com namespace e estrutura corretos")
    void deveGerarXmlDpsCorretamente() {
        DPS dps = DPSTest.criarDpsValida();
        DpsXmlGenerator generator = new DpsXmlGenerator();
        String xml = generator.gerar(dps);

        assertThat(xml).startsWith("<?xml version=\"1.0\"");
        assertThat(xml).contains("<DPS xmlns=\"http://www.sped.fazenda.gov.br/nfse\">");
        assertThat(xml).contains("<infDPS Id=\"" + dps.getChaveDPS().getIdXml() + "\" versao=\"1.00\">");
        assertThat(xml).contains("<tpAmb>2</tpAmb>");
        assertThat(xml).contains("<nDPS>101</nDPS>");
        assertThat(xml).contains("<serie>1</serie>");
        assertThat(xml).contains("<CNPJ>11222333000181</CNPJ>");
        assertThat(xml).contains("<CPF>12345678909</CPF>");
        assertThat(xml).contains("<vServ>1500.00</vServ>");
        assertThat(xml).contains("<vISS>75.00</vISS>");
    }

    @Test
    @DisplayName("Deve gerar XML de cancelamento com e101101")
    void deveGerarXmlCancelamento() {
        PedidoCancelamento pedido = PedidoCancelamento.builder()
                .chaveAcessoNfse("35503082608112223330001810000000000000000000000001")
                .cnpjOuCpfAutor("11222333000181")
                .codigoMotivo("1")
                .descricaoMotivo("Erro no valor")
                .build();

        EventoXmlGenerator generator = new EventoXmlGenerator();
        String xml = generator.gerarXmlCancelamento(pedido);

        assertThat(xml).contains("<pedRegEvento");
        assertThat(xml).contains("xmlns=\"http://www.sped.fazenda.gov.br/nfse\"");
        assertThat(xml).contains("versao=\"1.00\"");
        assertThat(xml).contains("<tpEvento>e101101</tpEvento>");
        assertThat(xml).contains("<CNPJAutor>11222333000181</CNPJAutor>");
        assertThat(xml).contains("<e101101>");
        assertThat(xml).contains("<cMotivo>1</cMotivo>");
        assertThat(xml).contains("<xMotivo>Erro no valor</xMotivo>");
    }

    @Test
    @DisplayName("Deve gerar XML de substituição com e101103")
    void deveGerarXmlSubstituicao() {
        PedidoSubstituicao pedido = PedidoSubstituicao.builder()
                .chaveAcessoNfse("35503082608112223330001810000000000000000000000001")
                .cnpjOuCpfAutor("11222333000181")
                .codigoMotivo("1")
                .descricaoMotivo("Substituicao de tomador")
                .chaveDpsSubstituta("35503082608112223330001810000000000000000000000002")
                .build();

        EventoXmlGenerator generator = new EventoXmlGenerator();
        String xml = generator.gerarXmlSubstituicao(pedido);

        assertThat(xml).contains("<pedRegEvento");
        assertThat(xml).contains("xmlns=\"http://www.sped.fazenda.gov.br/nfse\"");
        assertThat(xml).contains("versao=\"1.00\"");
        assertThat(xml).contains("<tpEvento>e101103</tpEvento>");
        assertThat(xml).contains("<e101103>");
        assertThat(xml).contains("<chDPSSubst>35503082608112223330001810000000000000000000000002</chDPSSubst>");
    }
}
