package com.interswitch.fraudtransactionapp.repository;


import com.interswitch.fraudtransactionapp.model.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    boolean existsByOwnerName(String ownerName);

    boolean existsApiKeyByOwnerName(String ownerName);
    Optional<ApiKey> findByKeyHashAndActiveTrue(String keyHash);
}