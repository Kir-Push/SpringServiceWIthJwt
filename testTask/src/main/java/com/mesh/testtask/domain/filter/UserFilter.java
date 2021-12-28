package com.mesh.testtask.domain.filter;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserFilter {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private BigDecimal amount;
    private Short age;
    private Integer page;
    private Integer pageSize;
}
