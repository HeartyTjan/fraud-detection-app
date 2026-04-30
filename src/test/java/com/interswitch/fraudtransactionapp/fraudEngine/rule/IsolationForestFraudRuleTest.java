//package com.interswitch.fraudtransactionapp.fraudEngine.rule;
//
//import com.interswitch.fraudtransactionapp.dto.request.TransactionRequest;
//import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudRuleResult;
//import com.interswitch.fraudtransactionapp.fraudEngine.model.RuleContext;
//import com.interswitch.fraudtransactionapp.model.Transactions;
//import com.interswitch.fraudtransactionapp.repository.TransactionRepository;
//import com.interswitch.fraudtransactionapp.service.IsolationForestModelService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import smile.anomaly.IsolationForest;
//
//import java.math.BigDecimal;
//import java.time.Instant;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//public class IsolationForestFraudRuleTest {
//
//    @Autowired
//    private IsolationForestFraudRule isolationForestFraudRule;
//
//    @Autowired
//    private TransactionRepository transactionRepository;
//
//    private IsolationForest isolationForest;
//
//    @Autowired
//    private IsolationForestModelService isolationForestModelService;
//
//    @BeforeEach
//    public void setUp() {
//        isolationForest = isolationForestModelService.getIsolationForestModel();
//    }
//
//    @Test
//    public void testEvaluateFraudulentTransaction() {
//        // Ensure the model is trained using real data from the database
//        double[][] trainingData = fetchTransactionData();
//        isolationForest = IsolationForest.fit(trainingData);  // Train the Isolation Forest model
//
//        // Test case for a normal transaction
//        double[] normalTransaction = {1200.0, 0.0, 30, 0, 0, 14};  // 1200 amount, 0 for APPROVE decision, risk score 30, hour 14
//        double normalScore = isolationForest.score(normalTransaction);
//        System.out.println("Normal transaction score: " + normalScore);
//
//        // Assert that normal transactions are not flagged as anomalies
//        assertTrue(normalScore >= -1 && normalScore <= 1);  // Adjust based on training data score distribution
//
//        // Test case for a fraudulent transaction (high amount and suspicious time)
//        double[] fraudulentTransaction = {5000000.0, 2.0, 95, 1, 1, 3};  // High amount, 2 for BLOCK decision, risk score 95, hour 3
//        double fraudScore = isolationForest.score(fraudulentTransaction);
//        System.out.println("Fraudulent transaction score: " + fraudScore); // Expecting a negative score (anomaly)
//
//        assertTrue(fraudScore < 0.0);  // Lower threshold for fraud detection
//
//
//        assertTrue(fraudScore < normalScore);
//    }
//
//    private double[][] fetchTransactionData() {
//        List<Transactions> transactions = transactionRepository.findAll();
//        double[][] data = new double[transactions.size()][6];
//
//        Map<String, Integer> merchantMap = new HashMap<>();
//        Map<String, Integer> ipMap = new HashMap<>();
//
//        for (int i = 0; i < transactions.size(); i++) {
//            Transactions tx = transactions.get(i);
//
//            data[i][0] = tx.getAmount().doubleValue();
//
//            data[i][1] = tx.getRiskScore();
//
//            String merchantId = tx.getMerchantId();
//            merchantMap.putIfAbsent(merchantId, merchantMap.size());
//            data[i][2] = merchantMap.get(merchantId);
//
//            String ipAddress = tx.getIpAddress();
//            ipMap.putIfAbsent(ipAddress, ipMap.size());
//            data[i][3] = ipMap.get(ipAddress);
//
//            Instant instant = tx.getCreatedAt();
//            ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
//            data[i][4] = zdt.getHour();
//        }
//
//        return data;
//    }
//
//}
