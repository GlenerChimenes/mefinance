package com.br.mefinance.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("serial")
@Entity
@Table(name = "tb_role")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String authority;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private Set<User> usuarios = new HashSet<>();

    public Role() {
    }

    public Role(Long id, String authority) {
        super();
        this.id = id;
        this.authority = authority;
    }

    public Set<User> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Set<User> usuarios) {
        this.usuarios = usuarios;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(authority);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Role other = (Role) obj;
        return Objects.equals(authority, other.authority);
    }
}
