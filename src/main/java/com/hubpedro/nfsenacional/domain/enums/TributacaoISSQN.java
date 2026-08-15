package com.hubpedro.nfsenacional.domain.enums;

/**
 * Forma de tributação do ISSQN.
 */
public enum TributacaoISSQN {

    OPERACAO_NORMAL("1", "Operação normal"),
    IMUNIDADE("2", "Imunidade"),
    EXPORTACAO_SERVICO("3", "Exportação de serviço"),
    NAO_INCIDENCIA("4", "Não incidência"),
    ISS_FIXO("5", "ISS fixo"),
    PROCESSO_JUDICIAL("6", "Processo judicial");

    private final String codigo;
    private final String descricao;

    TributacaoISSQN(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TributacaoISSQN fromCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código de tributação ISSQN não pode ser vazio");
        }
        for (TributacaoISSQN valor : values()) {
            if (valor.codigo.equals(codigo.trim())) {
                return valor;
            }
        }
        throw new IllegalArgumentException("Tributação ISSQN inválida: " + codigo);
    }
}
