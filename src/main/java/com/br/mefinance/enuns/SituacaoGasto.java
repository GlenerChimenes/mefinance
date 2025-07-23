package com.br.mefinance.enuns;

public enum SituacaoGasto {

    PENDENTE("Pedente"),
    PAGO("Pago"),
    ATRASADO("Atrasado"),
    CANCELADO("Cancelado");

    private final String descricao;

    SituacaoGasto(String descricao){
        this.descricao = descricao;
    }

}
