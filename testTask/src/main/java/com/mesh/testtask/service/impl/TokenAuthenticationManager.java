package com.mesh.testtask.service.impl;

import com.mesh.testtask.domain.auth.TokenAuth;
import com.mesh.testtask.domain.auth.TokenField;
import com.mesh.testtask.domain.entity.User;
import com.mesh.testtask.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenAuthenticationManager implements AuthenticationManager {

    private final UserService userService;

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    public TokenAuthenticationManager(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            Authentication result = authentication;
            if (authentication instanceof TokenAuth) {
                result = processAuthentication((TokenAuth) authentication);
            } else {
                authentication.setAuthenticated(false);
            }
            return result;
        } catch (Exception ex) {
            throw new AuthenticationServiceException(ex.getMessage());
        }
    }

    private TokenAuth processAuthentication(TokenAuth authentication) throws AuthenticationException {
        String token = authentication.getToken();
        DefaultClaims claims;
        try {
            claims = (DefaultClaims) Jwts.parser().setSigningKey(secret).parse(token).getBody();
        } catch (Exception ex) {
            throw new AuthenticationServiceException("Token corrupted");
        }

        Long expiredTime = claims.get(TokenField.EXPIRATION_DATE.name(), Long.class);
        if (expiredTime == null) {
            throw new AuthenticationServiceException("Invalid token");
        }

        Date expiredDate = new Date(expiredTime);
        if (expiredDate.after(new Date())) {
            return buildFullTokenAuthentication(authentication, claims);
        } else {
            throw new AuthenticationServiceException("Token expired date error");
        }
    }

    private TokenAuth buildFullTokenAuthentication(TokenAuth authentication, DefaultClaims claims) {
        User user = userService.getByName(claims.get(TokenField.NAME.name(), String.class));
        if (user != null) {
            return new TokenAuth(authentication.getToken(), true, user);
        } else {
            throw new AuthenticationServiceException("User not exist");
        }
    }
}
