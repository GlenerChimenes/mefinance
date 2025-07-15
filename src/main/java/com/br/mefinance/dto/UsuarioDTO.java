package com.br.mefinance.dto;

import com.br.mefinance.entities.Gasto;
import com.br.mefinance.entities.Usuario;

import java.util.ArrayList;
import java.util.List;

public class UsuarioDTO {

    private Long id;
    private String nome;
    private String email;
    private String senha;

    private List<Gasto> gastos = new ArrayList<>();

  //  Set<Role> roles = new HashSet<>();

    public UsuarioDTO() {
    }

    public UsuarioDTO(Long id, String nome, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public UsuarioDTO(Usuario entity) {
        this.id = entity.getId();
        this.nome = entity.getNome();
        this.email = entity.getEmail();
        this.senha = entity.getSenha();
       // this.gastos = entity.getGastos(); TODO Não preciso trazer os gastos com usuario. Busco no controle de gastos.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public List<Gasto> getGastos() {
        return gastos;
    }

    public void setGastos(List<Gasto> gastos) {
        this.gastos = gastos;
    }
}
