package com.interswitch.fraudtransactionapp.exception;

import lombok.Getter;

@Getter
public class AbstractException extends RuntimeException {
    String code;

    public AbstractException(String code, String message) {
        super(message);
        this.code = code;
    }
}
