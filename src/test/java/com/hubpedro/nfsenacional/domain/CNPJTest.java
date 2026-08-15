package com.hubpedro.nfsenacional.domain;

import com.hubpedro.nfsenacional.domain.exception.CnpjInvalidoException;
import com.hubpedro.nfsenacional.domain.valueobject.CNPJ;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes do Value Object CNPJ")
public class CNPJTest {

    @Test
    @DisplayName("Deve validar e formatar CNPJ válido")
    void deveValidarCnpjValido() {
        // CNPJ válido com pontuação
        CNPJ cnpj = new CNPJ("11.222.333/0001-81");
        assertThat(cnpj.getNumero()).isEqualTo("11222333000181");
        assertThat(cnpj.getFormatado()).isEqualTo("11.222.333/0001-81");

        // CNPJ válido somente números
        CNPJ cnpj2 = new CNPJ("11222333000181");
        assertThat(cnpj2.getNumero()).isEqualTo("11222333000181");
        assertThat(cnpj2).isEqualTo(cnpj);
    }

    @Test
    @DisplayName("Deve rejeitar CNPJ com dígitos verificadores inválidos")
    void deveRejeitarCnpjInvalido() {
        assertThatThrownBy(() -> new CNPJ("11222333000199"))
                .isInstanceOf(CnpjInvalidoException.class);
    }

    @Test
    @DisplayName("Deve rejeitar CNPJ com números repetidos conhecidos")
    void deveRejeitarCnpjRepetido() {
        assertThatThrownBy(() -> new CNPJ("00000000000000"))
                .isInstanceOf(CnpjInvalidoException.class);
        assertThatThrownBy(() -> new CNPJ("11111111111111"))
                .isInstanceOf(CnpjInvalidoException.class);
    }

    @Test
    @DisplayName("Deve rejeitar CNPJ com tamanho incompatível")
    void deveRejeitarTamanhoInvalido() {
        assertThatThrownBy(() -> new CNPJ("123"))
                .isInstanceOf(CnpjInvalidoException.class);
    }
}
