package com.interswitch.fraudtransactionapp.repository;

import com.interswitch.fraudtransactionapp.model.IpRisk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IpRiskRepository extends JpaRepository<IpRisk, String> {
}
