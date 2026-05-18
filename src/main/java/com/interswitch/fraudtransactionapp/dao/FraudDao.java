package com.interswitch.fraudtransactionapp.dao;

import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudProfile;
import com.interswitch.fraudtransactionapp.fraudEngine.model.VelocityResult;
import com.interswitch.fraudtransactionapp.model.LastTransactionInfo;
import com.interswitch.fraudtransactionapp.model.RiskUpdateResult;
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
import java.util.Optional;

@Slf4j
@Repository
public class FraudDao {

    private final SimpleJdbcCall checkCardVelocityProc, checkAbnormalAmountProc, checkIpVelocityProc,
            checkAllVelocityProc, loadFraudProfile, applyBlockActions, updateRiskSCore;
    private final JdbcTemplate jdbcTemplate;

    public FraudDao(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
        this.checkCardVelocityProc = new SimpleJdbcCall(jdbcTemplate).withProcedureName("check_card_velocity");
        this.checkAbnormalAmountProc = new SimpleJdbcCall(jdbcTemplate).withProcedureName("check_abnormal_amount_proc");
        this.checkIpVelocityProc = new SimpleJdbcCall(jdbcTemplate).withProcedureName("check_ip_velocity");
        this.checkAllVelocityProc = new SimpleJdbcCall(jdbcTemplate).withProcedureName("check_all_velocity");
        this.loadFraudProfile = new SimpleJdbcCall(jdbcTemplate).withProcedureName("usp_LoadFraudProfile");
        this.applyBlockActions = new SimpleJdbcCall(jdbcTemplate).withProcedureName("usp_apply_block_actions");
        this.updateRiskSCore = new SimpleJdbcCall(jdbcTemplate).withProcedureName("usp_update_risk_scores");
    }


    public RiskUpdateResult updateRiskScores(String ip, String merchantId, int delta){
        long start = System.currentTimeMillis();

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("ip", ip)
                .addValue("merchantId", merchantId)
                .addValue("delta", delta);


        Map<String, Object> out = updateRiskSCore.execute(in);

        log.debug(
                "[TIMING] applyBlockActions took {}ms",
                System.currentTimeMillis() - start
        );

        Integer ipScore = Optional.ofNullable((Integer) out.get("ip_score")).orElse(0);
        Integer merchantScore = Optional.ofNullable((Integer) out.get("merchant_score")).orElse(0);
        return new RiskUpdateResult(
                ip,
                merchantId,
               ipScore,
                merchantScore
        );

    }

    public void applyBlockActions(String cardNo, String ip,String merchantId){
        long start = System.currentTimeMillis();

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("cardNo", cardNo)
                .addValue("ip", ip)
                .addValue("merchantId", merchantId);

        applyBlockActions.execute(in);

        log.debug(
                "[TIMING] applyBlockActions took {}ms",
                System.currentTimeMillis() - start
        );

    }

    public FraudProfile loadFraudProfile(
            String cardNo,
            String ipAddress,
            String merchantId,
            Instant transactionTime
    ) {

        long start = System.currentTimeMillis();

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("CardNo", cardNo)
                .addValue("IpAddress", ipAddress)
                .addValue("MerchantId", merchantId)
                .addValue("TransactionTime", Timestamp.from(transactionTime));

        Map<String, Object> row =
                loadFraudProfile.execute(in);

        VelocityResult velocity = new VelocityResult();
        velocity.setCardLast1Min(getInt(row, "card_last_1_min", 0));
        velocity.setCardLast1Hour(getInt(row, "card_last_1_hour", 0));
        velocity.setCardLast24Hour(getInt(row, "card_last_24_hour", 0));
        velocity.setIpLast1Min(getInt(row, "ip_last_1_min", 0));
        velocity.setIpLast1Hour(getInt(row, "ip_last_1_hour", 0));

        LastTransactionInfo lastTx = new LastTransactionInfo();
        lastTx.setLatitude(getDouble(row, "last_latitude"));
        lastTx.setLongitude(getDouble(row, "last_longitude"));
        lastTx.setTransactionTime(getInstant(row, "last_tx_time"));

        int count = getInt(row, "total_tx_count", 0);
        boolean firstTx = count == 0;

        FraudProfile profile = new FraudProfile(
                velocity,
                lastTx,
                firstTx,
                (BigDecimal) row.getOrDefault("average_amount", BigDecimal.ZERO),
                getInt(row, "card_last_24_hour", 0),
                getInt(row, "ip_risk_score", 0),
                getInt(row, "merchant_risk_score", 0)
        );

        log.debug("[TIMING] loadFraudProfile took {}ms",
                System.currentTimeMillis() - start);

        return profile;
    }

    public VelocityResult checkAllVelocity(String cardNo, String ipAddress, Instant now) {
        long start = System.currentTimeMillis();
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_card_no", cardNo)
                .addValue("p_ip_address", ipAddress)
                .addValue("p_now", Timestamp.from(now));

        Map<String, Object> out = checkAllVelocityProc.execute(in);

        VelocityResult result = new VelocityResult();
        result.setCardLast1Min(getIntOrZero(out, "card_1min"));
        result.setCardLast1Hour(getIntOrZero(out, "card_1hour"));
        result.setCardLast24Hour(getIntOrZero(out, "card_24hour"));
        result.setIpLast1Min(getIntOrZero(out, "ip_1min"));
        result.setIpLast1Hour(getIntOrZero(out, "ip_1hour"));
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

    private int getInt(Map<String, Object> map, String key, int i) {
        Object value = map.get(key);
        if (value == null) return 0;
        return ((Number) value).intValue();
    }

    private double getDouble(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val == null) return 0.0;
        return ((Number) val).doubleValue();
    }

    private Instant getInstant(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val == null) return null;
        return ((Timestamp) val).toInstant();
    }
}

