package com.interswitch.fraudtransactionapp.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class FraudJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public int checkCardVelocity(String cardNo, Instant since) {
      Integer count = jdbcTemplate.queryForObject(
                "SELECT check_card_velocity(?, ?)",
                Integer.class,
                cardNo,
                Timestamp.from(since)
        );

        return count != null ? count : 0;
    }

    public String checkAbnormalAmount(String cardNo, BigDecimal amount) {
        String sql = "SELECT check_abnormal_amount(?, ?)";
        return jdbcTemplate.queryForObject(sql, String.class, cardNo, amount);
    }
}
