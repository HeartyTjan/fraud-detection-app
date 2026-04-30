package com.interswitch.fraudtransactionapp.dto.response;


import lombok.Setter;

@Setter
public class ApiKeyCreateResponse extends ApiKeyResponse {
    private String rawKey;
}