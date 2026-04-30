package com.interswitch.fraudtransactionapp.service;

import com.interswitch.fraudtransactionapp.model.ApiKey;

public interface ApiKeyService {
    String createApiKey(String ownerName);

    void revokeApiKey(Long id);

    ApiKey validateApiKey(String rawKey);
}
