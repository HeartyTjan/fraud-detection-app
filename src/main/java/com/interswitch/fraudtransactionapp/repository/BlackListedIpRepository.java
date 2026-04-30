package com.interswitch.fraudtransactionapp.repository;

import com.interswitch.fraudtransactionapp.model.BlacklistedIp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackListedIpRepository extends JpaRepository<BlacklistedIp,String> {
    boolean existsByIp(String ip);

}
