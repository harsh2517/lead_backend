package com.accountooze.response;

import java.util.Date;
import java.util.List;

import com.accountooze.exception.LowerCaseClassNameResolver;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

import lombok.Data;

@Data
public class ApiResponse {

    private List<Object> dataArray;
    private Object data;
    private String message;
    private boolean success;
    private long timestamp;

    private ApiResponse() {
        timestamp = new Date().getTime();
        success = true;
    }

    public ApiResponse(Object object) {
        this();
        this.data = object;
    }

    public ApiResponse(String message, boolean success) {
        this();
        this.message = message;
        this.success = success;
    }
}
