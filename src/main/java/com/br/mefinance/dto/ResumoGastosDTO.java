package com.br.mefinance.dto;

import java.math.BigDecimal;
import java.util.List;

public class ResumoGastosDTO {

    private List<GastoDTO> gastosDTO;
    private BigDecimal totalGastos;
    private BigDecimal sobraNoMes;
    private BigDecimal rendaMensal;

    public ResumoGastosDTO(List<GastoDTO> gastosDTO, BigDecimal totalGastos, BigDecimal sobraNoMes, BigDecimal rendaMensal) {
        this.gastosDTO = gastosDTO;
        this.totalGastos = totalGastos;
        this.sobraNoMes = sobraNoMes;
        this.rendaMensal = rendaMensal;
    }

    public List<GastoDTO> getGastosDTO() {
        return gastosDTO;
    }

    public BigDecimal getTotalGastos() {
        return totalGastos;
    }

    public BigDecimal getSobraNoMes() {
        return sobraNoMes;
    }

    public BigDecimal getRendaMensal() {
        return rendaMensal;
    }
}
