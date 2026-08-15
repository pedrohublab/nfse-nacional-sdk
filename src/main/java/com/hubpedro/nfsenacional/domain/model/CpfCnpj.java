package com.hubpedro.nfsenacional.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object polimórfico que representa um CPF ou CNPJ válido.
 */
public final class CpfCnpj {

    private static final Pattern NON_DIGITS = Pattern.compile("[^0-9]");

    private final Tipo tipo;
    private final String numero;

    private CpfCnpj(Tipo tipo, String numero) {
        this.tipo = Objects.requireNonNull(tipo, "Tipo é obrigatório");
        this.numero = limpar(Objects.requireNonNull(numero, "Número é obrigatório"));
        validar();
    }

    private static String limpar(String valor) {
        return NON_DIGITS.matcher(valor).replaceAll("");
    }

    private void validar() {
        if (tipo == Tipo.CPF && numero.length() != 11) {
            throw new IllegalArgumentException("CPF deve ter 11 dígitos");
        }
        if (tipo == Tipo.CNPJ && numero.length() != 14) {
            throw new IllegalArgumentException("CNPJ deve ter 14 dígitos");
        }
    }

    /**
     * Detecta automaticamente se é CPF ou CNPJ pela quantidade de dígitos numéricos.
     */
    public static CpfCnpj of(String numero) {
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("Número de CPF/CNPJ não pode ser nulo ou vazio");
        }
        String limpo = NON_DIGITS.matcher(numero).replaceAll("");
        if (limpo.length() == 11) {
            return cpf(limpo);
        } else if (limpo.length() == 14) {
            return cnpj(limpo);
        }
        throw new IllegalArgumentException("Número inválido: deve ter 11 dígitos (CPF) ou 14 dígitos (CNPJ)");
    }

    public static CpfCnpj cpf(String cpf) {
        return new CpfCnpj(Tipo.CPF, cpf);
    }

    public static CpfCnpj cnpj(String cnpj) {
        return new CpfCnpj(Tipo.CNPJ, cnpj);
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getNumero() {
        return numero;
    }

    public boolean isCpf() {
        return tipo == Tipo.CPF;
    }

    public boolean isCnpj() {
        return tipo == Tipo.CNPJ;
    }

    public String formatado() {
        if (tipo == Tipo.CPF) {
            return numero.substring(0, 3) + "." +
                   numero.substring(3, 6) + "." +
                   numero.substring(6, 9) + "-" +
                   numero.substring(9);
        } else {
            return numero.substring(0, 2) + "." +
                   numero.substring(2, 5) + "." +
                   numero.substring(5, 8) + "/" +
                   numero.substring(8, 12) + "-" +
                   numero.substring(12);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CpfCnpj cpfCnpj = (CpfCnpj) o;
        return tipo == cpfCnpj.tipo && Objects.equals(numero, cpfCnpj.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipo, numero);
    }

    @Override
    public String toString() {
        return formatado();
    }

    public enum Tipo {
        CPF, CNPJ
    }
}
