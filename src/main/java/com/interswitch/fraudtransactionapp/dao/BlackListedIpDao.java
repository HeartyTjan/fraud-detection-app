package com.interswitch.fraudtransactionapp.dao;

import com.interswitch.fraudtransactionapp.exception.ResourceNotFoundException;
import com.interswitch.fraudtransactionapp.model.BlacklistedIp;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class BlackListedIpDao extends BaseDao<BlacklistedIp> {

    private SimpleJdbcCall upsertBlacklistedIp, updateBlacklistedIp;

    public BlackListedIpDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        InitializedJdbcCall();
    }

    @Override
    public BlacklistedIp save(BlacklistedIp blacklistedIp) {
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("p_ip", blacklistedIp.getIp())
                .addValue("p_last_updated",
                        blacklistedIp.getLastUpdated() == null ? null : Timestamp.valueOf(blacklistedIp.getLastUpdated()));

        Map<String, Object> result = upsertBlacklistedIp.execute(param);

        List<BlacklistedIp> savedIp= (List<BlacklistedIp>) result.get(SINGLE_RESULT);
        return (savedIp == null || savedIp.isEmpty()) ? null : savedIp.get(0);
    }

    @Override
    public BlacklistedIp update(BlacklistedIp blacklistedIp) {
        findById(blacklistedIp.getIp())
                .orElseThrow(()-> new ResourceNotFoundException("Blacklisted merchant not found with id: " + blacklistedIp.getIp()));

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("p_ip", blacklistedIp.getIp())
                .addValue("p_last_updated",
                        blacklistedIp.getLastUpdated() == null ? null : Timestamp.from(Instant.from(blacklistedIp.getLastUpdated())));

        Map<String, Object> result = updateBlacklistedIp.execute(param);

        List<BlacklistedIp> updatedIp= (List<BlacklistedIp>) result.get(SINGLE_RESULT);
        return (updatedIp == null || updatedIp.isEmpty()) ? null : updatedIp.get(0);
    }

    public Optional<BlacklistedIp> findById(String ipTobeBlacklisted) {
        String sql = "SELECT * FROM blacklisted_ip WHERE ip = ?";

        List<BlacklistedIp> foundBlacklistedIp =  jdbcTemplate.query(
                sql,
                BeanPropertyRowMapper.newInstance(BlacklistedIp.class),
                ipTobeBlacklisted
        );

        return foundBlacklistedIp.isEmpty() ? Optional.empty() : Optional.ofNullable(foundBlacklistedIp.get(0));

    }

    public List<BlacklistedIp> findAll() {
        String sql = "SELECT * FROM blacklisted_ip";
        return jdbcTemplate.query(
                sql,
                BeanPropertyRowMapper.newInstance(BlacklistedIp.class)
        );
    }

    private void InitializedJdbcCall() {

        this.updateBlacklistedIp = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("update_blacklisted_ip");


        this.upsertBlacklistedIp = new SimpleJdbcCall(jdbcTemplate).withProcedureName("upsert_blacklisted_ip")
                .returningResultSet(SINGLE_RESULT, BeanPropertyRowMapper.newInstance(BlacklistedIp.class));

        this.updateBlacklistedIp = new SimpleJdbcCall(jdbcTemplate).withProcedureName("update_blacklisted_ip")
                .returningResultSet(SINGLE_RESULT, BeanPropertyRowMapper.newInstance(BlacklistedIp.class));

//        this.findByIdJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("find_blacklisted_ip_by_id")
//                .returningResultSet(SINGLE_RESULT, BeanPropertyRowMapper.newInstance(BlacklistedIp.class));
//
//       this.findAllJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("find_all_blacklisted_ip")
//                .returningResultSet(MULTIPLE_RESULT, BeanPropertyRowMapper.newInstance(BlacklistedIp.class));
    }
}
