package com.br.mefinance.projections;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface GastoProjection {
    Long getId();
    String getDescricao();
    BigDecimal getValor();
    Integer getPeriodo();
    LocalDate getDataVencimento();
    String getSituacao();
}
