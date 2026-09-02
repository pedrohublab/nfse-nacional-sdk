package com.hubpedro.nfsenacional.domain.valueobject;

import com.hubpedro.nfsenacional.domain.enums.DpsEmissionStatus;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Chave da DPS (Declaração de Prestação de Serviços).
 * Formato: codigoMunicipio(7) + AAAAMMDD(8) + cnpj(14) + serie(5) + numeroDPS(15) + digito(1) = 50 chars.
 * Imutável e auto-validado.
 */
public final class ChaveDPS {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final String chaveCalculada;
    private final String codigoMunicipio;
    private final OffsetDateTime dataEmissao;
    private final String cnpjPrestador;
    private final String serie;
    private final long numeroDPS;
    private DpsEmissionStatus status;

    public static ChaveDPS of(String chave50) {
        if (chave50 == null || !chave50.matches("[0-9A-Za-z]{50}")) {
            throw new IllegalArgumentException("Chave da DPS deve ter exatamente 50 caracteres: " + chave50);
        }
        return new ChaveDPS(chave50);
    }

    public static String computar(String codigoMunicipio, OffsetDateTime dataEmissao,
                                  String cnpjPrestador, String serie, long numeroDPS) {
        return new ChaveDPS(codigoMunicipio, dataEmissao, cnpjPrestador, serie, numeroDPS).valor();
    }

    public static String computar(String codigoMunicipio, OffsetDateTime dataEmissao,
                                  String cnpjPrestador, String serie, long numeroDPS, DpsEmissionStatus status) {
        return new ChaveDPS(codigoMunicipio, dataEmissao, cnpjPrestador, serie, numeroDPS).valor();
    }

    private ChaveDPS(String chave50) {
        this.chaveCalculada = chave50;
        this.codigoMunicipio = chave50.substring(0, 7);
        this.dataEmissao = null;
        this.cnpjPrestador = chave50.substring(15, 29);
        this.serie = chave50.substring(29, 34);
        this.numeroDPS = Long.parseLong(chave50.substring(34, 49));
    }

    public ChaveDPS(String codigoMunicipio, OffsetDateTime dataEmissao, String cnpjPrestador,
                    String serie, long numeroDPS) {
        this.codigoMunicipio = Objects.requireNonNull(codigoMunicipio, "Código do município é obrigatório");
        this.dataEmissao = Objects.requireNonNull(dataEmissao, "Data de emissão é obrigatória");
        this.cnpjPrestador = Objects.requireNonNull(cnpjPrestador, "CNPJ do prestador é obrigatório");
        this.serie = Objects.requireNonNull(serie, "Série é obrigatória");
        this.numeroDPS = numeroDPS;
        validar();
        this.chaveCalculada = calcularChave(codigoMunicipio, dataEmissao, cnpjPrestador, serie, numeroDPS);
    }

    private void validar() {
        if (!codigoMunicipio.matches("\\d{7}")) {
            throw new IllegalArgumentException("Código do município deve ter 7 dígitos: " + codigoMunicipio);
        }
        if (!cnpjPrestador.matches("[0-9A-Za-z]{14}")) {
            throw new IllegalArgumentException("CNPJ do prestador deve ter 14 caracteres: " + cnpjPrestador);
        }
        if (serie.isEmpty() || serie.length() > 5) {
            throw new IllegalArgumentException("Série deve ter entre 1 e 5 caracteres: " + serie);
        }
        if (numeroDPS <= 0) {
            throw new IllegalArgumentException("Número da DPS deve ser maior que zero: " + numeroDPS);
        }
    }

    private static String calcularChave(String codigoMunicipio, OffsetDateTime dataEmissao,
                                        String cnpjPrestador, String serie, long numeroDPS) {
        String dataParte = dataEmissao.format(DATE_FMT);
        String serieFormatada = padLeft(serie, 5);
        String numeroFormatado = padLeft(String.valueOf(numeroDPS), 15);

        String chaveBase = codigoMunicipio + dataParte + cnpjPrestador + serieFormatada + numeroFormatado;
        int digitoVerificador = calcularDigitoVerificador(chaveBase);

        return chaveBase + digitoVerificador;
    }

    /**
     * Retorna a chave completa com 50 caracteres numéricos.
     */
    public String valor() {
        return chaveCalculada;
    }

    /**
     * Retorna o identificador XML padrão da DPS: 'DPS' + chave de 50 dígitos.
     */
    public String getIdXml() {
        return "DPS" + valor();
    }

    private static int calcularDigitoVerificador(String chaveBase) {
        int[] pesos = {2, 3, 4, 5, 6, 7, 8, 9};
        int soma = 0;
        int indicePeso = 0;

        for (int i = chaveBase.length() - 1; i >= 0; i--) {
            char c = chaveBase.charAt(i);
            int digito = (c >= 'A' && c <= 'Z') ? (c - 48) : ((c >= 'a' && c <= 'z') ? (c - 80) : Character.getNumericValue(c));
            soma += digito * pesos[indicePeso % pesos.length];
            indicePeso++;
        }

        int resto = soma % 11;
        return (resto < 2) ? 0 : 11 - resto;
    }

    private static String padLeft(String input, int length) {
        if (input.length() >= length) {
            return input.substring(0, length);
        }
        int padLen = length - input.length();
        return "0".repeat(padLen) + input;
    }

    public String getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public OffsetDateTime getDataEmissao() {
        return dataEmissao;
    }

    public String getCnpjPrestador() {
        return cnpjPrestador;
    }

    public String getSerie() {
        return serie;
    }

    public long getNumeroDPS() {
        return numeroDPS;
    }

    public DpsEmissionStatus getStatus() {
        return status;
    }

    public void setStatus(DpsEmissionStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChaveDPS chaveDPS = (ChaveDPS) o;
        return Objects.equals(valor(), chaveDPS.valor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor());
    }

    @Override
    public String toString() {
        return valor();
    }
}
