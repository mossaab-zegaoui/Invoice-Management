package com.example.securebusiness.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
