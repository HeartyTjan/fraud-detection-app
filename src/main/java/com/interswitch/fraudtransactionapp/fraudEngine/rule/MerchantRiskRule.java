package com.interswitch.fraudtransactionapp.fraudEngine.rule;

import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudProfile;
import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudRuleResult;
import com.interswitch.fraudtransactionapp.fraudEngine.model.RuleContext;
import com.interswitch.fraudtransactionapp.util.mapper.FraudRuleMapper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(5)
public class MerchantRiskRule implements FraudRule {

    @Override
    public FraudRuleResult evaluate(RuleContext context) {


        int riskScore = context.fraudProfile().getMerchantRiskScore();


        if (riskScore > 50) {
            return FraudRuleMapper.mapToResult(
                    riskScore,
                    "MERCHANT_RISK_HIGH",
                    false
            );
        }

        return FraudRuleMapper.mapToResult(0, null, false);
    }
}