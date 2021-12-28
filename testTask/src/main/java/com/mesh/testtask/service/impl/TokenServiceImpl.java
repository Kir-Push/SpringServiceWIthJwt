package com.mesh.testtask.service.impl;

import com.mesh.testtask.domain.auth.TokenField;
import com.mesh.testtask.domain.entity.User;
import com.mesh.testtask.service.TokenService;
import com.mesh.testtask.service.UserService;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenServiceImpl implements TokenService {

    private final UserService userService;

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    public TokenServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String getToken(String username, String password) throws Exception {
        if (username == null || password == null) {
            return null;
        }
        User user = userService.getByName(username);
        if (user == null) {
            throw new AuthenticationServiceException("User not exist");
        }
        Map<String, Object> tokenData = new HashMap<>();

        if (password.equals(user.getPassword())) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, 1);

            tokenData.put(TokenField.ID.name(), user.getId().toString());
            tokenData.put(TokenField.NAME.name(), user.getName());
            tokenData.put(TokenField.CREATE_DATE.name(), new Date().getTime());
            tokenData.put(TokenField.EXPIRATION_DATE.name(), calendar.getTime());

            JwtBuilder jwtBuilder = Jwts.builder();
            jwtBuilder.setExpiration(calendar.getTime());
            jwtBuilder.setClaims(tokenData);

            return jwtBuilder.signWith(SignatureAlgorithm.HS512, secret).compact();
        } else {
            throw new Exception("Authentication error");
        }
    }
}
