package com.br.mefinance.projections;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface GastoProjection {
    Long getId();
    String getDescricao();
    BigDecimal getValor();
    Integer getPeriodo();
    LocalDateTime getDataVencimento();
}
