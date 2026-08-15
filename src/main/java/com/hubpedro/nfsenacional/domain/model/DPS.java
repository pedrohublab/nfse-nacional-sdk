package com.hubpedro.nfsenacional.domain.model;

import com.hubpedro.nfsenacional.domain.enums.TipoAmbiente;
import com.hubpedro.nfsenacional.domain.enums.TipoEmitente;
import com.hubpedro.nfsenacional.domain.valueobject.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Aggregate Root: Declaração de Prestação de Serviços (DPS).
 * Documento fiscal eletrônico do padrão NFS-e Nacional (ADN).
 * Imutável, construído via Builder, auto-validado.
 */
public final class DPS {

    private final String versao;
    private final ChaveDPS chaveDPS;
    private final TipoAmbiente tipoAmbiente;
    private final OffsetDateTime dataHoraEmissao;
    private final String versaoAplicativo;
    private final String serie;
    private final long numeroDPS;
    private final LocalDate dataCompetencia;
    private final TipoEmitente tipoEmitente;
    private final String codigoLocalEmissao;
    private final Substituicao substituicao;
    private final Prestador prestador;
    private final Tomador tomador;
    private final Intermediario intermediario;
    private final Servico servico;
    private final Valores valores;
    private final InformacaoAdicional informacaoAdicional;

    private DPS(Builder builder) {
        this.versao = builder.versao != null ? builder.versao : "1.00";
        this.tipoAmbiente = builder.tipoAmbiente != null ? builder.tipoAmbiente : TipoAmbiente.HOMOLOGACAO;
        this.dataHoraEmissao = builder.dataHoraEmissao != null ? builder.dataHoraEmissao : OffsetDateTime.now();

        this.versaoAplicativo = Objects.requireNonNull(builder.versaoAplicativo, "Versão do aplicativo é obrigatória");
        this.serie = Objects.requireNonNull(builder.serie, "Série é obrigatória");
        this.numeroDPS = builder.numeroDPS;
        this.dataCompetencia = Objects.requireNonNull(builder.dataCompetencia, "Data de competência é obrigatória");
        this.tipoEmitente = Objects.requireNonNull(builder.tipoEmitente, "Tipo de emitente é obrigatório");
        this.codigoLocalEmissao = Objects.requireNonNull(builder.codigoLocalEmissao, "Código do local de emissão é obrigatório");
        this.prestador = Objects.requireNonNull(builder.prestador, "Prestador é obrigatório");
        this.servico = Objects.requireNonNull(builder.servico, "Serviço é obrigatório");
        this.valores = Objects.requireNonNull(builder.valores, "Valores são obrigatórios");

        this.substituicao = builder.substituicao;
        this.tomador = builder.tomador;
        this.intermediario = builder.intermediario;
        this.informacaoAdicional = builder.informacaoAdicional;

        validar();

        this.chaveDPS = new ChaveDPS(
                codigoLocalEmissao,
                dataHoraEmissao,
                prestador.getCnpj().getNumero(),
                serie,
                numeroDPS
        );
    }

    private void validar() {
        if (versaoAplicativo.isBlank()) {
            throw new IllegalArgumentException("Versão do aplicativo não pode ser vazia");
        }
        if (!serie.matches("[a-zA-Z0-9]{1,5}")) {
            throw new IllegalArgumentException("Série deve ter entre 1 e 5 caracteres alfanuméricos: " + serie);
        }
        if (numeroDPS <= 0) {
            throw new IllegalArgumentException("Número da DPS deve ser maior que zero: " + numeroDPS);
        }
        if (!codigoLocalEmissao.matches("\\d{7}")) {
            throw new IllegalArgumentException("Código do local de emissão deve ter exatamente 7 dígitos: " + codigoLocalEmissao);
        }
        if (tipoEmitente == TipoEmitente.TOMADOR && tomador == null) {
            throw new IllegalArgumentException("Tomador é obrigatório quando o tipo de emitente é Tomador");
        }
        if (tipoEmitente == TipoEmitente.INTERMEDIARIO && intermediario == null) {
            throw new IllegalArgumentException("Intermediário é obrigatório quando o tipo de emitente é Intermediário");
        }
    }

