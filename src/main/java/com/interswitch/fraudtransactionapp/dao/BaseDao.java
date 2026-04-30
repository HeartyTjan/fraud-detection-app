package com.interswitch.fraudtransactionapp.dao;

import com.interswitch.fraudtransactionapp.model.BlacklistedMerchant;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public abstract class BaseDao<T> {
    protected static final String SINGLE_RESULT = "single";
    protected static final String MULTIPLE_RESULT = "list";
    protected static final String COUNT_RESULT = "count";

    protected JdbcTemplate jdbcTemplate;

    abstract T save(T entity);

    abstract T update(T entity);
}
