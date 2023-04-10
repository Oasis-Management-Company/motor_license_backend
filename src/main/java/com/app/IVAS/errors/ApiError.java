package com.app.IVAS.errors;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@Data
public class ApiError {

    private int statusCode;
    private HttpStatus status;
    private String message;
    private List<String> errors;
    private Object data;
    private boolean success;

    public ApiError(int statusCode, HttpStatus status, String message, boolean success, List<String> errors, Object data) {
        super();
        this.statusCode = statusCode;
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.data = data;
        this.success = success;
    }

    public ApiError(int statusCode, HttpStatus status, String message, boolean success, String error, Object data) {
        super();
        this.statusCode = statusCode;
        this.status = status;
        this.message = message;
        errors = Collections.singletonList(error);
        this.data = data;
        this.success = success;
    }
}
