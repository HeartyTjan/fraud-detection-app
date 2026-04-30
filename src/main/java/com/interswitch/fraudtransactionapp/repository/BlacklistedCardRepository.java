package com.interswitch.fraudtransactionapp.repository;

import com.interswitch.fraudtransactionapp.model.BlacklistedCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlacklistedCardRepository extends JpaRepository<BlacklistedCard, String> {
    boolean existsByCardNo(String cardNo);

    Optional<BlacklistedCard> findByCardNo(String cardNo);
}
