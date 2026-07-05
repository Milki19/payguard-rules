package com.payguard.core.rule.impl;

import com.payguard.core.model.Channel;
import com.payguard.core.model.DecisionType;
import com.payguard.core.model.RiskLevel;
import com.payguard.core.model.Transaction;
import com.payguard.core.rule.RuleResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrencyAllowedRuleTest {

    @Test
    void shouldApproveTransactionWithAllowedCurrency() {
        CurrencyAllowedRule rule = new CurrencyAllowedRule(Set.of("EUR", "USD", "RSD"));

        Transaction transaction = validTransaction("EUR");

        RuleResult result = rule.evaluate(transaction);

        assertTrue(result.isPassed());
        assertEquals(DecisionType.APPROVED, result.getDecisionType());
        assertEquals("Transaction currency is allowed", result.getMessage());
    }

    @Test
    void shouldRejectTransactionWithNotAllowedCurrency() {
        CurrencyAllowedRule rule = new CurrencyAllowedRule(Set.of("EUR", "USD", "RSD"));

        Transaction transaction = validTransaction("GBP");

        RuleResult result = rule.evaluate(transaction);

        assertFalse(result.isPassed());
        assertEquals(DecisionType.REJECTED, result.getDecisionType());
        assertEquals("Transaction currency is not allowed", result.getMessage());
    }

    @Test
    void shouldApproveCurrencyIgnoringCaseAndSpaces() {
        CurrencyAllowedRule rule = new CurrencyAllowedRule(Set.of("eur", "usd", "rsd"));

        Transaction transaction = validTransaction(" eur ");

        RuleResult result = rule.evaluate(transaction);

        assertTrue(result.isPassed());
        assertEquals(DecisionType.APPROVED, result.getDecisionType());
        assertEquals("Transaction currency is allowed", result.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAllowedCurrenciesIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new CurrencyAllowedRule(null);
        });
    }

    @Test
    void shouldThrowExceptionWhenAllowedCurrenciesIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            new CurrencyAllowedRule(Set.of());
        });
    }

    @Test
    void shouldThrowExceptionWhenAllowedCurrenciesContainsBlank() {
        Set<String> allowedCurrencies = new HashSet<>();
        allowedCurrencies.add("EUR");
        allowedCurrencies.add("   ");

        assertThrows(IllegalArgumentException.class, () -> {
            new CurrencyAllowedRule(allowedCurrencies);
        });
    }

    @Test
    void shouldThrowExceptionWhenAllowedCurrenciesContainsNull() {
        Set<String> allowedCurrencies = new HashSet<>();
        allowedCurrencies.add("EUR");
        allowedCurrencies.add(null);

        assertThrows(IllegalArgumentException.class, () -> {
            new CurrencyAllowedRule(allowedCurrencies);
        });
    }

    @Test
    void shouldThrowExceptionWhenTransactionIsNull() {
        CurrencyAllowedRule rule = new CurrencyAllowedRule(Set.of("EUR", "USD", "RSD"));

        assertThrows(IllegalArgumentException.class, () -> {
            rule.evaluate(null);
        });
    }

    private Transaction validTransaction(String currency) {
        return new Transaction(
                "TX-1001",
                new BigDecimal("5000.00"),
                currency,
                "RS",
                Channel.ONLINE,
                RiskLevel.LOW
        );
    }
}