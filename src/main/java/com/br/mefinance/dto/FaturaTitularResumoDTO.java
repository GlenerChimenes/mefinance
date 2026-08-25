package com.br.mefinance.dto;

import java.math.BigDecimal;
import java.util.List;

public record FaturaTitularResumoDTO(
        String titular,
        BigDecimal total,
        int quantidade,
        List<FaturaCategoriaResumoDTO> categorias) {
}
