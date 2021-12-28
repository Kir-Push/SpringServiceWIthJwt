package com.mesh.testtask.controller;

import com.mesh.testtask.domain.auth.TokenField;
import com.mesh.testtask.domain.entity.BaseResponse;
import com.mesh.testtask.domain.entity.User;
import com.mesh.testtask.domain.entity.UserDTO;
import com.mesh.testtask.domain.entity.UserRequest;
import com.mesh.testtask.domain.filter.UserFilter;
import com.mesh.testtask.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Value("${jwt.secret}")
    private String secret;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/rest/users/all", method = RequestMethod.POST)
    @ResponseBody
    public List<UserDTO> getUserList(@RequestBody(required = false) UserFilter userFilter) throws Exception {
        return userService.getUserList(userFilter);
    }


    @RequestMapping(value = "/rest/users/edit", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse editUser(@RequestBody UserRequest userRequest, HttpServletRequest request) throws Exception {
        String token = request.getHeader("token");
        try {
            DefaultClaims claims = (DefaultClaims) Jwts.parser().setSigningKey(secret).parse(token).getBody();
            String name = claims.get(TokenField.NAME.name(), String.class);
            User byName = userService.getByName(name);
            userRequest.setId(byName.getId());
            userRequest.setName(byName.getName());
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new AuthenticationServiceException("Token & request mismatch");
        }
        return userService.editUser(userRequest);
    }

    @RequestMapping(value = "/rest/users/delete", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse deleteUser(@RequestBody UserRequest userRequest) throws Exception {
        return userService.deleteUser(userRequest);
    }
}
