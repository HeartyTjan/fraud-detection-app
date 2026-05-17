package com.interswitch.fraudtransactionapp.fraudEngine.rule;

import com.interswitch.fraudtransactionapp.fraudEngine.model.RuleContext;
import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudRuleResult;
import com.interswitch.fraudtransactionapp.repository.BlacklistCache;
import com.interswitch.fraudtransactionapp.repository.ProviderBlacklistedIPRepository;
import com.interswitch.fraudtransactionapp.util.mapper.FraudRuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(1)
public class BlacklistRule implements BlockingRule {

    private static final int BLACKLIST_SCORE = 100;

    private final BlacklistCache blacklistCache;

    @Override
    public FraudRuleResult evaluate(RuleContext context) {

        String cardNo = context.getRequest().getCardNo();
        String ip = context.getRequest().getIpAddress();
        String merchantId = context.getRequest().getMerchantId();

        if (cardNo != null && !cardNo.isEmpty() && blacklistCache.isCardBlacklisted(cardNo)) {
            return FraudRuleMapper.mapToResult(BLACKLIST_SCORE, "CARD_BLACKLISTED", true);
        }

        if (ip != null && !ip.isEmpty() && blacklistCache.isIpBlacklisted(ip)) {
            return FraudRuleMapper.mapToResult(BLACKLIST_SCORE, "IP_BLACKLISTED", true);
        }

        if (ip != null && !ip.isEmpty() && blacklistCache.isProviderIpBlacklisted(ip)) {
            return FraudRuleMapper.mapToResult(BLACKLIST_SCORE, "PROVIDER_IP_BLACKLISTED", true);
        }

        if (merchantId != null && !merchantId.isEmpty() && blacklistCache.isMerchantBlacklisted(merchantId)) {
            return FraudRuleMapper.mapToResult(BLACKLIST_SCORE, "MERCHANT_BLACKLISTED", true);
        }

        return FraudRuleMapper.mapToResult(0, null, false);
    }
}