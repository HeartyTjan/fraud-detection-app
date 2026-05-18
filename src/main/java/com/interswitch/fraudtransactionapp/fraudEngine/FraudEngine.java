package com.interswitch.fraudtransactionapp.fraudEngine;

import com.interswitch.fraudtransactionapp.config.FraudConfig;
import com.interswitch.fraudtransactionapp.config.TrackExecution;
import com.interswitch.fraudtransactionapp.dao.FraudDao;
import com.interswitch.fraudtransactionapp.dto.request.TransactionRequest;
import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudProfile;
import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudRuleResult;
import com.interswitch.fraudtransactionapp.fraudEngine.model.RuleContext;
import com.interswitch.fraudtransactionapp.fraudEngine.rule.BlockingRule;
import com.interswitch.fraudtransactionapp.fraudEngine.rule.FraudRule;
import com.interswitch.fraudtransactionapp.model.FraudDecision;
import com.interswitch.fraudtransactionapp.service.impl.FraudProfileCacheService;
import com.interswitch.fraudtransactionapp.util.mapper.FraudDecisionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
@TrackExecution
public class FraudEngine {

    private final FraudProfileCacheService fraudProfileCacheService;
    private final FraudConfig fraudConfig;
    private final ExecutorService fraudRuleExecutor;
    private final List<FraudRule> rules;
    private final FraudDao fraudDao;

    private List<BlockingRule> blockingRules;
    private List<FraudRule> scoringRules;

    public FraudEngine(
            FraudConfig fraudConfig,
            List<FraudRule> rules,
            FraudProfileCacheService fraudProfileCacheService,
            FraudDao fraudDao,
            @Qualifier("fraudRuleExecutor") ExecutorService fraudRuleExecutor
    ) {
        this.fraudConfig = fraudConfig;
        this.rules = rules;
        this.fraudProfileCacheService = fraudProfileCacheService;
        this.fraudRuleExecutor = fraudRuleExecutor;
        this.fraudDao = fraudDao;
    }

    @PostConstruct
    public void init() {

        this.blockingRules = rules.stream()
                .filter(BlockingRule.class::isInstance)
                .map(BlockingRule.class::cast)
                .toList();

        this.scoringRules = rules.stream()
                .filter(rule -> !(rule instanceof BlockingRule))
                .toList();

        log.info("FraudEngine initialized: {} blocking rules, {} scoring rules",
                blockingRules.size(), scoringRules.size());
    }

    public FraudDecision evaluate(TransactionRequest request) {

        long startTime = System.currentTimeMillis();

        log.info("=== FRAUD START === card={}, merchant={}, ip={}, amount={}",
                maskCard(request.getCardNo()),
                request.getMerchantId(),
                request.getIpAddress(),
                request.getAmount());

        RuleContext context = preloadData(request);

        Map<String, String> triggeredRules = new HashMap<>();
        int totalScore = 0;

        for (BlockingRule rule : blockingRules) {

            String ruleName = rule.getClass().getSimpleName();
            long start = System.currentTimeMillis();

            FraudRuleResult result = rule.evaluate(context);

            long duration = System.currentTimeMillis() - start;

            if (result.getScore() > 0) {
                log.info("[{}] TRIGGERED reason={}, score={}, time={}ms",
                        ruleName, result.getReason(), result.getScore(), duration);
            }

            totalScore += result.getScore();

            if (result.getReason() != null) {
                triggeredRules.put(ruleName, result.getReason());
            }

            if (result.isStopProcessing()) {
                return aggregateDecision(totalScore, triggeredRules, startTime);
            }
        }

        long parallelStart = System.currentTimeMillis();

        List<CompletableFuture<RuleResultHolder>> futures = scoringRules.stream()
                .map(rule -> CompletableFuture.supplyAsync(() -> {

                    long start = System.currentTimeMillis();

                    FraudRuleResult result = rule.evaluate(context);

                    return new RuleResultHolder(
                            rule.getClass().getSimpleName(),
                            result,
                            System.currentTimeMillis() - start
                    );

                }, fraudRuleExecutor))
                .toList();

        List<RuleResultHolder> results = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        int scoringScore = 0;

        for (RuleResultHolder holder : results) {

            scoringScore += holder.result.getScore();

            if (holder.result.getReason() != null) {
                triggeredRules.put(holder.ruleName, holder.result.getReason());
            }

            if (holder.result.getScore() > 0) {
                log.info("[{}] TRIGGERED reason={}, score={}, time={}ms",
                        holder.ruleName,
                        holder.result.getReason(),
                        holder.result.getScore(),
                        holder.executionTimeMs);
            }
        }

        log.info("[TIMING] scoring rules took {}ms",
                System.currentTimeMillis() - parallelStart);

        return aggregateDecision(totalScore + scoringScore, triggeredRules, startTime);
    }

    private RuleContext preloadData(TransactionRequest request) {

//        FraudProfile profile = fraudProfileCacheService.get(request);
        FraudProfile profile = fraudDao.loadFraudProfile(
                request.getCardNo(),
                request.getIpAddress(),
                request.getMerchantId(),
                request.getTransactionTime()
        );
        System.out.printf(
                "=== DETAILED FRAUD PROFILE ===\n" +
                        "Velocity: %s\n" +
                        "trx:  %s\n" +
                        "avg amount: %f\n" +
                        "24hrs:    %d\n" +
                        "ipscores: %d\n" +
                        "merchantid: %d\n" +
                        "first trx: %b\n"+
                        "=============================%n",
                profile.getVelocityResult().toString(),
                profile.getLastTransaction().toString(),
                profile.getAverageTransactionAmount(),
                profile.getTransactionsLast24Hours(), // Adjust getter name based on your class properties
                profile.getIpRiskScore(),
                profile.getMerchantRiskScore(),
                profile.isFirstTransaction()// Adjust getter name based on your class properties
        );
        return new RuleContext(request, profile);
    }

    private FraudDecision aggregateDecision(
            int totalScore,
            Map<String, String> triggeredRules,
            long startTime
    ) {

        int score = Math.min(100, totalScore);

        FraudDecision decision;

        if (score > fraudConfig.getThreshold().getBlock()) {
            decision = FraudDecisionMapper.block(score, triggeredRules);
        } else if (score > fraudConfig.getThreshold().getReview()) {
            decision = FraudDecisionMapper.review(score, triggeredRules);
        } else {
            decision = FraudDecisionMapper.allow(score, triggeredRules);
        }

        log.info("=== FRAUD END === decision={}, totalTime={}ms",
                decision.getDecision(),
                System.currentTimeMillis() - startTime);

        return decision;
    }

    private String maskCard(String cardNo) {
        if (cardNo == null || cardNo.length() < 8) return "****";
        return cardNo.substring(0, 6) + "****" +
                cardNo.substring(cardNo.length() - 4);
    }

    private record RuleResultHolder(
            String ruleName,
            FraudRuleResult result,
            long executionTimeMs
    ) {}
}