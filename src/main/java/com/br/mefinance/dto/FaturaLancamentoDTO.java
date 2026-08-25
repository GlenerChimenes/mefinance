package com.br.mefinance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FaturaLancamentoDTO(
        LocalDate data,
        String descricao,
        String categoria,
        BigDecimal valor) {
}
