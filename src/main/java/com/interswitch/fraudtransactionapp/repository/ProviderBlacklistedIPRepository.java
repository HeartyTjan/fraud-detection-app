package com.interswitch.fraudtransactionapp.repository;

import com.interswitch.fraudtransactionapp.client.model.ProviderBlacklistedIP;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderBlacklistedIPRepository extends JpaRepository<ProviderBlacklistedIP, String> {
}