    public String getVersao() {
        return versao;
    }

    public ChaveDPS getChaveDPS() {
        return chaveDPS;
    }

    public TipoAmbiente getTipoAmbiente() {
        return tipoAmbiente;
    }

    public OffsetDateTime getDataHoraEmissao() {
        return dataHoraEmissao;
    }

    public String getVersaoAplicativo() {
        return versaoAplicativo;
    }

    public String getSerie() {
        return serie;
    }

    public long getNumeroDPS() {
        return numeroDPS;
    }

    public LocalDate getDataCompetencia() {
        return dataCompetencia;
    }

    public TipoEmitente getTipoEmitente() {
        return tipoEmitente;
    }

    public String getCodigoLocalEmissao() {
        return codigoLocalEmissao;
    }

    public Substituicao getSubstituicao() {
        return substituicao;
    }

    public Prestador getPrestador() {
        return prestador;
    }

    public Tomador getTomador() {
        return tomador;
    }

    public Intermediario getIntermediario() {
        return intermediario;
    }

    public Servico getServico() {
        return servico;
    }

    public Valores getValores() {
        return valores;
    }

    public InformacaoAdicional getInformacaoAdicional() {
        return informacaoAdicional;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String versao = "1.00";
        private TipoAmbiente tipoAmbiente = TipoAmbiente.HOMOLOGACAO;
        private OffsetDateTime dataHoraEmissao = OffsetDateTime.now();
        private String versaoAplicativo = "NFSENACIONAL-SDK-1.0";
        private String serie;
        private long numeroDPS;
        private LocalDate dataCompetencia = LocalDate.now();
        private TipoEmitente tipoEmitente = TipoEmitente.PRESTADOR;
        private String codigoLocalEmissao;
        private Substituicao substituicao;
        private Prestador prestador;
        private Tomador tomador;
        private Intermediario intermediario;
        private Servico servico;
        private Valores valores;
        private InformacaoAdicional informacaoAdicional;

        public Builder versao(String versao) {
            this.versao = versao;
            return this;
        }

        public Builder tipoAmbiente(TipoAmbiente tipoAmbiente) {
            this.tipoAmbiente = tipoAmbiente;
            return this;
        }

        public Builder dataHoraEmissao(OffsetDateTime dataHoraEmissao) {
            this.dataHoraEmissao = dataHoraEmissao;
            return this;
        }

        public Builder versaoAplicativo(String versaoAplicativo) {
            this.versaoAplicativo = versaoAplicativo;
            return this;
        }

        public Builder serie(String serie) {
            this.serie = serie;
            return this;
        }

        public Builder numeroDPS(long numeroDPS) {
            this.numeroDPS = numeroDPS;
            return this;
        }

        public Builder dataCompetencia(LocalDate dataCompetencia) {
            this.dataCompetencia = dataCompetencia;
            return this;
        }

        public Builder tipoEmitente(TipoEmitente tipoEmitente) {
            this.tipoEmitente = tipoEmitente;
            return this;
        }

        public Builder codigoLocalEmissao(String codigoLocalEmissao) {
            this.codigoLocalEmissao = codigoLocalEmissao;
            return this;
        }

        public Builder substituicao(Substituicao substituicao) {
            this.substituicao = substituicao;
            return this;
        }

        public Builder prestador(Prestador prestador) {
            this.prestador = prestador;
            return this;
        }

        public Builder tomador(Tomador tomador) {
            this.tomador = tomador;
            return this;
        }

        public Builder intermediario(Intermediario intermediario) {
            this.intermediario = intermediario;
            return this;
        }

        public Builder servico(Servico servico) {
            this.servico = servico;
            return this;
        }

        public Builder valores(Valores valores) {
            this.valores = valores;
            return this;
        }

        public Builder informacaoAdicional(InformacaoAdicional informacaoAdicional) {
            this.informacaoAdicional = informacaoAdicional;
            return this;
        }

        public DPS build() {
            return new DPS(this);
        }
    }
}
