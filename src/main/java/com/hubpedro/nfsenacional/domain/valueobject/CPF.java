package com.hubpedro.nfsenacional.domain.valueobject;

import com.hubpedro.nfsenacional.domain.exception.CpfInvalidoException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object que representa um CPF válido.
 * Imutável e auto-validado na construção.
 */
public final class CPF {

    private static final Pattern NON_DIGITS = Pattern.compile("[^0-9]");
    private final String numero;

    public CPF(String numero) {
        String limpo = limpar(numero);
        validar(limpo);
        this.numero = limpo;
    }

    private static String limpar(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new CpfInvalidoException("CPF não pode ser nulo ou vazio");
        }
        return NON_DIGITS.matcher(cpf).replaceAll("");
    }

    private static void validar(String cpf) {
        if (cpf.length() != 11) {
            throw new CpfInvalidoException("CPF deve ter 11 dígitos: " + cpf);
        }

        if (todosDigitosIguais(cpf)) {
            throw new CpfInvalidoException("CPF inválido (dígitos repetidos): " + cpf);
        }

        int primeiroDigito = calcularDigito(cpf.substring(0, 9), 10);
        int segundoDigito = calcularDigito(cpf.substring(0, 10), 11);

        String digitosCalculados = String.valueOf(primeiroDigito) + segundoDigito;
        String digitosInformados = cpf.substring(9);

        if (!digitosCalculados.equals(digitosInformados)) {
            throw new CpfInvalidoException("CPF inválido (dígitos verificadores não conferem): " + cpf);
        }
    }

    private static boolean todosDigitosIguais(String cpf) {
        char primeiro = cpf.charAt(0);
        for (int i = 1; i < cpf.length(); i++) {
            if (cnpjOuCpfDiferente(cpf.charAt(i), primeiro)) {
                return false;
            }
        }
        return true;
    }

    private static boolean cnpjOuCpfDiferente(char c1, char c2) {
        return c1 != c2;
    }

    private static int calcularDigito(String base, int pesoInicial) {
        int soma = 0;
        int peso = pesoInicial;
        for (int i = 0; i < base.length(); i++) {
            int digito = Character.getNumericValue(base.charAt(i));
            soma += digito * peso;
            peso--;
        }
        int resto = soma % 11;
        return (resto < 2) ? 0 : 11 - resto;
    }

    /**
     * Retorna o CPF sem máscara (11 dígitos).
     */
    public String getNumero() {
        return numero;
    }

    public String semMascara() {
        return numero;
    }

    /**
     * Retorna o CPF formatado: 000.000.000-00
     */
    public String getFormatado() {
        return numero.substring(0, 3) + "." +
               numero.substring(3, 6) + "." +
               numero.substring(6, 9) + "-" +
               numero.substring(9);
    }

    public String formatado() {
        return getFormatado();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CPF cpf = (CPF) o;
        return Objects.equals(numero, cpf.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

    @Override
    public String toString() {
        return getFormatado();
    }
}
