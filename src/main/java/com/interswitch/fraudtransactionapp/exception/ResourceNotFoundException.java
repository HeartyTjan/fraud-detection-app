package com.interswitch.fraudtransactionapp.exception;

public class ResourceNotFoundException extends AbstractException {

    public ResourceNotFoundException(String code,String message) {
        super(code, message);
    }
    public ResourceNotFoundException(String message) {
        super("4000", message);
    }
}
