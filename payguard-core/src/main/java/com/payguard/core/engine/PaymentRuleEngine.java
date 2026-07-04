package com.payguard.core.engine;

import com.payguard.core.model.DecisionType;
import com.payguard.core.model.Transaction;
import com.payguard.core.rule.PaymentRule;
import com.payguard.core.rule.RuleResult;

import java.util.List;

public class PaymentRuleEngine {

    private final List<PaymentRule> rules;

    public PaymentRuleEngine(List<PaymentRule> rules) {
        if (rules == null || rules.isEmpty()) {
            throw new IllegalArgumentException("Payment rules cannot be empty or null");
        }

        for (PaymentRule rule : rules) {
            if (rule == null) {
                throw new IllegalArgumentException("Payment rules cannot contain null values");
            }
        }

        this.rules = List.copyOf(rules);
    }

    public RuleResult evaluate(Transaction transaction) {

        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        for (PaymentRule rule : rules) {
            RuleResult result = rule.evaluate(transaction);

            if (!result.isPassed()) {
                return result;
            }
        }
        return new RuleResult(true, DecisionType.APPROVED, "Payment rules have been evaluated successfully");
    }


    public List<PaymentRule> getRules() {
        return rules;
    }
}
