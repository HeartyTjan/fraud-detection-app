package com.interswitch.fraudtransactionapp.fraudEngine.rule;

import com.interswitch.fraudtransactionapp.fraudEngine.model.RuleContext;
import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudRuleResult;

public interface FraudRule {
    FraudRuleResult evaluate(RuleContext context);
}
