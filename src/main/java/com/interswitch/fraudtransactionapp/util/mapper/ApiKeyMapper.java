package com.interswitch.fraudtransactionapp.util.mapper;

import com.interswitch.fraudtransactionapp.dto.response.ApiKeyCreateResponse;
import com.interswitch.fraudtransactionapp.dto.response.ApiKeyResponse;
import com.interswitch.fraudtransactionapp.model.ApiKey;
import org.springframework.stereotype.Component;

@Component
public final class ApiKeyMapper {

    public static ApiKeyResponse toResponse(ApiKey apiKey) {
        ApiKeyResponse dto = new ApiKeyResponse();
        dto.setPrefix(apiKey.getPrefix());
        dto.setOwnerName(apiKey.getOwnerName());
        dto.setActive(apiKey.isActive());
        return dto;
    }

    public ApiKeyCreateResponse toCreateResponse(ApiKey apiKey, String rawKey) {
        ApiKeyCreateResponse dto = new ApiKeyCreateResponse();
        dto.setPrefix(apiKey.getPrefix());
        dto.setOwnerName(apiKey.getOwnerName());
        dto.setActive(apiKey.isActive());
        dto.setRawKey(rawKey);
        return dto;
    }
}