package com.hubpedro.nfsenacional.domain.valueobject;

import com.hubpedro.nfsenacional.domain.exception.CnpjInvalidoException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object que representa um CNPJ válido.
 * Imutável e auto-validado na construção.
 */
public final class CNPJ {

    private static final Pattern NON_DIGITS = Pattern.compile("[^0-9]");
    private final String valor;

    public CNPJ(String valor) {
        String limpo = limpar(valor);
        validar(limpo);
        this.valor = limpo;
    }

    private static String limpar(String cnpj) {
        if (cnpj == null || cnpj.isBlank()) {
            throw new CnpjInvalidoException("CNPJ não pode ser nulo ou vazio");
        }
        return NON_DIGITS.matcher(cnpj).replaceAll("");
    }

    private static void validar(String cnpj) {
        if (cnpj.length() != 14) {
            throw new CnpjInvalidoException("CNPJ deve ter 14 dígitos: " + cnpj);
        }

        if (todosDigitosIguais(cnpj)) {
            throw new CnpjInvalidoException("CNPJ inválido (dígitos repetidos): " + cnpj);
        }

        int primeiroDigito = calcularDigito(cnpj.substring(0, 12), new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        int segundoDigito = calcularDigito(cnpj.substring(0, 13), new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});

        String digitosCalculados = String.valueOf(primeiroDigito) + segundoDigito;
        String digitosInformados = cnpj.substring(12);

        if (!digitosCalculados.equals(digitosInformados)) {
            throw new CnpjInvalidoException("CNPJ inválido (dígitos verificadores não conferem): " + cnpj);
        }
    }

    private static boolean todosDigitosIguais(String cnpj) {
        char primeiro = cnpj.charAt(0);
        for (int i = 1; i < cnpj.length(); i++) {
            if (cnpj.charAt(i) != primeiro) {
                return false;
            }
        }
        return true;
    }

    private static int calcularDigito(String base, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            int digito = Character.getNumericValue(base.charAt(i));
            soma += digito * pesos[i];
        }
        int resto = soma % 11;
        return (resto < 2) ? 0 : 11 - resto;
    }

    /**
     * Retorna o CNPJ sem formatação (14 dígitos).
     */
    public String getNumero() {
        return valor;
    }

    public String getValor() {
        return valor;
    }

    /**
     * Retorna o CNPJ formatado: XX.XXX.XXX/XXXX-XX
     */
    public String getFormatado() {
        return valor.substring(0, 2) + "." +
               valor.substring(2, 5) + "." +
               valor.substring(5, 8) + "/" +
               valor.substring(8, 12) + "-" +
               valor.substring(12);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CNPJ cnpj = (CNPJ) o;
        return Objects.equals(valor, cnpj.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return getFormatado();
    }
}
