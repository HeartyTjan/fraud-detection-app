package com.interswitch.fraudtransactionapp.fraudEngine.rule;

import com.interswitch.fraudtransactionapp.config.FraudConfig;
import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudProfile;
import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudRuleResult;
import com.interswitch.fraudtransactionapp.fraudEngine.model.RuleContext;
import com.interswitch.fraudtransactionapp.fraudEngine.model.VelocityResult;
import com.interswitch.fraudtransactionapp.util.mapper.FraudRuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(3)
public class VelocityRule implements FraudRule {

    private final FraudConfig fraudConfig;

    private static final int SCORE_1_MIN = 50;
    private static final int SCORE_1_HOUR = 40;
    private static final int SCORE_24_HOUR = 30;
    private static final int SCORE_IP_1_MIN = 45;
    private static final int SCORE_IP_1_HOUR = 35;

    @Override
    public FraudRuleResult evaluate(RuleContext context) {

        FraudProfile profile = context.fraudProfile();
        VelocityResult v = profile.getVelocityResult();

        int totalScore = 0;
        StringBuilder reasons = new StringBuilder();

        if (v.getCardLast1Min() > fraudConfig.getVelocity().getCard().getOneMin()) {
            totalScore += SCORE_1_MIN;
            reasons.append("CARD_VELOCITY_1_MIN_EXCEEDED;");
        }

        if (v.getCardLast1Hour() > fraudConfig.getVelocity().getCard().getOneHour()) {
            totalScore += SCORE_1_HOUR;
            reasons.append("CARD_VELOCITY_1_HOUR_EXCEEDED;");
        }

        if (v.getCardLast24Hour()> fraudConfig.getVelocity().getCard().getTwentyFourHour()) {
            totalScore += SCORE_24_HOUR;
            reasons.append("CARD_VELOCITY_24_HOUR_EXCEEDED;");
        }

        if (v.getIpLast1Min() > fraudConfig.getVelocity().getIp().getOneMin()) {
            totalScore += SCORE_IP_1_MIN;
            reasons.append("IP_VELOCITY_1_MIN_EXCEEDED;");
        }

        if (v.getIpLast1Hour() > fraudConfig.getVelocity().getIp().getOneHour()) {
            totalScore += SCORE_IP_1_HOUR;
            reasons.append("IP_VELOCITY_1_HOUR_EXCEEDED;");
        }

        String reason = reasons.isEmpty() ? null : reasons.toString();

        return FraudRuleMapper.mapToResult(totalScore, reason, false);
    }
}