package com.hubpedro.nfsenacional.domain.model;

import com.hubpedro.nfsenacional.domain.enums.TipoAmbiente;
import com.hubpedro.nfsenacional.domain.enums.TipoEventoNfse;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Representa um Pedido de Cancelamento de NFS-e (Evento e101101).
 */
public final class PedidoCancelamento {

    private final String chaveAcessoNfse;
    private final String cnpjOuCpfAutor;
    private final TipoAmbiente tipoAmbiente;
    private final OffsetDateTime dataHoraEvento;
    private final int sequencialEvento;
    private final String codigoMotivo;
    private final String descricaoMotivo;

    private PedidoCancelamento(Builder builder) {
        this.chaveAcessoNfse = Objects.requireNonNull(builder.chaveAcessoNfse, "Chave de acesso da NFS-e é obrigatória");
        this.cnpjOuCpfAutor = Objects.requireNonNull(builder.cnpjOuCpfAutor, "CNPJ/CPF do autor é obrigatório");
        this.tipoAmbiente = builder.tipoAmbiente != null ? builder.tipoAmbiente : TipoAmbiente.HOMOLOGACAO;
        this.dataHoraEvento = builder.dataHoraEvento != null ? builder.dataHoraEvento : OffsetDateTime.now();
        this.sequencialEvento = builder.sequencialEvento > 0 ? builder.sequencialEvento : 1;
        this.codigoMotivo = Objects.requireNonNull(builder.codigoMotivo, "Código do motivo de cancelamento é obrigatório");
        this.descricaoMotivo = builder.descricaoMotivo;
        validar();
    }

    private void validar() {
        if (!chaveAcessoNfse.matches("\\d{50}")) {
            throw new IllegalArgumentException("Chave de acesso da NFS-e deve ter exatamente 50 dígitos: " + chaveAcessoNfse);
        }
        if (codigoMotivo.isBlank()) {
            throw new IllegalArgumentException("Código do motivo não pode ser vazio");
        }
    }

    /**
     * Retorna o ID único para a tag infPedReg: 'EVT' + chaveAcesso(50) + tpEvento(6) + seq(3)
     */
    public String getIdXml() {
        String seqStr = String.format("%03d", sequencialEvento);
        return "EVT" + chaveAcessoNfse + "101101" + seqStr;
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
        return TipoEventoNfse.CANCELAMENTO;
    }

    public String getCodigoMotivo() {
        return codigoMotivo;
    }

    public String getDescricaoMotivo() {
        return descricaoMotivo;
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

        public PedidoCancelamento build() {
            return new PedidoCancelamento(this);
        }
    }
}
