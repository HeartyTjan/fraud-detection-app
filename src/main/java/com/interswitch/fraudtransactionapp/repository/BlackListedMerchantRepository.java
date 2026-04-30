package com.interswitch.fraudtransactionapp.repository;

import com.interswitch.fraudtransactionapp.model.BlacklistedMerchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackListedMerchantRepository extends JpaRepository<BlacklistedMerchant, String> {
    boolean existsByMerchantId(String merchantId);
}
