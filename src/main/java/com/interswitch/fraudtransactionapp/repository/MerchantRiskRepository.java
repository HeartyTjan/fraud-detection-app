package com.interswitch.fraudtransactionapp.repository;

import com.interswitch.fraudtransactionapp.model.MerchantRisk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRiskRepository extends JpaRepository<MerchantRisk, String> {
}
