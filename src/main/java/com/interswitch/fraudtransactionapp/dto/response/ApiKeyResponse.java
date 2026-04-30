package com.interswitch.fraudtransactionapp.dto.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiKeyResponse {

    private String prefix;
    private String ownerName;    // e.g., GTBank
    private boolean active;

}