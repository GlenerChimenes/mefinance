package com.br.mefinance.projections;

public interface UserDetailsProjection {

    Long getUserId();
    String getUsername();
    String getPassword();
    Long getRoleId();
    String getAuthority();
}
