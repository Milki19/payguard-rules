package com.payguard.core.rule.impl;

import com.payguard.core.model.DecisionType;
import com.payguard.core.model.Transaction;
import com.payguard.core.rule.PaymentRule;
import com.payguard.core.rule.RuleResult;

import java.math.BigDecimal;

public class AmountLimitRule implements PaymentRule {

    private final BigDecimal reviewLimit;

    public AmountLimitRule(BigDecimal reviewLimit) {

        if (reviewLimit == null || reviewLimit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Review limit must be greater than zero");
        }

        this.reviewLimit = reviewLimit;
    }

    @Override
    public RuleResult evaluate(Transaction transaction) {

        RuleResult ruleResult;

        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        if (transaction.getAmount().compareTo(reviewLimit) > 0) {
            return new RuleResult(false, DecisionType.REVIEW, "Transaction amount exceeds review limit");
        }else {
            return new RuleResult(true, DecisionType.APPROVED, "Transaction amount is within allowed limit");
        }
    }


}
