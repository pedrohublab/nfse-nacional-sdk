package com.hubpedro.nfsenacional.domain.model;

import com.hubpedro.nfsenacional.domain.enums.TipoAmbiente;
import com.hubpedro.nfsenacional.domain.enums.TipoEventoNfse;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Representa um Pedido de Substituição de NFS-e (Evento e101103).
 */
public final class PedidoSubstituicao {

    private final String chaveAcessoNfse;
    private final String cnpjOuCpfAutor;
    private final TipoAmbiente tipoAmbiente;
    private final OffsetDateTime dataHoraEvento;
    private final int sequencialEvento;
    private final String codigoMotivo;
    private final String descricaoMotivo;
    private final String chaveDpsSubstituta;

    private PedidoSubstituicao(Builder builder) {
        this.chaveAcessoNfse = Objects.requireNonNull(builder.chaveAcessoNfse, "Chave de acesso da NFS-e é obrigatória");
        this.cnpjOuCpfAutor = Objects.requireNonNull(builder.cnpjOuCpfAutor, "CNPJ/CPF do autor é obrigatório");
        this.tipoAmbiente = builder.tipoAmbiente != null ? builder.tipoAmbiente : TipoAmbiente.HOMOLOGACAO;
        this.dataHoraEvento = builder.dataHoraEvento != null ? builder.dataHoraEvento : OffsetDateTime.now();
        this.sequencialEvento = builder.sequencialEvento > 0 ? builder.sequencialEvento : 1;
        this.codigoMotivo = Objects.requireNonNull(builder.codigoMotivo, "Código do motivo de substituição é obrigatório");
        this.descricaoMotivo = builder.descricaoMotivo;
        this.chaveDpsSubstituta = Objects.requireNonNull(builder.chaveDpsSubstituta, "Chave da DPS substituta é obrigatória");
        validar();
    }

    private void validar() {
        if (!chaveAcessoNfse.matches("\\d{50}")) {
            throw new IllegalArgumentException("Chave de acesso da NFS-e deve ter exatamente 50 dígitos: " + chaveAcessoNfse);
        }
        if (codigoMotivo.isBlank()) {
            throw new IllegalArgumentException("Código do motivo não pode ser vazio");
        }
        if (chaveDpsSubstituta.isBlank()) {
            throw new IllegalArgumentException("Chave da DPS substituta não pode ser vazia");
        }
    }

    public String getIdXml() {
        String seqStr = String.format("%03d", sequencialEvento);
        return "EVT" + chaveAcessoNfse + "101103" + seqStr;
    }

    public String getChaveAcessoNfse() {
        return chaveAcessoNfse;
    }

    public String getCnpjOuCpfAutor() {
        return cnpjOuCpfAutor;
    }

    public TipoAmbiente getTipoAmbiente() {
        return tipoAmbiente;
    }

    public OffsetDateTime getDataHoraEvento() {
        return dataHoraEvento;
    }

    public int getSequencialEvento() {
        return sequencialEvento;
    }

    public TipoEventoNfse getTipoEvento() {
        return TipoEventoNfse.CANCELAMENTO_POR_SUBSTITUICAO;
    }

    public String getCodigoMotivo() {
        return codigoMotivo;
    }

    public String getDescricaoMotivo() {
        return descricaoMotivo;
    }

    public String getChaveDpsSubstituta() {
        return chaveDpsSubstituta;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String chaveAcessoNfse;
        private String cnpjOuCpfAutor;
        private TipoAmbiente tipoAmbiente = TipoAmbiente.HOMOLOGACAO;
        private OffsetDateTime dataHoraEvento = OffsetDateTime.now();
        private int sequencialEvento = 1;
        private String codigoMotivo = "1";
        private String descricaoMotivo;
        private String chaveDpsSubstituta;

        public Builder chaveAcessoNfse(String chaveAcessoNfse) {
            this.chaveAcessoNfse = chaveAcessoNfse;
            return this;
        }

        public Builder cnpjOuCpfAutor(String cnpjOuCpfAutor) {
            this.cnpjOuCpfAutor = cnpjOuCpfAutor;
            return this;
        }

        public Builder tipoAmbiente(TipoAmbiente tipoAmbiente) {
            this.tipoAmbiente = tipoAmbiente;
            return this;
        }

        public Builder dataHoraEvento(OffsetDateTime dataHoraEvento) {
            this.dataHoraEvento = dataHoraEvento;
            return this;
        }

        public Builder sequencialEvento(int sequencialEvento) {
            this.sequencialEvento = sequencialEvento;
            return this;
        }

        public Builder codigoMotivo(String codigoMotivo) {
            this.codigoMotivo = codigoMotivo;
            return this;
        }

        public Builder descricaoMotivo(String descricaoMotivo) {
            this.descricaoMotivo = descricaoMotivo;
            return this;
        }

        public Builder chaveDpsSubstituta(String chaveDpsSubstituta) {
            this.chaveDpsSubstituta = chaveDpsSubstituta;
            return this;
        }

        public PedidoSubstituicao build() {
            return new PedidoSubstituicao(this);
        }
    }
}
