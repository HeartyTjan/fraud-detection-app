package com.interswitch.fraudtransactionapp.fraudEngine.rule;


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

    @Override
    public FraudRuleResult evaluate(RuleContext context) {
        String ip = context.getRequest().getIpAddress();
        int riskScore = context.getIpRiskMap().getOrDefault(ip,0);

        if (riskScore > 50) {
            return FraudRuleMapper.mapToResult(riskScore, "IP_RISK_HIGH", false);
        }

        return FraudRuleMapper.mapToResult(0, null, false);
    }
//    @Override
//    public FraudRuleResult evaluate(RuleContext context) {
//
//        String ip = context.getRequest().getIpAddress();
//
//        Integer riskScore = riskCache.getIpRisk(ip);
//
//        if (riskScore == null) {
//            riskScore = ipRiskRepository.findById(ip)
//                    .map(entity -> entity.getRiskScore())
//                    .orElse(0);
//            riskCache.setIpRisk(ip, riskScore);
//        }
//
//        if (riskScore > 50) {
//            return FraudRuleMapper.mapToResult(riskScore, "IP_RISK_HIGH",false);
//        }
//
//        return FraudRuleMapper.mapToResult(0, null,false);
//    }
}