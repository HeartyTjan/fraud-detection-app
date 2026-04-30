package com.interswitch.fraudtransactionapp.service;

import com.interswitch.fraudtransactionapp.model.Transactions;
import com.interswitch.fraudtransactionapp.repository.TransactionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smile.anomaly.IsolationForest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IsolationForestModelService {

    private final TransactionRepository transactionRepository;
    private IsolationForest isolationForest;

    @PostConstruct
    public void init() {
        trainModelFromDb();
    }
    public void trainModelFromDb() {
        double[][] trainingData = fetchTransactionData();
        if (trainingData.length == 0) {
            System.out.println("No transaction data available for training. Model will be trained when data is available.");
            return;
        }
        isolationForest = IsolationForest.fit(trainingData);
        System.out.println("Isolation Forest trained on " + trainingData.length + " transactions");
    }

    public IsolationForest getIsolationForestModel() {
        return isolationForest;
    }

    private double[][] fetchTransactionData() {
        List<Transactions> transactions = transactionRepository.findAll();
        double[][] data = new double[transactions.size()][4];

        Map<String, Integer> merchantMap = new HashMap<>();
        Map<String, Integer> ipMap = new HashMap<>();

        for (int i = 0; i < transactions.size(); i++) {
            Transactions tx = transactions.get(i);

            data[i][0] = tx.getAmount().doubleValue();


            String merchantId = tx.getMerchantId();
            merchantMap.putIfAbsent(merchantId, merchantMap.size());
            data[i][1] = merchantMap.get(merchantId);

            String ipAddress = tx.getIpAddress();
            ipMap.putIfAbsent(ipAddress, ipMap.size());
            data[i][2] = ipMap.get(ipAddress);

            LocalDateTime createdAt = tx.getCreatedAt();
            data[i][3] = createdAt.getHour();
        }

        return data;
    }

}