package com.hubpedro.nfsenacional.domain.valueobject;

import com.hubpedro.nfsenacional.domain.exception.CnpjInvalidoException;

import java.util.regex.Pattern;

/**
 * Value Object imutável que representa um CNPJ válido conforme as normas da Receita Federal do Brasil.
 * <p>
 * Suporta tanto o padrão numérico tradicional (14 dígitos) quanto o novo padrão alfanumérico
 * regulamentado pela Reforma Tributária (IN RFB nº 2.229/2024 e NT SE/CGNFS-e nº 009).
 * <p>
 * <b>Estrutura (14 posições):</b>
 * <ul>
 *   <li>8 posições: Raiz ([0-9A-Z]{8})</li>
 *   <li>4 posições: Ordem/Filial ([0-9A-Z]{4})</li>
 *   <li>2 posições: Dígitos Verificadores estritamente numéricos ([0-9]{2})</li>
 * </ul>
 * <p>
 * <b>Algoritmo de Cálculo (Módulo 11 com Conversão ASCII - 48):</b>
 * <ul>
 *   <li>Dígitos '0'-'9' (ASCII 48..57) &rarr; 0..9</li>
 *   <li>Letras 'A'-'Z' (ASCII 65..90) &rarr; 17..42</li>
 *   <li>Pesos DV1: [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]</li>
 *   <li>Pesos DV2: [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]</li>
 * </ul>
 */
public record CNPJ(String valor) {

    private static final Pattern PADRAO_FORMATADO = Pattern.compile("^[0-9A-Z]{2}\\.[0-9A-Z]{3}\\.[0-9A-Z]{3}/[0-9A-Z]{4}-[0-9]{2}$");
    private static final Pattern PADRAO_LIMPO = Pattern.compile("^[0-9A-Z]{12}[0-9]{2}$");

    private static final int[] PESOS_DV1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESOS_DV2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    public CNPJ {
        if (valor == null || valor.isBlank()) {
            throw new CnpjInvalidoException("CNPJ não pode ser nulo ou vazio");
        }

        String limpo = valor.replaceAll("[^0-9A-Za-z]", "").toUpperCase();

        if (!PADRAO_LIMPO.matcher(limpo).matches() || todosCaracteresIguais(limpo) || !validarDigitosVerificadores(limpo)) {
            throw new CnpjInvalidoException("CNPJ inválido: " + valor);
        }

        valor = limpo;
    }

    public static CNPJ of(String valor) {
        return new CNPJ(valor);
    }

    /**
     * Valida se uma string representa um CNPJ válido (numérico ou alfanumérico).
     *
     * @param cnpj a string a ser testada
     * @return {@code true} se for válido; {@code false} caso contrário
     */
    public static boolean validar(String cnpj) {
        if (cnpj == null || cnpj.isBlank()) {
            return false;
        }
        String limpo = cnpj.replaceAll("[^0-9A-Za-z]", "").toUpperCase();
        return PADRAO_LIMPO.matcher(limpo).matches()
                && !todosCaracteresIguais(limpo)
                && validarDigitosVerificadores(limpo);
    }

    private static boolean todosCaracteresIguais(String cnpj) {
        char primeiro = cnpj.charAt(0);
        for (int i = 1; i < cnpj.length(); i++) {
            if (cnpj.charAt(i) != primeiro) {
                return false;
            }
        }
        return true;
    }

    private static boolean validarDigitosVerificadores(String cnpjLimpo) {
        int dv1Calculado = calcularDv(cnpjLimpo.substring(0, 12), PESOS_DV1);
        int dv1Informado = Character.getNumericValue(cnpjLimpo.charAt(12));
        if (dv1Calculado != dv1Informado) {
            return false;
        }

        int dv2Calculado = calcularDv(cnpjLimpo.substring(0, 12) + dv1Calculado, PESOS_DV2);
        int dv2Informado = Character.getNumericValue(cnpjLimpo.charAt(13));
        return dv2Calculado == dv2Informado;
    }

    private static int calcularDv(String base, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            char c = base.charAt(i);
            // Regra oficial Receita Federal: código ASCII menos 48
            int valorChar = ((int) c) - 48;
            soma += valorChar * pesos[i];
        }
        int resto = soma % 11;
        return (resto < 2) ? 0 : 11 - resto;
    }

    /**
     * Retorna o CNPJ formatado no padrão XX.XXX.XXX/XXXX-XX.
     */
    public String formatado() {
        return String.format("%s.%s.%s/%s-%s",
                valor.substring(0, 2),
                valor.substring(2, 5),
                valor.substring(5, 8),
                valor.substring(8, 12),
                valor.substring(12, 14));
    }

    /**
     * Retorna o CNPJ sem formatação (14 caracteres alfanuméricos).
     */
    public String getNumero() {
        return valor;
    }

    /**
     * Retorna o CNPJ sem formatação (14 caracteres alfanuméricos).
     */
    public String getValor() {
        return valor;
    }

    /**
     * Retorna o CNPJ formatado: XX.XXX.XXX/XXXX-XX
     */
    public String getFormatado() {
        return formatado();
    }

    @Override
    public String toString() {
        return formatado();
    }
}
