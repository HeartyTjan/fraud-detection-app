package com.interswitch.fraudtransactionapp.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.interswitch.fraudtransactionapp.dao.TransactionJdbcDao;
import com.interswitch.fraudtransactionapp.model.Transactions;
import com.interswitch.fraudtransactionapp.service.FlaggedTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlaggedTransactionServiceImpl implements FlaggedTransactionService {

    private final TransactionJdbcDao transactionJdbcDao;

    private final Cache<String, List<Transactions>> flaggedCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(Duration.ofSeconds(30))
            .build();

//    @Override
//    public List<Transactions> getAllFlagged() {
//        return transactionJdbcDao.findFlaggedTransactions();
//    }


//    @Override
//    public List<Transactions> getAllFlagged() {
//        return flaggedCache.get("all_flagged",
//                key -> transactionJdbcDao.findFlaggedTransactions());
//    }


    @Override
    public List<Transactions> getAllFlagged(int page, int size) {
        String key = page + "_" + size;

        return flaggedCache.get(key,
                k -> transactionJdbcDao.findFlaggedTransactions(page, size));
    }
//    @Override
//    public List<Transactions> getAllFlagged(int page, int limit) {
//        return transactionJdbcDao.findFlaggedTransactions(page, limit);
//    }

    @Override
    public List<Transactions> getBlocked() {
        return transactionJdbcDao.findBlockedTransactions();
    }

    @Override
    public List<Transactions> getReview() {
        return transactionJdbcDao.findReviewTransactions();
    }

    @Override
    public List<Transactions> getFlaggedByIp(String ip) {
        return transactionJdbcDao.findFlaggedByIp(ip);
    }

    @Override
    public List<Transactions> getFlaggedByCard(String cardNo) {
        return transactionJdbcDao.findFlaggedByCard(cardNo);
    }

    @Override
    public List<Transactions> getFlaggedByMerchant(String merchantId) {
        return transactionJdbcDao.findFlaggedByMerchant(merchantId);
    }
}

