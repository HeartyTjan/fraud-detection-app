package com.interswitch.fraudtransactionapp.dao;

import com.interswitch.fraudtransactionapp.fraudEngine.model.VelocityResult;
import com.interswitch.fraudtransactionapp.model.LastTransactionInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

@Slf4j
@Repository
public class FraudDao {

    private final SimpleJdbcCall checkCardVelocityProc, checkAbnormalAmountProc, checkIpVelocityProc, checkAllVelocityProc;
    private final JdbcTemplate jdbcTemplate;

    public FraudDao(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
        this.checkCardVelocityProc = new SimpleJdbcCall(jdbcTemplate).withProcedureName("check_card_velocity");
        this.checkAbnormalAmountProc = new SimpleJdbcCall(jdbcTemplate).withProcedureName("check_abnormal_amount_proc");
        this.checkIpVelocityProc = new SimpleJdbcCall(jdbcTemplate).withProcedureName("check_ip_velocity");
        this.checkAllVelocityProc = new SimpleJdbcCall(jdbcTemplate).withProcedureName("check_all_velocity");
    }

    public VelocityResult checkAllVelocity(String cardNo, String ipAddress, Instant now) {
        long start = System.currentTimeMillis();
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_card_no", cardNo)
                .addValue("p_ip_address", ipAddress)
                .addValue("p_now", Timestamp.from(now));

        Map<String, Object> out = checkAllVelocityProc.execute(in);

        VelocityResult result = new VelocityResult();
        result.cardLast1Min = getIntOrZero(out, "card_1min");
        result.cardLast1Hour = getIntOrZero(out, "card_1hour");
        result.cardLast24Hour = getIntOrZero(out, "card_24hour");
        result.ipLast1Min = getIntOrZero(out, "ip_1min");
        result.ipLast1Hour = getIntOrZero(out, "ip_1hour");
        log.debug("[TIMING] checkAllVelocity proc took {}ms", System.currentTimeMillis() - start);
        return result;
    }

    private int getIntOrZero(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val instanceof Integer ? (Integer) val : 0;
    }



    public int checkIpVelocity(String ipAddress, Instant since) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_ip_address", ipAddress)
                .addValue("p_since", since == null ? null : Timestamp.from(since));

        Map<String, Object> out = checkIpVelocityProc.execute(in);

        Integer count = (Integer) out.get("p_tx_count");
        return count != null ? count : 0;
    }


    public int checkCardVelocity(String cardNo, Instant since) {

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_card_no", cardNo)
                .addValue("p_since",
                        since == null ? null : Timestamp.from(since));

        Map<String, Object> out = checkCardVelocityProc.execute(in);

        Integer count = (Integer) out.get("p_tx_count");
        return count != null ? count : 0;
    }

    public String checkAbnormalAmount(String cardNo, BigDecimal amount) {
        long start = System.currentTimeMillis();
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_card_no", cardNo)
                .addValue("p_amount", amount);

        Map<String, Object> out = checkAbnormalAmountProc.execute(in);
        Object decision = out.get("p_decision");
        log.debug("[TIMING] checkAbnormalAmount proc took {}ms, decision={}", System.currentTimeMillis() - start, decision);
        return decision != null ? decision.toString() : "ALLOW";
    }

    public LastTransactionInfo getLastTransactionWithLocation(String cardNo) {
        String sql = """
          SELECT TOP 1 latitude, longitude, transaction_time
          FROM transactions
          WHERE card_no = ?
            AND latitude IS NOT NULL
          ORDER BY transaction_time DESC
          """;

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{cardNo}, (rs, rowNum) -> {
                LastTransactionInfo info = new LastTransactionInfo();
                info.setLatitude(rs.getDouble("latitude"));
                info.setLongitude(rs.getDouble("longitude"));
                info.setTransactionTime(rs.getTimestamp("transaction_time").toInstant());
                return info;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int countDistinctCardsPerDevice(String deviceFingerprint, Instant since) {
        String sql = """
          SELECT COUNT(DISTINCT card_no)
          FROM transactions
          WHERE device_fingerprint = ?
            AND transaction_time >= ?
          """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class,
                deviceFingerprint, Timestamp.from(since));
        return count != null ? count : 0;
    }

    public int countDistinctCardsPerIp(String ipAddress, Instant since) {
        String sql = """
          SELECT COUNT(DISTINCT card_no)
          FROM transactions
          WHERE ip_address = ?
            AND transaction_time >= ?
          """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class,
                ipAddress, Timestamp.from(since));
        return count != null ? count : 0;
    }

    public boolean isFirstTransaction(String cardNo) {
        long start = System.currentTimeMillis();
        String sql = "SELECT CASE WHEN EXISTS (SELECT 1 FROM transactions WHERE card_no = ?) THEN 0 ELSE 1 END";
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, cardNo);
        log.debug("[TIMING] isFirstTransaction took {}ms", System.currentTimeMillis() - start);
        return result != null && result == 1;
    }


}

