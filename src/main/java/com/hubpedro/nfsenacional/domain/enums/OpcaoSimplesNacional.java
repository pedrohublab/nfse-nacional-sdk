package com.hubpedro.nfsenacional.domain.enums;

/**
 * Opção pelo Simples Nacional do prestador.
 */
public enum OpcaoSimplesNacional {

    SIM("1", "Optante"),
    NAO("2", "Não optante");

    private final String codigo;
    private final String descricao;

    OpcaoSimplesNacional(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static OpcaoSimplesNacional fromCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código da opção do Simples Nacional não pode ser vazio");
        }
        for (OpcaoSimplesNacional valor : values()) {
            if (valor.codigo.equals(codigo.trim())) {
                return valor;
            }
        }
        throw new IllegalArgumentException("Opção do Simples Nacional inválida: " + codigo);
    }
}
