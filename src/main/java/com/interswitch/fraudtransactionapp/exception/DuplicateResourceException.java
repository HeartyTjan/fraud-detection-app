package com.interswitch.fraudtransactionapp.exception;

public class DuplicateResourceException extends AbstractException {
    public DuplicateResourceException(String message) {
        super("409",message);
    }
}
