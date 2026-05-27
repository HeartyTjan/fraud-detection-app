package com.interswitch.fraudtransactionapp.fraudEngine;


import com.interswitch.fraudtransactionapp.config.FraudConfig;
import com.interswitch.fraudtransactionapp.config.TrackExecution;
import com.interswitch.fraudtransactionapp.dto.request.TransactionRequest;
import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudRuleResult;
import com.interswitch.fraudtransactionapp.fraudEngine.model.RuleContext;
import com.interswitch.fraudtransactionapp.fraudEngine.rule.BlockingRule;
import com.interswitch.fraudtransactionapp.fraudEngine.rule.FraudRule;
import com.interswitch.fraudtransactionapp.model.FraudDecision;
import com.interswitch.fraudtransactionapp.model.IpRisk;
import com.interswitch.fraudtransactionapp.model.MerchantRisk;
import com.interswitch.fraudtransactionapp.repository.BlacklistedCardRepository;
import com.interswitch.fraudtransactionapp.repository.IpRiskRepository;
import com.interswitch.fraudtransactionapp.repository.MerchantRiskRepository;
import com.interswitch.fraudtransactionapp.service.impl.RiskService;
import com.interswitch.fraudtransactionapp.util.mapper.FraudDecisionMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
@TrackExecution
public class FraudEngine {

    private final IpRiskRepository ipRiskRepository;
    private final MerchantRiskRepository merchantRiskRepository;
    private final BlacklistedCardRepository blacklistedCardRepository;
    private final List<FraudRule> rules;
    private final RiskService riskService;
    private final FraudConfig fraudConfig;
    private final ExecutorService fraudRuleExecutor;
    private final MeterRegistry meterRegistry;


    private List<BlockingRule> blockingRules;
    private List<FraudRule> scoringRules;

    public FraudEngine(
            IpRiskRepository ipRiskRepository,
            MerchantRiskRepository merchantRiskRepository,
            BlacklistedCardRepository blacklistedCardRepository,
            List<FraudRule> rules,
            RiskService riskService,
            FraudConfig fraudConfig,
            @Qualifier("fraudRuleExecutor") ExecutorService fraudRuleExecutor
    ) {
        this.ipRiskRepository = ipRiskRepository;
        this.merchantRiskRepository = merchantRiskRepository;
        this.blacklistedCardRepository = blacklistedCardRepository;
        this.rules = rules;
        this.riskService = riskService;
        this.fraudConfig = fraudConfig;
        this.fraudRuleExecutor = fraudRuleExecutor;
    }

    @PostConstruct
    public void init() {
        blockingRules = new ArrayList<>();
        scoringRules = new ArrayList<>();

        for (FraudRule rule : rules) {
            if (rule instanceof BlockingRule) {
                blockingRules.add((BlockingRule) rule);
            } else {
                scoringRules.add(rule);
            }
        }

        log.info("FraudEngine initialized: {} blocking rules, {} scoring rules (parallel)",
                blockingRules.size(), scoringRules.size());
    }

    private List<BlockingRule> blockingRules;
    private List<FraudRule> scoringRules;

    public FraudEngine(
            IpRiskRepository ipRiskRepository,
            MerchantRiskRepository merchantRiskRepository,
            BlacklistedCardRepository blacklistedCardRepository,
            List<FraudRule> rules,
            RiskService riskService,
            FraudConfig fraudConfig,
            MeterRegistry meterRegistry,
            @Qualifier("fraudRuleExecutor") ExecutorService fraudRuleExecutor
    ) {
        this.ipRiskRepository = ipRiskRepository;
        this.merchantRiskRepository = merchantRiskRepository;
        this.blacklistedCardRepository = blacklistedCardRepository;
        this.rules = rules;
        this.riskService = riskService;
        this.fraudConfig = fraudConfig;
        this.meterRegistry = meterRegistry;
        this.fraudRuleExecutor = fraudRuleExecutor;
    }

    @PostConstruct
    public void init() {
        blockingRules = new ArrayList<>();
        scoringRules = new ArrayList<>();

        for (FraudRule rule : rules) {
            if (rule instanceof BlockingRule) {
                blockingRules.add((BlockingRule) rule);
            } else {
                scoringRules.add(rule);
            }
        }

        log.info("FraudEngine initialized: {} blocking rules, {} scoring rules (parallel)",
                blockingRules.size(), scoringRules.size());
    }

