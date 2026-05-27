package com.br.mefinance.config.customgrant;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomUserAuthorities {

    private Long userId;
	private String username;
	private Collection<? extends GrantedAuthority> authorities;

	public CustomUserAuthorities(Long userId, String username, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
		this.authorities = authorities;
	}

    public Long getUserId() {
        return userId;
    }

	public String getUsername() {
		return username;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
}
