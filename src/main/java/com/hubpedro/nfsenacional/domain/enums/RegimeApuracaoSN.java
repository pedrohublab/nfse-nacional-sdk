package com.hubpedro.nfsenacional.domain.enums;

/**
 * Regime de apuração dos tributos federais e municipais pelo Simples Nacional.
 */
public enum RegimeApuracaoSN {

    MEI("1", "Microempreendedor Individual (MEI)"),
    SIMPLES_NACIONAL_RECEITA_BRUTA_ATE_180K("2", "Simples Nacional - receita bruta até R$ 180.000,00"),
    SIMPLES_NACIONAL_RECEITA_BRUTA_180K_A_360K("3", "Simples Nacional - receita bruta de R$ 180.000,01 a R$ 360.000,00"),
    SIMPLES_NACIONAL_RECEITA_BRUTA_360K_A_720K("4", "Simples Nacional - receita bruta de R$ 360.000,01 a R$ 720.000,00"),
    SIMPLES_NACIONAL_RECEITA_BRUTA_720K_A_1800K("5", "Simples Nacional - receita bruta de R$ 720.000,01 a R$ 1.800.000,00"),
    SIMPLES_NACIONAL_RECEITA_BRUTA_ACIMA_1800K("6", "Simples Nacional - receita bruta acima de R$ 1.800.000,00");

    private final String codigo;
    private final String descricao;

    RegimeApuracaoSN(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static RegimeApuracaoSN fromCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código do regime de apuração do Simples Nacional não pode ser vazio");
        }
        for (RegimeApuracaoSN valor : values()) {
            if (valor.codigo.equals(codigo.trim())) {
                return valor;
            }
        }
        throw new IllegalArgumentException("Regime de apuração do Simples Nacional inválido: " + codigo);
    }
}
