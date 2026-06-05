package com.br.mefinance.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_user_access_log")
public class UserAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataAcesso;

    private String email;

    private String nome;

    private String ip;

    private String userAgent;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public UserAccessLog() {
    }

    public UserAccessLog(User user, String ip, String userAgent) {
        this.user = user;
        this.email = user.getEmail();
        this.nome = user.getNome();
        this.ip = ip;
        this.userAgent = userAgent;
        this.dataAcesso = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDataAcesso() {
        return dataAcesso;
    }

    public void setDataAcesso(LocalDateTime dataAcesso) {
        this.dataAcesso = dataAcesso;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public String getIp() {
        return ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public User getUser() {
        return user;
    }
}