package com.br.mefinance.dto;

import java.math.BigDecimal;

public record FaturaCategoriaResumoDTO(
        String categoria,
        BigDecimal total,
        long quantidade) {
}
