package com.br.mefinance.dto;

import com.br.mefinance.entities.Gasto;
import com.br.mefinance.entities.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GastoDTO {

    private Long          id;

    @NotBlank(message = "Campo descricao requerido")
    private String descricao;

    @Positive(message = "Valor deve ser positivo")
    private BigDecimal valor;

    @NotBlank(message = "Campo periodo requerido")
    private Integer periodo;

    @NotNull(message = "Data de vencimento é obrigatoria")
    private LocalDateTime dataVencimento;

    @NotNull(message = "User é obrigatória")
    private User usuario;

    public GastoDTO() {
    }

    public GastoDTO(Long id, String descricao, BigDecimal valor, Integer periodo, LocalDateTime dataVencimento, User user) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.periodo = periodo;
        this.dataVencimento = dataVencimento;
        this.usuario = user;
    }

    public GastoDTO(Gasto entity) {
        this.id = entity.getId();
        this.descricao = entity.getDescricao();
        this.valor = entity.getValor();
        this.periodo = entity.getPeriodo();
        this.dataVencimento = entity.getDataVencimento();
        this.usuario = entity.getUser();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public LocalDateTime getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDateTime dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User user) {
        this.usuario = user;
    }
}
