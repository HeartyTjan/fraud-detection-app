package com.interswitch.fraudtransactionapp.service.impl;


import com.interswitch.fraudtransactionapp.dao.BlackListedMerchantDao;
import com.interswitch.fraudtransactionapp.dto.response.BlacklistResponse;
import com.interswitch.fraudtransactionapp.model.*;
import com.interswitch.fraudtransactionapp.repository.*;
import com.interswitch.fraudtransactionapp.service.BlacklistService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlacklistServiceImpl implements BlacklistService {
    Logger log = LoggerFactory.getLogger(BlacklistServiceImpl.class);

    private final BlacklistedCardRepository cardRepository;
    private final BlackListedIpRepository ipRepository;
//    private final BlackListedMerchantRepository merchantRepository;
    private final BlackListedMerchantDao blackListedMerchantDao;

    @Override
    public List<BlacklistedCard> getAllCards() {
        return cardRepository.findAll();
    }

    @Override
    public BlacklistedCard getCard(String cardNo) {
        log.info("Getting blacklisted card by cardNo {}", cardNo);
        return cardRepository.findByCardNo(cardNo)
                .orElseThrow(() -> new RuntimeException("Card not found"));
    }

    @Override
    public List<BlacklistedIp> getAllIps() {
        return ipRepository.findAll();
    }

    @Override
    public BlacklistedIp getIp(String ip) {
        return ipRepository.findById(ip)
                .orElseThrow(() -> new RuntimeException("IP not found"));
    }

    @Override
    public List<BlacklistedMerchant> getAllMerchants() {
        return blackListedMerchantDao.findAll();
    }

    @Override
    public BlacklistResponse getAllBlacklist() {

        return BlacklistResponse.builder()
                .cards(cardRepository.findAll())
                .ips(ipRepository.findAll())
                .merchants(blackListedMerchantDao.findAll())
                .build();
    }

    @Override
    public BlacklistedMerchant getMerchant(String merchantId) {
        return blackListedMerchantDao.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));
    }
}
