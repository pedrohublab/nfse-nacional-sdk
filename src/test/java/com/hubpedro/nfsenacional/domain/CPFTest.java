package com.hubpedro.nfsenacional.domain;

import com.hubpedro.nfsenacional.domain.exception.CpfInvalidoException;
import com.hubpedro.nfsenacional.domain.valueobject.CPF;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes do Value Object CPF")
public class CPFTest {

    @Test
    @DisplayName("Deve validar e formatar CPF válido")
    void deveValidarCpfValido() {
        CPF cpf = new CPF("123.456.789-09");
        assertThat(cpf.getNumero()).isEqualTo("12345678909");
        assertThat(cpf.getFormatado()).isEqualTo("123.456.789-09");
    }

    @Test
    @DisplayName("Deve rejeitar CPF com dígitos inválidos")
    void deveRejeitarCpfInvalido() {
        assertThatThrownBy(() -> new CPF("12345678999"))
                .isInstanceOf(CpfInvalidoException.class);
    }

    @Test
    @DisplayName("Deve rejeitar CPF repetido")
    void deveRejeitarCpfRepetido() {
        assertThatThrownBy(() -> new CPF("111.111.111-11"))
                .isInstanceOf(CpfInvalidoException.class);
    }
}
