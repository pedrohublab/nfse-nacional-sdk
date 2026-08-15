package com.hubpedro.nfsenacional.domain.enums;

/**
 * Ambiente de operação na SEFIN Nacional.
 */
public enum TipoAmbiente {

    PRODUCAO("1", "Produção"),
    HOMOLOGACAO("2", "Produção Restrita / Homologação");

    private final String codigo;
    private final String descricao;

    TipoAmbiente(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoAmbiente fromCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código do tipo de ambiente não pode ser vazio");
        }
        for (TipoAmbiente valor : values()) {
            if (valor.codigo.equals(codigo.trim())) {
                return valor;
            }
        }
        throw new IllegalArgumentException("Tipo de ambiente inválido: " + codigo);
    }
}
