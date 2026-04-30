//package com.interswitch.fraudtransactionapp.fraudEngine.rule;
//
//import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudRuleResult;
//import com.interswitch.fraudtransactionapp.fraudEngine.model.RuleContext;
//import com.interswitch.fraudtransactionapp.service.IsolationForestModelService;
//import com.interswitch.fraudtransactionapp.util.helper.DataNormalization;
//import com.interswitch.fraudtransactionapp.util.mapper.FraudRuleMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.stereotype.Component;
//import smile.anomaly.IsolationForest;
//
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//
//@Component
//@RequiredArgsConstructor
//public class IsolationForestFraudRule implements FraudRule {
//
//    private static final Logger log = LoggerFactory.getLogger(IsolationForestFraudRule.class);
//    private IsolationForest isolationForest;
//    private final IsolationForestModelService modelService;
//
//    @Override
//    public FraudRuleResult evaluate(RuleContext context) {
//        if (isolationForest == null) {
//            isolationForest = modelService.getIsolationForestModel();
//
//        }
//        log.info("isolationForest is {}", isolationForest);
//
//        double[] details = extractDetails(context);
//
//        double score = isolationForest.score(details);
//
//        if (score < -0.8) {
//            return FraudRuleMapper.mapToResult(-10, "Anomalous transaction detected by Isolation Forest", true);
//        } else if (score < 0.0) {
//            return FraudRuleMapper.mapToResult(-5, "Suspicious transaction detected by Isolation Forest", true);
//        } else {
//            return FraudRuleMapper.mapToResult(0, null, false);
//        }
//    }
//
////    private double[] extractDetails(RuleContext context) {
////        double amount = context.getRequest().getAmount().doubleValue();
////        double type = encodeTransactionType(context.getRequest().getType());
////        double time = getTransactionHour(context.getRequest().getTransactionTime());
////        return new double[]{amount, type, time};
////    }
//    private double[] extractDetails(RuleContext context) {
//        double amount = context.getRequest().getAmount().doubleValue();
//        double type = encodeTransactionType(context.getRequest().getType());
//
//        double merchant = Double.parseDouble(context.getRequest().getMerchantId());
//        double ip = Double.parseDouble(context.getRequest().getIpAddress());
//
//        double hour = getTransactionHour(context.getRequest().getTransactionTime());
//
//        return new double[]{amount, merchant, ip, hour};
//    }
//
//    private double encodeTransactionType(String type) {
//        if ("DEPOSIT".equals(type)) return 0;
//        if ("WITHDRAWAL".equals(type)) return 1;
//        return -1;
//    }
//
//    private double getTransactionHour(Instant transactionTime) {
//        LocalDateTime localDateTime = transactionTime.atZone(ZoneId.of("UTC")).toLocalDateTime();
//        return localDateTime.getHour();
//    }
//
//
//}
