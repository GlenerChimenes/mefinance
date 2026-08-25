package com.br.mefinance.dto;

import java.math.BigDecimal;
import java.util.List;

public record FaturaAnaliseDTO(
        String nomeArquivo,
        BigDecimal totalGeral,
        int quantidadeLancamentos,
        String termoPesquisa,
        BigDecimal totalPesquisa,
        List<FaturaTitularResumoDTO> titulares,
        List<FaturaMesResumoDTO> meses,
        List<FaturaLancamentoDTO> lancamentos) {
}
