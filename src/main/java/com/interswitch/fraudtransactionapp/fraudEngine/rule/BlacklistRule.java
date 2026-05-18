package com.interswitch.fraudtransactionapp.fraudEngine.rule;

import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudRuleResult;
import com.interswitch.fraudtransactionapp.fraudEngine.model.RuleContext;
import com.interswitch.fraudtransactionapp.repository.BlacklistCache;
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

        String cardNo = context.request().getCardNo();
        String ip = context.request().getIpAddress();
        String merchantId = context.request().getMerchantId();

        if (blacklistCache.isCardBlacklisted(cardNo)) {
            return blocked("CARD_BLACKLISTED");
        }

        if (blacklistCache.isIpBlacklisted(ip)) {
            return blocked("IP_BLACKLISTED");
        }

        if (blacklistCache.isProviderIpBlacklisted(ip)) {
            return blocked("IP_BLACKLISTED");
        }

        if (blacklistCache.isMerchantBlacklisted(merchantId)) {
            return blocked("MERCHANT_BLACKLISTED");
        }

        return FraudRuleMapper.mapToResult(0, null, false);
    }

    private FraudRuleResult blocked(String reason) {
        return FraudRuleMapper.mapToResult(
                BLACKLIST_SCORE,
                reason,
                true
        );
    }
}