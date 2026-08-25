package com.br.mefinance.services;

import com.br.mefinance.dto.FaturaAnaliseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;

class FaturaAnaliseServiceTests {

    private final FaturaAnaliseService service = new FaturaAnaliseService();

    @Test
    void deveAgruparLancamentosPorMesECategoriaEPesquisarDescricao() {
        String csv = """
                data;descricao;valor
                05/07/2025;POSTO IPIRANGA;150,00
                20/07/2025;POSTO SHELL;200,50
                03/08/2025;IFOOD RESTAURANTE;45,90
                """;
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "fatura.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        FaturaAnaliseDTO analise = service.analisar(arquivo, YearMonth.of(2025, 8), "posto");

        assertThat(analise.quantidadeLancamentos()).isEqualTo(3);
        assertThat(analise.totalGeral()).isEqualByComparingTo("396.40");
        assertThat(analise.totalPesquisa()).isEqualByComparingTo("350.50");
        assertThat(analise.meses()).hasSize(2);
        assertThat(analise.meses().getFirst().categorias().getFirst().categoria()).isEqualTo("Combustível");
        assertThat(analise.meses().getFirst().categorias().getFirst().total()).isEqualByComparingTo("350.50");
    }

    @Test
    void deveLerDatasSicoobIgnorarParcelaEConsiderarEstorno() {
        String texto = """
                10 MAI Valori *DB COMERCIO
                03/03 Goiania R$ 336,32
                04 JUL AGUA VIDA PURIFICADO
                01/02 GOIANIA R$ 724,50
                01 JUN SHOPEE *SHPSTECNOLOG SAO PAULO -R$ 101,88
                02 JUL AMAZONMKTPLC*RCAHOM
                E SAO PAULO R$ 81,17
                31 JUL POSTO CACIQUE LTDA GOIANAPOLIS R$ 162,28
                TOTAL R$ 1.202,39
                """;
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "fatura.txt", "text/plain", texto.getBytes(StandardCharsets.UTF_8));

        FaturaAnaliseDTO analise = service.analisar(arquivo, YearMonth.of(2026, 8), "posto");

        assertThat(analise.quantidadeLancamentos()).isEqualTo(5);
        assertThat(analise.totalGeral()).isEqualByComparingTo("1202.39");
        assertThat(analise.totalPesquisa()).isEqualByComparingTo("162.28");
        assertThat(analise.meses()).extracting("periodo")
                .containsExactly("2026-05", "2026-06", "2026-07");
        assertThat(analise.lancamentos().getFirst().descricao()).contains("Valori *DB COMERCIO");
    }
}
