package com.interswitch.fraudtransactionapp.exception;

public class ResourceExistsException extends RuntimeException {
    public ResourceExistsException(String message) {
        super(message);
    }
}
