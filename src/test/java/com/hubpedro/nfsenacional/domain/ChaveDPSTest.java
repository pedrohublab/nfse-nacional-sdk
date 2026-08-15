package com.hubpedro.nfsenacional.domain;

import com.hubpedro.nfsenacional.domain.valueobject.ChaveDPS;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes do Value Object ChaveDPS")
public class ChaveDPSTest {

    @Test
    @DisplayName("Deve gerar ChaveDPS de 50 dígitos e id XML com prefixo DPS")
    void deveGerarChaveDpsCorretamente() {
        OffsetDateTime dhEmissao = OffsetDateTime.parse("2026-08-15T14:30:00-03:00");
        ChaveDPS chave = new ChaveDPS(
                "3550308", // São Paulo
                dhEmissao,
                "11222333000181",
                "1",
                12345L
        );

        assertThat(chave.valor()).hasSize(50);
        assertThat(chave.getIdXml()).isEqualTo("DPS" + chave.valor());
        assertThat(chave.getCodigoMunicipio()).isEqualTo("3550308");
    }

    @Test
    @DisplayName("Deve aceitar ChaveDPS a partir de string de 50 dígitos")
    void deveAceitarChaveDpsDeString() {
        String chaveStr = "35503082608112223330001810000000000000000000000001";
        ChaveDPS chave = ChaveDPS.of(chaveStr);
        assertThat(chave.valor()).isEqualTo(chaveStr);
        assertThat(chave.getIdXml()).isEqualTo("DPS" + chaveStr);
    }

    @Test
    @DisplayName("Deve rejeitar ChaveDPS com tamanho diferente de 50 dígitos")
    void deveRejeitarChaveComTamanhoInvalido() {
        assertThatThrownBy(() -> ChaveDPS.of("123"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
