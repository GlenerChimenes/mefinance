package com.br.mefinance.dto;

import java.math.BigDecimal;
import java.util.List;

public record FaturaMesResumoDTO(
        String periodo,
        BigDecimal total,
        List<FaturaCategoriaResumoDTO> categorias) {
}
