package com.interswitch.fraudtransactionapp.fraudEngine.rule;

import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudProfile;
import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudRuleResult;
import com.interswitch.fraudtransactionapp.fraudEngine.model.RuleContext;
import com.interswitch.fraudtransactionapp.util.mapper.FraudRuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(4)
public class IpRiskRule implements FraudRule {

    private static final int THRESHOLD = 50;

    @Override
    public FraudRuleResult evaluate(RuleContext context) {

        FraudProfile profile = context.fraudProfile();

        int riskScore = profile.getIpRiskScore();

        if (riskScore > THRESHOLD) {
            return FraudRuleMapper.mapToResult(
                    riskScore,
                    "IP_RISK_HIGH",
                    false
            );
        }

        return FraudRuleMapper.mapToResult(0, null, false);
    }
}