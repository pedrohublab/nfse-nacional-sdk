package com.hubpedro.nfsenacional.domain.enums;

/**
 * Regime especial de tributação do prestador de serviços.
 */
public enum RegimeEspecialTributacao {

    NENHUM("0", "Nenhum"),
    MICROEMPRESA_MUNICIPAL("1", "Microempresa Municipal"),
    ESTIMATIVA("2", "Estimativa"),
    SOCIEDADE_PROFISSIONAIS("3", "Sociedade de Profissionais"),
    COOPERATIVA("4", "Cooperativa"),
    MEI("5", "Microempreendedor Individual (MEI)"),
    ME_EPP("6", "Microempresa e Empresa de Pequeno Porte (ME/EPP)");

    private final String codigo;
    private final String descricao;

    RegimeEspecialTributacao(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static RegimeEspecialTributacao fromCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código do regime especial de tributação não pode ser vazio");
        }
        for (RegimeEspecialTributacao valor : values()) {
            if (valor.codigo.equals(codigo.trim())) {
                return valor;
            }
        }
        throw new IllegalArgumentException("Regime especial de tributação inválido: " + codigo);
    }
}
