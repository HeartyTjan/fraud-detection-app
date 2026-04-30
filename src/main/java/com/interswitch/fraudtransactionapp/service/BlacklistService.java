package com.interswitch.fraudtransactionapp.service;

import com.interswitch.fraudtransactionapp.dto.response.BlacklistResponse;
import com.interswitch.fraudtransactionapp.model.BlacklistedCard;
import com.interswitch.fraudtransactionapp.model.BlacklistedIp;
import com.interswitch.fraudtransactionapp.model.BlacklistedMerchant;

import java.util.List;

public interface BlacklistService {

    List<BlacklistedCard> getAllCards();
    BlacklistedCard getCard(String cardNo);

    List<BlacklistedIp> getAllIps();
    BlacklistedIp getIp(String ip);

    List<BlacklistedMerchant> getAllMerchants();
    BlacklistedMerchant getMerchant(String merchantId);
    BlacklistResponse getAllBlacklist();
}