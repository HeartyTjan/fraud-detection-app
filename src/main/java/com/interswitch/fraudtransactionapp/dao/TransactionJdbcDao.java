package com.interswitch.fraudtransactionapp.dao;

import com.interswitch.fraudtransactionapp.model.Transactions;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionJdbcDao {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_SQL = """
              INSERT INTO transactions (
                  card_no, amount, merchant_id, ip_address, risk_score, decision,
                  transaction_time, type, device_fingerprint, user_agent, session_id,
                  latitude, longitude, country_code, currency, card_bin,
                  merchant_category, channel_type, triggered_rules, created_at
              ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
              """;

    private static final RowMapper<Transactions> ROW_MAPPER = (rs, rowNum) -> {
        Transactions tx = new Transactions();
        tx.setId(rs.getLong("id"));
        tx.setCardNo(rs.getString("card_no"));
        tx.setAmount(rs.getBigDecimal("amount"));
        tx.setMerchantId(rs.getString("merchant_id"));
        tx.setIpAddress(rs.getString("ip_address"));
        tx.setRiskScore(rs.getInt("risk_score"));
        tx.setDecision(rs.getString("decision"));
        Timestamp txTime = rs.getTimestamp("transaction_time");
        tx.setTransactionTime(txTime != null ? txTime.toInstant() : null);
        tx.setType(rs.getString("type"));
        tx.setDeviceFingerprint(rs.getString("device_fingerprint"));
        tx.setUserAgent(rs.getString("user_agent"));
        tx.setSessionId(rs.getString("session_id"));
        tx.setLatitude(rs.getObject("latitude", Double.class));
        tx.setLongitude(rs.getObject("longitude", Double.class));
        tx.setCountryCode(rs.getString("country_code"));
        tx.setCurrency(rs.getString("currency"));
        tx.setCardBin(rs.getString("card_bin"));
        tx.setMerchantCategory(rs.getString("merchant_category"));
        tx.setChannelType(rs.getString("channel_type"));
        tx.setTriggeredRules(rs.getString("triggered_rules"));
        return tx;
    };

    public void save(Transactions tx) {
        jdbcTemplate.update(INSERT_SQL,
                tx.getCardNo(),
                tx.getAmount(),
                tx.getMerchantId(),
                tx.getIpAddress(),
                tx.getRiskScore(),
                tx.getDecision(),
                tx.getTransactionTime() != null ? Timestamp.from(tx.getTransactionTime()) : null,
                tx.getType(),
                tx.getDeviceFingerprint(),
                tx.getUserAgent(),
                tx.getSessionId(),
                tx.getLatitude(),
                tx.getLongitude(),
                tx.getCountryCode(),
                tx.getCurrency(),
                tx.getCardBin(),
                tx.getMerchantCategory(),
                tx.getChannelType(),
                tx.getTriggeredRules(),
                Timestamp.from(java.time.Instant.now())
        );
    }

    public List<Transactions> findByDecision(String decision) {
        String sql = "SELECT * FROM transactions WHERE decision = ? ORDER BY transaction_time DESC";
        return jdbcTemplate.query(sql, ROW_MAPPER, decision);
    }

    public List<Transactions> findBlockedTransactions() {
        return findByDecision("BLOCK");
    }

    public List<Transactions> findReviewTransactions() {
        return findByDecision("REVIEW");
    }

    public List<Transactions> findFlaggedTransactions() {
        String sql = "SELECT * FROM transactions WHERE decision IN ('BLOCK', 'REVIEW') ORDER BY transaction_time DESC";
        return jdbcTemplate.query(sql, ROW_MAPPER);
    }

    public List<Transactions> findFlaggedByIp(String ipAddress) {
        String sql = "SELECT * FROM transactions WHERE ip_address = ? AND decision IN ('BLOCK', 'REVIEW') ORDER BY transaction_time DESC";
        return jdbcTemplate.query(sql, ROW_MAPPER, ipAddress);
    }

    public List<Transactions> findFlaggedByCard(String cardNo) {
        String sql = "SELECT * FROM transactions WHERE card_no = ? AND decision IN ('BLOCK', 'REVIEW') ORDER BY transaction_time DESC";
        return jdbcTemplate.query(sql, ROW_MAPPER, cardNo);
    }

    public List<Transactions> findFlaggedByMerchant(String merchantId) {
        String sql = "SELECT * FROM transactions WHERE merchant_id = ? AND decision IN ('BLOCK', 'REVIEW') ORDER BY transaction_time DESC";
        return jdbcTemplate.query(sql, ROW_MAPPER, merchantId);
    }

    public List<Transactions> findFlaggedTransactions(int page, int size) {
        String sql = """
          SELECT * FROM transactions
          WHERE decision IN ('BLOCK', 'REVIEW')
          ORDER BY transaction_time DESC
          OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
          """;
        return jdbcTemplate.query(sql, ROW_MAPPER, page * size, size);
    }
}