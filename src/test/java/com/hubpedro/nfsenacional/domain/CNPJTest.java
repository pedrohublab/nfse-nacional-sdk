package com.hubpedro.nfsenacional.domain;

import com.hubpedro.nfsenacional.domain.exception.CnpjInvalidoException;
import com.hubpedro.nfsenacional.domain.valueobject.CNPJ;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes do Value Object CNPJ (Numérico e Alfanumérico - NT 009)")
public class CNPJTest {

    @Test
    @DisplayName("Deve validar e formatar CNPJ numérico tradicional válido")
    void deveValidarCnpjNumericoValido() {
        // CNPJ válido com pontuação
        CNPJ cnpj = new CNPJ("11.222.333/0001-81");
        assertThat(cnpj.getNumero()).isEqualTo("11222333000181");
        assertThat(cnpj.valor()).isEqualTo("11222333000181");
        assertThat(cnpj.getFormatado()).isEqualTo("11.222.333/0001-81");
        assertThat(cnpj.formatado()).isEqualTo("11.222.333/0001-81");
        assertThat(cnpj.toString()).isEqualTo("11.222.333/0001-81");

        // CNPJ válido somente números
        CNPJ cnpj2 = new CNPJ("11222333000181");
        assertThat(cnpj2.getNumero()).isEqualTo("11222333000181");
        assertThat(cnpj2).isEqualTo(cnpj);
        assertThat(CNPJ.validar("11.222.333/0001-81")).isTrue();
        assertThat(CNPJ.validar("11222333000181")).isTrue();
    }

    @Test
    @DisplayName("Deve validar e formatar CNPJ alfanumérico conforme IN RFB 2.229/2024 e NT 009")
    void deveValidarCnpjAlfanumericoValido() {
        // Exemplo 1: 12.ABC.345/01DE-35
        CNPJ cnpjAlfa1 = new CNPJ("12.ABC.345/01DE-35");
        assertThat(cnpjAlfa1.getNumero()).isEqualTo("12ABC34501DE35");
        assertThat(cnpjAlfa1.formatado()).isEqualTo("12.ABC.345/01DE-35");
        assertThat(CNPJ.validar("12.ABC.345/01DE-35")).isTrue();
        assertThat(CNPJ.validar("12ABC34501DE35")).isTrue();

        // Normalização de minúsculas para maiúsculas
        CNPJ cnpjMinusc = new CNPJ("12abc34501de35");
        assertThat(cnpjMinusc.getNumero()).isEqualTo("12ABC34501DE35");
        assertThat(cnpjMinusc).isEqualTo(cnpjAlfa1);

        // Exemplo 2: AB.12C.D34/EF56-02
        CNPJ cnpjAlfa2 = new CNPJ("AB.12C.D34/EF56-02");
        assertThat(cnpjAlfa2.getNumero()).isEqualTo("AB12CD34EF5602");
        assertThat(cnpjAlfa2.formatado()).isEqualTo("AB.12C.D34/EF56-02");
        assertThat(CNPJ.validar("AB.12C.D34/EF56-02")).isTrue();
    }

    @Test
    @DisplayName("Deve rejeitar CNPJ com dígitos verificadores inválidos")
    void deveRejeitarCnpjInvalido() {
        // Numérico com DV inválido
        assertThatThrownBy(() -> new CNPJ("11222333000199"))
                .isInstanceOf(CnpjInvalidoException.class);
        assertThat(CNPJ.validar("11222333000199")).isFalse();

        // Alfanumérico com DV inválido
        assertThatThrownBy(() -> new CNPJ("12ABC34501DE99"))
                .isInstanceOf(CnpjInvalidoException.class);
        assertThat(CNPJ.validar("12ABC34501DE99")).isFalse();
    }

    @Test
    @DisplayName("Deve rejeitar CNPJ com caracteres repetidos conhecidos")
    void deveRejeitarCnpjRepetido() {
        assertThatThrownBy(() -> new CNPJ("00000000000000"))
                .isInstanceOf(CnpjInvalidoException.class);
        assertThatThrownBy(() -> new CNPJ("11111111111111"))
                .isInstanceOf(CnpjInvalidoException.class);
        assertThatThrownBy(() -> new CNPJ("AAAAAAAAAAAAAA"))
                .isInstanceOf(CnpjInvalidoException.class);

        assertThat(CNPJ.validar("00000000000000")).isFalse();
        assertThat(CNPJ.validar("11111111111111")).isFalse();
    }

    @Test
    @DisplayName("Deve rejeitar CNPJ com tamanho ou formato incompatível")
    void deveRejeitarTamanhoEFormatoInvalido() {
        assertThatThrownBy(() -> new CNPJ("123"))
                .isInstanceOf(CnpjInvalidoException.class);
        assertThatThrownBy(() -> new CNPJ(null))
                .isInstanceOf(CnpjInvalidoException.class);
        assertThatThrownBy(() -> new CNPJ("   "))
                .isInstanceOf(CnpjInvalidoException.class);

        assertThat(CNPJ.validar(null)).isFalse();
        assertThat(CNPJ.validar("")).isFalse();
        assertThat(CNPJ.validar("123")).isFalse();
    }
}
