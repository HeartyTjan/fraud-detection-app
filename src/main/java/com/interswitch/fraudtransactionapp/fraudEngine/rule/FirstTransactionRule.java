package com.interswitch.fraudtransactionapp.fraudEngine.rule;


import com.interswitch.fraudtransactionapp.dao.FraudDao;
import com.interswitch.fraudtransactionapp.dto.request.TransactionRequest;
import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudRuleResult;
import com.interswitch.fraudtransactionapp.fraudEngine.model.RuleContext;
import com.interswitch.fraudtransactionapp.util.mapper.FraudRuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Order(2)
public class FirstTransactionRule implements FraudRule {

    private final FraudDao fraudDao;

    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("100000");
    private static final int FIRST_TX_HIGH_VALUE_SCORE = 45;
    private static final int FIRST_TX_BASE_SCORE = 15;

    @Override
    public FraudRuleResult evaluate(RuleContext context) {
        TransactionRequest request = context.getRequest();

        boolean isFirstTransaction = fraudDao.isFirstTransaction(request.getCardNo());

        if (!isFirstTransaction) {
            return FraudRuleMapper.mapToResult(0, null, false);
        }

        if (request.getAmount().compareTo(HIGH_VALUE_THRESHOLD) > 0) {
            return FraudRuleMapper.mapToResult(
                    FIRST_TX_HIGH_VALUE_SCORE,
                    "FIRST_TX_HIGH_VALUE",
                    false
            );
        }

        return FraudRuleMapper.mapToResult(
                FIRST_TX_BASE_SCORE,
                "FIRST_TRANSACTION",
                false
        );
    }
}