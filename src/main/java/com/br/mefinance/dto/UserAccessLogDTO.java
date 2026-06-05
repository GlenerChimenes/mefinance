package com.br.mefinance.dto;

import com.br.mefinance.entities.UserAccessLog;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class UserAccessLogDTO {

    private Long id;
    private String nome;
    private String email;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataAcesso;

    private String ip;
    private String userAgent;

    public UserAccessLogDTO(UserAccessLog entity) {
        this.id = entity.getId();
        this.nome = entity.getNome();
        this.email = entity.getEmail();
        this.dataAcesso = entity.getDataAcesso();
        this.ip = entity.getIp();
        this.userAgent = entity.getUserAgent();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getDataAcesso() {
        return dataAcesso;
    }

    public String getIp() {
        return ip;
    }

    public String getUserAgent() {
        return userAgent;
    }
}
