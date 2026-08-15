package com.hubpedro.nfsenacional.domain;

import com.hubpedro.nfsenacional.domain.enums.*;
import com.hubpedro.nfsenacional.domain.model.CpfCnpj;
import com.hubpedro.nfsenacional.domain.model.DPS;
import com.hubpedro.nfsenacional.domain.valueobject.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes do Aggregate Root DPS")
public class DPSTest {

    @Test
    @DisplayName("Deve construir DPS válida com builder fluente")
    void deveConstruirDpsValida() {
        DPS dps = criarDpsValida();

        assertThat(dps).isNotNull();
        assertThat(dps.getNumeroDPS()).isEqualTo(101L);
        assertThat(dps.getSerie()).isEqualTo("1");
        assertThat(dps.getChaveDPS().valor()).hasSize(50);
        assertThat(dps.getPrestador().getCnpj().getNumero()).isEqualTo("11222333000181");
        assertThat(dps.getTomador().getCpfCnpj().getNumero()).isEqualTo("12345678909");
        assertThat(dps.getValores().getValoresServico().getValorServico()).isEqualByComparingTo(new BigDecimal("1500.00"));
    }

    @Test
    @DisplayName("Deve rejeitar DPS com número de DPS zerado ou negativo")
    void deveRejeitarNumeroDpsInvalido() {
        DPS dpsBase = criarDpsValida();
        assertThatThrownBy(() -> DPS.builder()
                .serie(dpsBase.getSerie())
                .numeroDPS(0)
                .codigoLocalEmissao(dpsBase.getCodigoLocalEmissao())
                .tipoEmitente(dpsBase.getTipoEmitente())
                .dataCompetencia(dpsBase.getDataCompetencia())
                .versaoAplicativo(dpsBase.getVersaoAplicativo())
                .prestador(dpsBase.getPrestador())
                .servico(dpsBase.getServico())
                .valores(dpsBase.getValores())
                .build()
        ).isInstanceOf(IllegalArgumentException.class);
    }

    public static DPS criarDpsValida() {
        RegimeTributario regTrib = RegimeTributario.builder()
                .opcaoSimplesNacional(OpcaoSimplesNacional.SIM)
                .regimeApuracaoSN(RegimeApuracaoSN.SIMPLES_NACIONAL_RECEITA_BRUTA_ATE_180K)
                .build();

        Endereco endPrestador = Endereco.builder()
                .logradouro("Avenida Paulista")
                .numero("1000")
                .complemento("Sala 10")
                .bairro("Bela Vista")
                .codigoMunicipio("3550308")
                .uf("SP")
                .cep("01310100")
                .build();

        Prestador prestador = Prestador.builder()
                .cnpj(new CNPJ("11222333000181"))
                .nomeRazaoSocial("EMPRESA PRESTADORA LTDA")
                .inscricaoMunicipal("12345678")
                .endereco(endPrestador)
                .regimeTributario(regTrib)
                .build();

        Endereco endTomador = Endereco.builder()
                .logradouro("Rua das Flores")
                .numero("123")
                .bairro("Centro")
                .codigoMunicipio("3550308")
                .uf("SP")
                .cep("01001000")
                .build();

        Tomador tomador = Tomador.builder()
                .cpfCnpj(CpfCnpj.of("12345678909"))
                .nomeRazaoSocial("CLIENTE TOMADOR LTDA")
                .endereco(endTomador)
                .build();

        CodigoServico codServico = CodigoServico.builder()
                .codigoTribNacional("01.07.01")
                .codigoTribMunicipal("01070100")
                .cnae("6201500")
                .descricaoServico("Desenvolvimento de Software Sob Encomenda")
                .build();

        LocalPrestacao locPrest = LocalPrestacao.builder()
                .codigoLocalPrestacao("3550308")
                .codigoPaisPrestacao("1058")
                .build();

        Servico servico = Servico.builder()
                .codigoServico(codServico)
                .localPrestacao(locPrest)
                .build();

        ValoresServico valServ = ValoresServico.builder()
                .valorServico(new BigDecimal("1500.00"))
                .valorRecebido(new BigDecimal("1500.00"))
                .build();

        BaseCalculo baseCalculo = BaseCalculo.builder()
                .valorBaseCalculo(new BigDecimal("1500.00"))
                .aliquota(new BigDecimal("5.00"))
                .valorISS(new BigDecimal("75.00"))
                .tipoRetencao(TipoRetencaoISSQN.NAO_RETIDO)
                .build();

        TributacaoMunicipal tribMun = TributacaoMunicipal.builder()
                .tributacaoISSQN(TributacaoISSQN.OPERACAO_NORMAL)
                .codigoPaisResultado("1058")
                .baseCalculo(baseCalculo)
                .build();

        TotalTributos totTrib = TotalTributos.builder()
                .indicadorTotalTributos(IndicadorTotalTributos.SIM)
                .percentualTotalTributosSN(new BigDecimal("6.00"))
                .build();

        Tributacao trib = Tributacao.builder()
                .tributacaoMunicipal(tribMun)
                .totalTributos(totTrib)
                .build();

        Valores valores = Valores.builder()
                .valoresServico(valServ)
                .valorDescontoIncondicionado(BigDecimal.ZERO)
                .valorDescontoCondicionado(BigDecimal.ZERO)
                .tributacao(trib)
                .build();

        return DPS.builder()
                .serie("1")
                .numeroDPS(101L)
                .tipoAmbiente(TipoAmbiente.HOMOLOGACAO)
                .dataHoraEmissao(OffsetDateTime.parse("2026-08-15T10:00:00-03:00"))
                .dataCompetencia(LocalDate.of(2026, 8, 15))
                .tipoEmitente(TipoEmitente.PRESTADOR)
                .codigoLocalEmissao("3550308")
                .versaoAplicativo("NFSENACIONAL-SDK-1.0")
                .prestador(prestador)
                .tomador(tomador)
                .servico(servico)
                .valores(valores)
                .informacaoAdicional(new InformacaoAdicional("Servicos prestados referente a Agosto/2026"))
                .build();
    }
}
