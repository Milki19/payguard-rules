package com.payguard.core.rule.impl;

import com.payguard.core.model.DecisionType;
import com.payguard.core.model.RiskLevel;
import com.payguard.core.model.Transaction;
import com.payguard.core.rule.PaymentRule;
import com.payguard.core.rule.RuleResult;

import java.math.BigDecimal;

public class RiskLevelRule implements PaymentRule {

    private final BigDecimal mediumRiskReviewLimit;

    public RiskLevelRule(BigDecimal mediumRiskReviewLimit) {

        if (mediumRiskReviewLimit == null) {
            throw new IllegalArgumentException("Medium risk review limit must be greater than zero");
        }

        if (mediumRiskReviewLimit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        this.mediumRiskReviewLimit = mediumRiskReviewLimit;
    }

    @Override
    public RuleResult evaluate(Transaction transaction) {

        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        RiskLevel riskLevel = transaction.getCustomerRiskLevel();
        switch (riskLevel) {
            case HIGH:
                return new RuleResult(false, DecisionType.REVIEW, "High risk customer requires manual review");

            case MEDIUM:
                if (transaction.getAmount().compareTo(mediumRiskReviewLimit) > 0) {
                    return new RuleResult(false, DecisionType.REVIEW, "Medium risk customer amount exceeds review limit");
                }
                return new RuleResult(true, DecisionType.APPROVED, "Customer risk level is acceptable");

            case LOW:
                return new RuleResult(true, DecisionType.APPROVED, "Customer risk level is acceptable");

            default:
                throw new IllegalArgumentException("Unsupported risk level");
        }
    }
}
