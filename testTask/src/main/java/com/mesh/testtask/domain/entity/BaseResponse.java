package com.mesh.testtask.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class BaseResponse {

    public Boolean success;
    public String message;
    public Error error;

    public BaseResponse(String invalid_request, boolean b) {
        this.success = false;
        this.message = invalid_request;
    }

    public BaseResponse() {
        this.success = true;
    }
}
