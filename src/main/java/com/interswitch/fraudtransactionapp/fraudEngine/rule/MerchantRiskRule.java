package com.interswitch.fraudtransactionapp.fraudEngine.rule;


import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudRuleResult;
import com.interswitch.fraudtransactionapp.fraudEngine.model.RuleContext;
import com.interswitch.fraudtransactionapp.util.mapper.FraudRuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(5)
public class MerchantRiskRule implements FraudRule {

    @Override
    public FraudRuleResult evaluate(RuleContext context) {

        String merchantId = context.getRequest().getMerchantId();
        int riskScore = context.getMerchantRiskMap().getOrDefault(merchantId, 0);

        if (riskScore > 50) {
            return FraudRuleMapper.mapToResult(riskScore, "MERCHANT_RISK_HIGH", false);
        }

        return FraudRuleMapper.mapToResult(0, null, false);
    }
}