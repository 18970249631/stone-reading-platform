package com.stonereading.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final Integer code = 400;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Integer code, String message) {
        super(message);
    }
}