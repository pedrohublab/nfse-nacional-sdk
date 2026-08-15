package com.hubpedro.nfsenacional.domain.enums;

/**
 * Indicador de prestação de informações relativas ao total de tributos.
 */
public enum IndicadorTotalTributos {

    NAO("0", "Não informar valor total de tributos"),
    SIM("1", "Informar valor total de tributos");

    private final String codigo;
    private final String descricao;

    IndicadorTotalTributos(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static IndicadorTotalTributos fromCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código do indicador de total de tributos não pode ser vazio");
        }
        for (IndicadorTotalTributos valor : values()) {
            if (valor.codigo.equals(codigo.trim())) {
                return valor;
            }
        }
        throw new IllegalArgumentException("Indicador de total de tributos inválido: " + codigo);
    }
}
