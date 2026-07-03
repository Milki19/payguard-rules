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

class AmountLimitRuleTest {

    @Test
    void shouldApproveTransactionWithinLimit() {
        AmountLimitRule rule = new AmountLimitRule(new BigDecimal("10000.00"));

        Transaction transaction = new Transaction(
                "TX-1001",
                new BigDecimal("5000.00"),
                "EUR",
                "RS",
                Channel.ONLINE,
                RiskLevel.LOW
        );

        RuleResult result = rule.evaluate(transaction);

        assertTrue(result.isPassed());
        assertEquals(DecisionType.APPROVED, result.getDecisionType());
        assertEquals("Transaction amount is within allowed limit", result.getMessage());
    }

    @Test
    void shouldReviewTransactionAboveLimit() {
        AmountLimitRule rule = new AmountLimitRule(new BigDecimal("10000.00"));

        Transaction transaction = new Transaction(
                "TX-1002",
                new BigDecimal("12500.00"),
                "EUR",
                "RS",
                Channel.POS,
                RiskLevel.MEDIUM
        );

        RuleResult result = rule.evaluate(transaction);

        assertFalse(result.isPassed());
        assertEquals(DecisionType.REVIEW, result.getDecisionType());
        assertEquals("Transaction amount exceeds review limit", result.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLimitIsZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AmountLimitRule(BigDecimal.ZERO);
        });
    }

    @Test
    void shouldThrowExceptionWhenTransactionIsNull() {
        AmountLimitRule rule = new AmountLimitRule(new BigDecimal("10000.00"));

        assertThrows(IllegalArgumentException.class, () -> {
            rule.evaluate(null);
        });
    }
}
