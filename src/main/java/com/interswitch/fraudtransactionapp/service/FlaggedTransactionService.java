package com.interswitch.fraudtransactionapp.service;

import com.interswitch.fraudtransactionapp.model.Transactions;

import java.util.List;

public interface FlaggedTransactionService {
//    List<Transactions> getAllFlagged();

    List<Transactions> getAllFlagged(int page, int limit);

    List<Transactions> getBlocked();

    List<Transactions> getReview();

    List<Transactions> getFlaggedByIp(String ip);

    List<Transactions> getFlaggedByCard(String cardNo);

    List<Transactions> getFlaggedByMerchant(String merchantId);

}