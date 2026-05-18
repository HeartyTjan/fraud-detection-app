package com.interswitch.fraudtransactionapp.fraudEngine.rule;

import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudRuleResult;
import com.interswitch.fraudtransactionapp.fraudEngine.model.RuleContext;
import com.interswitch.fraudtransactionapp.util.mapper.FraudRuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
@Order(7)
public class AbnormalAmountRule implements FraudRule {

    private static final BigDecimal HIGH_THRESHOLD = new BigDecimal("100000");
    private static final BigDecimal MEDIUM_THRESHOLD = new BigDecimal("50000");

    @Override
    public FraudRuleResult evaluate(RuleContext context) {

        if (context.fraudProfile().isFirstTransaction()) {
            return FraudRuleMapper.mapToResult(0, null, false);
        }
        BigDecimal amount = context.request().getAmount();

        BigDecimal avg = context.fraudProfile().getAverageTransactionAmount();

        if (avg == null || avg.compareTo(BigDecimal.ZERO) <= 0) {
            return FraudRuleMapper.mapToResult(0, null, false);
        }

        BigDecimal ratio = amount.divide(avg, 2, RoundingMode.HALF_UP);

        if (amount.compareTo(HIGH_THRESHOLD) > 0 || ratio.compareTo(new BigDecimal("5")) > 0) {
            return FraudRuleMapper.mapToResult(60, "ABNORMAL_AMOUNT_HIGH", false);
        }

        if (amount.compareTo(MEDIUM_THRESHOLD) > 0 || ratio.compareTo(new BigDecimal("3")) > 0) {
            return FraudRuleMapper.mapToResult(40, "ABNORMAL_AMOUNT_REVIEW", false);
        }

        return FraudRuleMapper.mapToResult(0, null, false);
    }
}