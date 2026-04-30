

package com.interswitch.fraudtransactionapp.dao;

import com.interswitch.fraudtransactionapp.exception.ResourceNotFoundException;
import com.interswitch.fraudtransactionapp.model.BlacklistedMerchant;
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
public class BlackListedMerchantDao extends BaseDao<BlacklistedMerchant> {

    private SimpleJdbcCall upsertBlacklistedMerchant, updateBlacklistedMerchant;

    public BlackListedMerchantDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        InitializedJdbcCall();
    }

    @Override
    @SuppressWarnings("unchecked")
    public BlacklistedMerchant save(BlacklistedMerchant blacklistedMerchant) {
        SqlParameterSource param =  new MapSqlParameterSource()
                .addValue("p_merchant_id", blacklistedMerchant.getMerchantId())
                .addValue("p_last_updated",
                        blacklistedMerchant.getLastUpdated() == null ? null : Timestamp.valueOf(blacklistedMerchant.getLastUpdated()));

        Map<String, Object> result = upsertBlacklistedMerchant.execute(param);
        List<BlacklistedMerchant> merchants  =  (List<BlacklistedMerchant>) result.get(SINGLE_RESULT);
        return (merchants == null || merchants.isEmpty()) ? null : merchants.get(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public BlacklistedMerchant update(BlacklistedMerchant blacklistedMerchant) {
        findById(blacklistedMerchant.getMerchantId())
               .orElseThrow(()-> new ResourceNotFoundException("Blacklisted merchant not found with id: " + blacklistedMerchant.getMerchantId()));

        SqlParameterSource param =  new MapSqlParameterSource()
                .addValue("p_merchant_id", blacklistedMerchant.getMerchantId())
                .addValue("p_last_updated",
                        blacklistedMerchant.getLastUpdated() == null ? null : Timestamp.from(Instant.from(blacklistedMerchant.getLastUpdated())));

        Map<String, Object> result = updateBlacklistedMerchant.execute(param);
        List<BlacklistedMerchant> merchants = (List<BlacklistedMerchant>) result.get(SINGLE_RESULT);
        return (merchants == null || merchants.isEmpty()) ? null : merchants.get(0);
    }

    public Optional<BlacklistedMerchant> findById(String merchantId) {
        String sql = "SELECT * FROM blacklisted_merchant WHERE merchant_id = ?";
        List<BlacklistedMerchant> foundBlacklistedMerchants =  jdbcTemplate.query(
                sql,
                BeanPropertyRowMapper.newInstance(BlacklistedMerchant.class),
                merchantId
        );
        return foundBlacklistedMerchants.isEmpty() ? Optional.empty() : Optional.of(foundBlacklistedMerchants.get(0));
    }

    public List<BlacklistedMerchant> findAll() {
            String sql = "SELECT merchant_id, last_updated FROM blacklisted_merchant";
            return jdbcTemplate.query(
                    sql,
                    BeanPropertyRowMapper.newInstance(BlacklistedMerchant.class)
            );
    }


    private void InitializedJdbcCall() {
        this.upsertBlacklistedMerchant = new SimpleJdbcCall(jdbcTemplate).withProcedureName("upsert_blacklisted_merchant")
                .returningResultSet(SINGLE_RESULT, BeanPropertyRowMapper.newInstance(BlacklistedMerchant.class));

        this.updateBlacklistedMerchant = new SimpleJdbcCall(jdbcTemplate).withProcedureName("update_blacklisted_merchant")
                .returningResultSet(SINGLE_RESULT, BeanPropertyRowMapper.newInstance(BlacklistedMerchant.class));

//        this.findByIdJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("find_blacklisted_merchant_by_id")
//                .returningResultSet(SINGLE_RESULT, BeanPropertyRowMapper.newInstance(BlacklistedMerchant.class));
//
//        this.findAllJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("find_all_blacklisted_merchants")
//                .returningResultSet(MULTIPLE_RESULT, BeanPropertyRowMapper.newInstance(BlacklistedMerchant.class));


   }

}
