package com.mesh.testtask.domain.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FilterCriteria {
    private String key;
    private String operation;
    private Object value;
}
