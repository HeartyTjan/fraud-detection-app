package com.interswitch.fraudtransactionapp.util.mapper;

import com.interswitch.fraudtransactionapp.client.dto.ProviderBlacklistedIPDTO;
import com.interswitch.fraudtransactionapp.client.model.ProviderBlacklistedIP;

public final class ProviderBlacklistedIPMapper {

    public static ProviderBlacklistedIP toEntity(ProviderBlacklistedIPDTO dto) {
        ProviderBlacklistedIP entity = new ProviderBlacklistedIP();

        entity.setIp(dto.getIp());
        entity.setConfidenceLevel(dto.getConfidenceLevel());
        entity.setLastSeen(dto.getLastSeen());
        entity.setSessions(dto.getSessions());
        entity.setCountryCode(dto.getCountryCode());

        entity.setProtocols(String.join(",", dto.getProtocols()));

        return entity;
    }
}