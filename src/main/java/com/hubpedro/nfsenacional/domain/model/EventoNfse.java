package com.hubpedro.nfsenacional.domain.model;

import com.hubpedro.nfsenacional.domain.enums.TipoAmbiente;
import com.hubpedro.nfsenacional.domain.enums.TipoEventoNfse;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Entidade que encapsula qualquer evento fiscal registrado na NFS-e Nacional.
 */
public final class EventoNfse {

    private final String chaveAcessoNfse;
    private final TipoEventoNfse tipoEvento;
    private final String cnpjOuCpfAutor;
    private final TipoAmbiente tipoAmbiente;
    private final OffsetDateTime dataHoraEvento;
    private final int sequencialEvento;
    private final String detalheEventoXml;

    public EventoNfse(String chaveAcessoNfse, TipoEventoNfse tipoEvento, String cnpjOuCpfAutor,
                      TipoAmbiente tipoAmbiente, OffsetDateTime dataHoraEvento, int sequencialEvento,
                      String detalheEventoXml) {
        this.chaveAcessoNfse = Objects.requireNonNull(chaveAcessoNfse, "Chave de acesso é obrigatória");
        this.tipoEvento = Objects.requireNonNull(tipoEvento, "Tipo de evento é obrigatório");
        this.cnpjOuCpfAutor = Objects.requireNonNull(cnpjOuCpfAutor, "Autor é obrigatório");
        this.tipoAmbiente = tipoAmbiente != null ? tipoAmbiente : TipoAmbiente.HOMOLOGACAO;
        this.dataHoraEvento = dataHoraEvento != null ? dataHoraEvento : OffsetDateTime.now();
        this.sequencialEvento = sequencialEvento > 0 ? sequencialEvento : 1;
        this.detalheEventoXml = detalheEventoXml;
    }

    public String getIdXml() {
        String seqStr = String.format("%03d", sequencialEvento);
        String codEvt = tipoEvento.getCodigo().replace("e", "");
        return "EVT" + chaveAcessoNfse + codEvt + seqStr;
    }

    public String getChaveAcessoNfse() {
        return chaveAcessoNfse;
    }

    public TipoEventoNfse getTipoEvento() {
        return tipoEvento;
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

    public String getDetalheEventoXml() {
        return detalheEventoXml;
    }
}
