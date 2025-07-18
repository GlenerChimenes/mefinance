package com.br.mefinance.dto;

import com.br.mefinance.entities.Gasto;
import com.br.mefinance.entities.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {

    private Long id;
    private String nome;
    private String email;

    private List<Gasto> gastos = new ArrayList<>();

    List<String> roles = new ArrayList<>();

    public UserDTO() {
    }

    public UserDTO(Long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public UserDTO(User entity) {
        this.id = entity.getId();
        this.nome = entity.getNome();
        this.email = entity.getEmail();
        for (GrantedAuthority role : entity.getRoles())
            roles.add(role.getAuthority());
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

    public List<Gasto> getGastos() {
        return gastos;
    }

    public void setGastos(List<Gasto> gastos) {
        this.gastos = gastos;
    }

    public List<String> getRoles() {
        return roles;
    }

}
