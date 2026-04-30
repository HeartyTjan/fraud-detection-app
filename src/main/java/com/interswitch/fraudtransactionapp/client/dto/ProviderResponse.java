package com.interswitch.fraudtransactionapp.client.dto;

import com.interswitch.fraudtransactionapp.client.model.Meta;
import lombok.Getter;

import java.util.List;

@Getter
public class ProviderResponse {
    private Meta meta;
    private List<ProviderBlacklistedIPDTO> data;
    private Object error;

}
