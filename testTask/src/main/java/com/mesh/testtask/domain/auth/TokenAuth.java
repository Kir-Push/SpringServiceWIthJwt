package com.mesh.testtask.domain.auth;

import com.mesh.testtask.domain.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@AllArgsConstructor
public class TokenAuth implements Authentication {

    private String token;
    private boolean isAuthenticated;
    private User principal;


    public TokenAuth() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public String getName() {
        return principal != null ? principal.getName() : null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
        isAuthenticated = authenticated;
    }

    public String getToken() {
        return token;
    }
}
