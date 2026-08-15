package com.hubpedro.nfsenacional.domain.exception;

import java.util.Collections;
import java.util.List;

/**
 * Exceção semântica lançada quando a validação do XML contra o Schema XSD falha localmente.
 */
public class XmlValidationException extends NFSeException {

    private final List<String> erros;

    public XmlValidationException(String message) {
        this(message, Collections.singletonList(message));
    }

    public XmlValidationException(String message, List<String> erros) {
        super(message);
        this.erros = erros != null ? List.copyOf(erros) : Collections.emptyList();
    }

    public List<String> getErros() {
        return erros;
    }
}
