package com.interswitch.fraudtransactionapp.fraudEngine;


import com.interswitch.fraudtransactionapp.config.FraudConfig;
import com.interswitch.fraudtransactionapp.config.TrackExecution;
import com.interswitch.fraudtransactionapp.dto.request.TransactionRequest;
import com.interswitch.fraudtransactionapp.fraudEngine.model.RuleContext;
import com.interswitch.fraudtransactionapp.fraudEngine.rule.FraudRule;
import com.interswitch.fraudtransactionapp.model.FraudDecision;
import com.interswitch.fraudtransactionapp.model.IpRisk;
import com.interswitch.fraudtransactionapp.model.MerchantRisk;
import com.interswitch.fraudtransactionapp.repository.BlacklistedCardRepository;
import com.interswitch.fraudtransactionapp.repository.IpRiskRepository;
import com.interswitch.fraudtransactionapp.repository.MerchantRiskRepository;
import com.interswitch.fraudtransactionapp.service.impl.RiskService;
import com.interswitch.fraudtransactionapp.util.mapper.FraudDecisionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@TrackExecution
public class FraudEngine {

    private final IpRiskRepository ipRiskRepository;
    private final MerchantRiskRepository merchantRiskRepository;
    private final BlacklistedCardRepository blacklistedCardRepository;
    private final List<FraudRule> rules;
    private final RiskService riskService;
    private final FraudConfig fraudConfig;


    public FraudDecision evaluate(TransactionRequest request) {

        RuleContext context = new RuleContext(request);
        context.setTriggeredRules(new HashMap<>());

        preloadData(context);

        for (FraudRule rule : rules) {
            var result = rule.evaluate(context);
            context.setCumulativeScore(context.getCumulativeScore() + result.getScore());

            if (result.isStopProcessing()) {
                if (result.getReason() != null) {
                    context.addTriggeredRule(rule.getClass().getSimpleName(), result.getReason());
                }
                return aggregateDecision(context);
            }
            if (result.getReason() != null) {
                context.addTriggeredRule(rule.getClass().getSimpleName(), result.getReason());
            }
        }

        return aggregateDecision(context);
    }

    private void preloadData(RuleContext context) {
        String ip = context.getRequest().getIpAddress();
        String merchantId = context.getRequest().getMerchantId();
        String cardNo = context.getRequest().getCardNo();

        Integer ipScore = riskService.getIpRisk(ip);
        if (ipScore == null) {
            ipScore = ipRiskRepository.findById(ip).map(IpRisk::getRiskScore).orElse(0);
            riskService.updateIpRisk(ip, ipScore);
        }

        Integer merchantScore = riskService.getMerchantRisk(merchantId);
        if (merchantScore == null) {
            merchantScore = merchantRiskRepository.findById(merchantId).map(MerchantRisk::getRiskScore).orElse(0);
            riskService.updateMerchantRisk(merchantId, merchantScore);
        }

        context.setIpRiskMap(Map.of(ip, ipScore));
        context.setMerchantRiskMap(Map.of(merchantId, merchantScore));
    }

    private FraudDecision aggregateDecision(RuleContext context) {
        int totalScore = context.getCumulativeScore();
        Map<String, String> triggered = context.getTriggeredRules();

        if (totalScore > fraudConfig.getThreshold().getBlock()) {
            return FraudDecisionMapper.block(totalScore, triggered);
        } else if (totalScore > fraudConfig.getThreshold().getReview()) {
            return FraudDecisionMapper.review(totalScore, triggered);
        }

        return FraudDecisionMapper.allow(totalScore, triggered);
    }
}