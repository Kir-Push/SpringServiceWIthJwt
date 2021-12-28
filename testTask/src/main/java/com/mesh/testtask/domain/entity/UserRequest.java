package com.mesh.testtask.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class UserRequest {
    private Long id;
    private Short age;
    private String name;
    private String password;
    private String email;
    private List<String> phone;
    private BigDecimal amount;
}
