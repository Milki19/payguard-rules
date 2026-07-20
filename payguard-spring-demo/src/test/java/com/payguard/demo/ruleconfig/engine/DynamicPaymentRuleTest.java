package com.payguard.demo.ruleconfig.engine;

import com.payguard.core.model.Channel;
import com.payguard.core.model.DecisionType;
import com.payguard.core.model.RiskLevel;
import com.payguard.core.model.Transaction;
import com.payguard.core.rule.RuleResult;
import com.payguard.demo.ruleconfig.RuleOperator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DynamicPaymentRuleTest {

    @Test
    void shouldReturnReviewWhenAmountIsGreaterThanLimit() {
        DynamicPaymentRule rule = new DynamicPaymentRule(
                "High amount review",
                "amount",
                RuleOperator.GREATER_THAN,
                "10000",
                DecisionType.REVIEW,
                "Transaction amount exceeds review limit"
        );

        Transaction transaction = transaction(
                new BigDecimal("12500"),
                "EUR",
                "RS",
                Channel.ONLINE,
                RiskLevel.LOW
        );

        RuleResult result = rule.evaluate(transaction);

        assertFalse(result.isPassed());
        assertEquals(DecisionType.REVIEW, result.getDecisionType());
        assertEquals("High amount review", result.getRuleName());
        assertEquals("Transaction amount exceeds review limit", result.getMessage());
    }

    @Test
    void shouldReturnApprovedWhenAmountIsNotGreaterThanLimit() {
        DynamicPaymentRule rule = new DynamicPaymentRule(
                "High amount review",
                "amount",
                RuleOperator.GREATER_THAN,
                "10000",
                DecisionType.REVIEW,
                "Transaction amount exceeds review limit"
        );

        Transaction transaction = transaction(
                new BigDecimal("5000"),
                "EUR",
                "RS",
                Channel.ONLINE,
                RiskLevel.LOW
        );

        RuleResult result = rule.evaluate(transaction);

        assertTrue(result.isPassed());
        assertEquals(DecisionType.APPROVED, result.getDecisionType());
        assertEquals("High amount review", result.getRuleName());
        assertEquals("Rule condition not matched", result.getMessage());
    }

    @Test
    void shouldReturnRejectedWhenCountryIsInBlockedList() {
        DynamicPaymentRule rule = new DynamicPaymentRule(
                "Blocked countries",
                "country",
                RuleOperator.IN,
                "RU,KP,IR",
                DecisionType.REJECTED,
                "Transaction country is blocked"
        );

        Transaction transaction = transaction(
                new BigDecimal("5000"),
                "EUR",
                "RU",
                Channel.ONLINE,
                RiskLevel.LOW
        );

        RuleResult result = rule.evaluate(transaction);

        assertFalse(result.isPassed());
        assertEquals(DecisionType.REJECTED, result.getDecisionType());
        assertEquals("Blocked countries", result.getRuleName());
        assertEquals("Transaction country is blocked", result.getMessage());
    }

    @Test
    void shouldReturnRejectedWhenCurrencyIsNotInAllowedList() {
        DynamicPaymentRule rule = new DynamicPaymentRule(
                "Unsupported currency",
                "currency",
                RuleOperator.NOT_IN,
                "EUR,USD,RSD",
                DecisionType.REJECTED,
                "Transaction currency is not allowed"
        );

        Transaction transaction = transaction(
                new BigDecimal("5000"),
                "GBP",
                "RS",
                Channel.ONLINE,
                RiskLevel.LOW
        );

        RuleResult result = rule.evaluate(transaction);

        assertFalse(result.isPassed());
        assertEquals(DecisionType.REJECTED, result.getDecisionType());
        assertEquals("Unsupported currency", result.getRuleName());
        assertEquals("Transaction currency is not allowed", result.getMessage());
    }

    @Test
    void shouldReturnApprovedWhenCurrencyIsInAllowedList() {
        DynamicPaymentRule rule = new DynamicPaymentRule(
                "Unsupported currency",
                "currency",
                RuleOperator.NOT_IN,
                "EUR,USD,RSD",
                DecisionType.REJECTED,
                "Transaction currency is not allowed"
        );

        Transaction transaction = transaction(
                new BigDecimal("5000"),
                "EUR",
                "RS",
                Channel.ONLINE,
                RiskLevel.LOW
        );

        RuleResult result = rule.evaluate(transaction);

        assertTrue(result.isPassed());
        assertEquals(DecisionType.APPROVED, result.getDecisionType());
        assertEquals("Unsupported currency", result.getRuleName());
        assertEquals("Rule condition not matched", result.getMessage());
    }

    private Transaction transaction(
            BigDecimal amount,
            String currency,
            String country,
            Channel channel,
            RiskLevel riskLevel
    ) {
        return new Transaction(
                "TX-DYNAMIC-TEST",
                amount,
                currency,
                country,
                channel,
                riskLevel
        );
    }
}