package com.interswitch.fraudtransactionapp.fraudEngine.rule;

/**
 * Marker interface for rules that must run synchronously before parallel scoring rules.
 * Blocking rules can stop processing immediately (e.g., blacklist checks).
 */
public interface BlockingRule extends FraudRule {
}