    public FraudDecision evaluate(TransactionRequest request) {
        Timer.Sample sample = Timer.start(meterRegistry);


        long totalStart = System.currentTimeMillis();
        log.info("=== FRAUD EVALUATION START === card={}, merchant={}, ip={}, amount={}",
                maskCard(request.getCardNo()), request.getMerchantId(), request.getIpAddress(), request.getAmount());

        RuleContext context = new RuleContext(request);
        context.setTriggeredRules(new HashMap<>());

        long preloadStart = System.currentTimeMillis();
        preloadData(context);
        log.info("[TIMING] preloadData took {}ms", System.currentTimeMillis() - preloadStart);

        for (BlockingRule rule : blockingRules) {
            String ruleName = rule.getClass().getSimpleName();
            long ruleStart = System.currentTimeMillis();
            var result = rule.evaluate(context);
            long ruleTime = System.currentTimeMillis() - ruleStart;

            context.setCumulativeScore(context.getCumulativeScore() + result.getScore());

            if (result.getScore() > 0) {
                log.info("[{}] TRIGGERED: reason={}, score=+{}, cumulative={}, took {}ms",
                        ruleName, result.getReason(), result.getScore(), context.getCumulativeScore(), ruleTime);
            } else {
                log.debug("[{}] PASSED: score=0, took {}ms", ruleName, ruleTime);
            }

            if (result.isStopProcessing()) {
                log.warn("[{}] STOP PROCESSING: reason={}", ruleName, result.getReason());
                if (result.getReason() != null) {
                    context.addTriggeredRule(ruleName, result.getReason());
                }
                FraudDecision decision = aggregateDecision(context);
                log.info("=== FRAUD EVALUATION END === decision={}, finalScore={}, totalTime={}ms",
                        decision.getDecision(), decision.getRiskScore(), System.currentTimeMillis() - totalStart);

                Counter.builder("fraud.decisions")
                        .tag("type", decision.getDecision().name())
                        .register(meterRegistry)
                        .increment();

                sample.stop(Timer.builder("fraud.engine.latency")
                        .tag("stage", "evaluate")
                        .register(meterRegistry));


                return decision;
            }

            if (result.getReason() != null) {
                context.addTriggeredRule(ruleName, result.getReason());
            }
        }

        long parallelStart = System.currentTimeMillis();
        List<CompletableFuture<RuleResultHolder>> futures = new ArrayList<>();

        for (FraudRule rule : scoringRules) {
            String ruleName = rule.getClass().getSimpleName();
            CompletableFuture<RuleResultHolder> future = CompletableFuture.supplyAsync(() -> {
                long ruleStart = System.currentTimeMillis();
                FraudRuleResult result = rule.evaluate(context);
                long ruleTime = System.currentTimeMillis() - ruleStart;
                return new RuleResultHolder(ruleName, result, ruleTime);
            }, fraudRuleExecutor);
            futures.add(future);
        }


        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        for (CompletableFuture<RuleResultHolder> future : futures) {
            RuleResultHolder holder = future.join();

            context.setCumulativeScore(context.getCumulativeScore() + holder.result.getScore());

            if (holder.result.getScore() > 0) {
                log.info("[{}] TRIGGERED: reason={}, score=+{}, cumulative={}, took {}ms",
                        holder.ruleName, holder.result.getReason(), holder.result.getScore(),
                        context.getCumulativeScore(), holder.executionTimeMs);
            } else {
                log.debug("[{}] PASSED: score=0, took {}ms", holder.ruleName, holder.executionTimeMs);
            }

            if (holder.result.getReason() != null) {
                context.addTriggeredRule(holder.ruleName, holder.result.getReason());
            }
        }

        log.info("[TIMING] parallel rules took {}ms total", System.currentTimeMillis() - parallelStart);

        FraudDecision decision = aggregateDecision(context);
        log.info("=== FRAUD EVALUATION END === decision={}, finalScore={}, totalTime={}ms",
                decision.getDecision(), decision.getRiskScore(), System.currentTimeMillis() - totalStart);

        Counter.builder("fraud.decisions")
                .tag("type", decision.getDecision().name())
                .register(meterRegistry)
                .increment();

        sample.stop(Timer.builder("fraud.engine.latency")
                .tag("stage", "evaluate")
                .register(meterRegistry));


        return decision;
    }

    private record RuleResultHolder(String ruleName, FraudRuleResult result, long executionTimeMs) {}

    private String maskCard(String cardNo) {
        if (cardNo == null || cardNo.length() < 8) return "****";
        return cardNo.substring(0, 6) + "****" + cardNo.substring(cardNo.length() - 4);
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

        log.debug("Preloaded risk data: ipRisk={}, merchantRisk={}", ipScore, merchantScore);
        context.setIpRiskMap(Map.of(ip, ipScore));
        context.setMerchantRiskMap(Map.of(merchantId, merchantScore));
    }

    private FraudDecision aggregateDecision(RuleContext context) {
        int totalScore = Math.min(100, context.getCumulativeScore());
        Map<String, String> triggered = context.getTriggeredRules();

        if (totalScore > fraudConfig.getThreshold().getBlock()) {
            return FraudDecisionMapper.block(totalScore, triggered);
        } else if (totalScore > fraudConfig.getThreshold().getReview()) {
            return FraudDecisionMapper.review(totalScore, triggered);
        }

        return FraudDecisionMapper.allow(totalScore, triggered);
    }
}


