package com.mesh.testtask.controller;

import com.mesh.testtask.domain.auth.AuthRequest;
import com.mesh.testtask.domain.auth.TokenAuth;
import com.mesh.testtask.domain.entity.BaseResponse;
import com.mesh.testtask.domain.entity.User;
import com.mesh.testtask.domain.entity.UserRequest;
import com.mesh.testtask.service.TokenService;
import com.mesh.testtask.service.UserService;
import com.mesh.testtask.service.impl.TokenAuthenticationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
public class LoginController {

    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    private final UserService userService;
    private final TokenService tokenService;
    private final TokenAuthenticationManager authenticationManager;

    public LoginController(UserService userService, TokenService tokenService, TokenAuthenticationManager authenticationManager) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authenticationRequest) throws Exception {

        String username = authenticationRequest.getUsername();
        String password = authenticationRequest.getPassword();

        final User userDetails = userService.getByName(username);
        if (userDetails == null) {
            return new ResponseEntity<>(UNAUTHORIZED);
        }

        final String token = tokenService.getToken(username, password);
        authenticate(token);

        return ResponseEntity.ok(token);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public BaseResponse createUser(@RequestBody UserRequest userRequest) throws Exception {
        return userService.createUser(userRequest);
    }

    private void authenticate(String token) throws Exception {
        try {
            TokenAuth tokenAuth = new TokenAuth(token,false, null);
            authenticationManager.authenticate(tokenAuth);
        } catch (AuthenticationServiceException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
