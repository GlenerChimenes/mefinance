package com.br.mefinance.dto;

import com.br.mefinance.entities.Gasto;
import com.br.mefinance.enuns.SituacaoGasto;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GastoDTO {

    private Long          id;

    @NotBlank(message = "Campo descricao requerido")
    private String descricao;

    @Positive(message = "Valor deve ser positivo")
    private BigDecimal valor;

    @NotNull(message = "Campo periodo requerido")
    private Integer periodo;

    @NotNull(message = "Data de vencimento é obrigatoria")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataVencimento;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataPagamento;

    @Enumerated(EnumType.STRING)
    private SituacaoGasto situacao;

    private Long idCategoria;

    private String nomeCategoria;

//    @NotNull(message = "User é obrigatória")
//    private User user;

    @NotNull(message = "Usuário é obrigatório")
    private Long idUser;

    public GastoDTO() {
    }

    public GastoDTO(Long id, String descricao, BigDecimal valor, Integer periodo, LocalDate dataVencimento) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.periodo = periodo;
        this.dataVencimento = dataVencimento;
//        this.user = user;
    }

    public GastoDTO(Gasto entity) {
        this.id = entity.getId();
        this.descricao = entity.getDescricao();
        this.valor = entity.getValor();
        this.periodo = entity.getPeriodo();
        this.dataVencimento = entity.getDataVencimento();
        this.dataPagamento = entity.getDataPagamento();
        this.situacao = entity.getSituacao();

        if (entity.getUser() != null) {
            this.idUser = entity.getUser().getId();
        }

        if (entity.getCategoria() != null) {
            this.idCategoria = entity.getCategoria().getId();
            this.nomeCategoria = entity.getCategoria().getNome();
        }
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

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDateTime dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public SituacaoGasto getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoGasto situacao) {
        this.situacao = situacao;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public void setNomeCategoria(String nomeCategoria) {
        this.nomeCategoria = nomeCategoria;
    }
}
