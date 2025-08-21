package com.br.mefinance.dto;

import java.math.BigDecimal;
import java.util.List;

public class ResumoGastosDTO {

    private List<GastoDTO> gastosDTO;
    private BigDecimal totalGastos;
    private BigDecimal sobraNoMes;

    public ResumoGastosDTO(List<GastoDTO> gastosDTO, BigDecimal totalGastos, BigDecimal sobraNoMes) {
        this.gastosDTO = gastosDTO;
        this.totalGastos = totalGastos;
        this.sobraNoMes = sobraNoMes;
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
}
