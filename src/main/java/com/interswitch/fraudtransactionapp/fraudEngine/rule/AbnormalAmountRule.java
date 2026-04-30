package com.interswitch.fraudtransactionapp.fraudEngine.rule;

import com.interswitch.fraudtransactionapp.dao.FraudDao;
import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudRuleResult;
import com.interswitch.fraudtransactionapp.fraudEngine.model.RuleContext;
import com.interswitch.fraudtransactionapp.repository.FraudJdbcRepository;
import com.interswitch.fraudtransactionapp.util.mapper.FraudRuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Order(3)
public class AbnormalAmountRule implements FraudRule {

    private final FraudDao fraudDao;
//    private final FraudJdbcRepository fraudDao;

    @Override
    public FraudRuleResult evaluate(RuleContext context) {
        String cardNo = context.getRequest().getCardNo();
        BigDecimal amount = context.getRequest().getAmount();

        String decision = fraudDao.checkAbnormalAmount(cardNo, amount);

        int score = 0;
        String reason = null;

        switch (decision) {
            case "BLOCK" -> {
                score = 60;
                reason = "ABNORMAL_AMOUNT_BLOCK";
            }
            case "REVIEW" -> {
                score = 40;
                reason = "ABNORMAL_AMOUNT_REVIEW";
            }
        }

        return FraudRuleMapper.mapToResult(score, reason, false);
    }
}