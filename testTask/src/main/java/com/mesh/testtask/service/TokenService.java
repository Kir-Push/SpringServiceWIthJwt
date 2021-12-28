package com.mesh.testtask.service;

public interface TokenService {

    String getToken(String username, String password) throws Exception;
}
