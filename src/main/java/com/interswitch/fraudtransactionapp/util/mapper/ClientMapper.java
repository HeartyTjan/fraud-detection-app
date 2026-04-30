package com.interswitch.fraudtransactionapp.util.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interswitch.fraudtransactionapp.client.dto.ProviderResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public final class ClientMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ProviderResponse mapProviderResponse(String jsonResponse) throws IOException {
        return MAPPER.readValue(jsonResponse, ProviderResponse.class);

    }
}