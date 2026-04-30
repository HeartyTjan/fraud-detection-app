package com.interswitch.fraudtransactionapp.service.impl;

import com.interswitch.fraudtransactionapp.exception.ResourceExistsException;
import com.interswitch.fraudtransactionapp.exception.ResourceNotFoundException;
import com.interswitch.fraudtransactionapp.model.ApiKey;
import com.interswitch.fraudtransactionapp.repository.ApiKeyRepository;
import com.interswitch.fraudtransactionapp.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;


@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl implements ApiKeyService {
    private final Logger log = LoggerFactory.getLogger(ApiKeyServiceImpl.class);

    private final Cache<String, ApiKey> validatedKeyCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();

    private final ApiKeyRepository apiKeyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String createApiKey(String ownerName) {

       boolean isExisted =  apiKeyRepository.existsApiKeyByOwnerName(ownerName);
       log.info("Existed ApiKey {} ", isExisted);
       if (isExisted) throw new ResourceExistsException("User with name " + ownerName + " already exists");

        String prefix = "sk_live_";
        String rawKey = prefix + UUID.randomUUID();

        String hashedKey = passwordEncoder.encode(rawKey);

        ApiKey apiKeyEntity = new ApiKey();
        apiKeyEntity.setKey(hashedKey);
        apiKeyEntity.setPrefix(prefix);
        apiKeyEntity.setOwnerName(ownerName);
        apiKeyEntity.setCreatedAt(LocalDateTime.now());
        apiKeyEntity.setUpdatedAt(LocalDateTime.now());
        apiKeyEntity.setActive(true);
        apiKeyEntity.setKeyHash(sha256(rawKey));
        apiKeyRepository.save(apiKeyEntity);

        return rawKey;
    }


    @Override
    public void revokeApiKey(Long id) {
        ApiKey key = apiKeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("API Key not found"));
        key.setActive(false);
        apiKeyRepository.save(key);
    }


//    @Override
//    public ApiKey validateApiKey(String rawKey) {
//        String hash = sha256(rawKey);
//        return apiKeyRepository.findByKeyHashAndActiveTrue(hash)
//                .filter(k -> passwordEncoder.matches(rawKey, k.getKey()))
//                .orElseThrow(() -> new ResourceNotFoundException("Invalid API key"));
//    }
@Override
public ApiKey validateApiKey(String rawKey) {
    String hash = sha256(rawKey);

    ApiKey cached = validatedKeyCache.getIfPresent(hash);
    if (cached != null && cached.isActive()) {
        return cached;
    }

    ApiKey key = apiKeyRepository.findByKeyHashAndActiveTrue(hash)
            .filter(k -> passwordEncoder.matches(rawKey, k.getKey()))
            .orElseThrow(() -> new ResourceNotFoundException("Invalid API key"));

    validatedKeyCache.put(hash, key);
    return key;
}

//    @Override
//    public ApiKey validateApiKey(String rawKey) {
//        long start = System.currentTimeMillis();
//
//        String hash = sha256(rawKey);
//        log.info("SHA-256 took: {}ms", System.currentTimeMillis() - start);
//
//        long dbStart = System.currentTimeMillis();
//        Optional<ApiKey> keyOpt = apiKeyRepository.findByKeyHashAndActiveTrue(hash);
//        log.info("DB lookup took: {}ms", System.currentTimeMillis() - dbStart);
//
//        long bcryptStart = System.currentTimeMillis();
//        ApiKey result = keyOpt
//                .filter(k -> passwordEncoder.matches(rawKey, k.getKey()))
//                .orElseThrow(() -> new ResourceNotFoundException("Invalid API key"));
//        log.info("BCrypt verify took: {}ms", System.currentTimeMillis() - bcryptStart);
//
//        log.info("Total validateApiKey: {}ms", System.currentTimeMillis() - start);
//        return result;
//    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}