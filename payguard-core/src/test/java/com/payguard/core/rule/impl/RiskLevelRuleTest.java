package com.payguard.core.rule.impl;

import com.payguard.core.model.Channel;
import com.payguard.core.model.DecisionType;
import com.payguard.core.model.RiskLevel;
import com.payguard.core.model.Transaction;
import com.payguard.core.rule.RuleResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RiskLevelRuleTest {

    @Test
    void shouldReviewHighRiskCustomer() {
        RiskLevelRule rule = new RiskLevelRule(new BigDecimal("10000.00"));

        Transaction transaction = validTransaction(
                new BigDecimal("100.00"),
                RiskLevel.HIGH
        );

        RuleResult result = rule.evaluate(transaction);

        assertFalse(result.isPassed());
        assertEquals(DecisionType.REVIEW, result.getDecisionType());
        assertEquals("High risk customer requires manual review", result.getMessage());
    }

    @Test
    void shouldReviewMediumRiskCustomerWhenAmountExceedsLimit() {
        RiskLevelRule rule = new RiskLevelRule(new BigDecimal("10000.00"));

        Transaction transaction = validTransaction(
                new BigDecimal("12500.00"),
                RiskLevel.MEDIUM
        );

        RuleResult result = rule.evaluate(transaction);

        assertFalse(result.isPassed());
        assertEquals(DecisionType.REVIEW, result.getDecisionType());
        assertEquals("Medium risk customer amount exceeds review limit", result.getMessage());
    }

    @Test
    void shouldApproveMediumRiskCustomerWhenAmountIsWithinLimit() {
        RiskLevelRule rule = new RiskLevelRule(new BigDecimal("10000.00"));

        Transaction transaction = validTransaction(
                new BigDecimal("5000.00"),
                RiskLevel.MEDIUM
        );

        RuleResult result = rule.evaluate(transaction);

        assertTrue(result.isPassed());
        assertEquals(DecisionType.APPROVED, result.getDecisionType());
        assertEquals("Customer risk level is acceptable", result.getMessage());
    }

    @Test
    void shouldApproveLowRiskCustomer() {
        RiskLevelRule rule = new RiskLevelRule(new BigDecimal("10000.00"));

        Transaction transaction = validTransaction(
                new BigDecimal("50000.00"),
                RiskLevel.LOW
        );

        RuleResult result = rule.evaluate(transaction);

        assertTrue(result.isPassed());
        assertEquals(DecisionType.APPROVED, result.getDecisionType());
        assertEquals("Customer risk level is acceptable", result.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenMediumRiskReviewLimitIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new RiskLevelRule(null);
        });
    }

    @Test
    void shouldThrowExceptionWhenMediumRiskReviewLimitIsZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            new RiskLevelRule(BigDecimal.ZERO);
        });
    }

    @Test
    void shouldThrowExceptionWhenTransactionIsNull() {
        RiskLevelRule rule = new RiskLevelRule(new BigDecimal("10000.00"));

        assertThrows(IllegalArgumentException.class, () -> {
            rule.evaluate(null);
        });
    }

    private Transaction validTransaction(BigDecimal amount, RiskLevel riskLevel) {
        return new Transaction(
                "TX-1001",
                amount,
                "EUR",
                "RS",
                Channel.ONLINE,
                riskLevel
        );
    }
}