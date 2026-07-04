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

class CountryBlockedRuleTest {

    @Test
    void shouldApproveTransactionFromAllowedCountry() {
        CountryBlockedRule rule = new CountryBlockedRule(Set.of("RU", "KP", "IR"));

        Transaction transaction = validTransaction("RS");

        RuleResult result = rule.evaluate(transaction);

        assertTrue(result.isPassed());
        assertEquals(DecisionType.APPROVED, result.getDecisionType());
        assertEquals("Transaction country is allowed", result.getMessage());
    }

    @Test
    void shouldRejectTransactionFromBlockedCountry() {
        CountryBlockedRule rule = new CountryBlockedRule(Set.of("RU", "KP", "IR"));

        Transaction transaction = validTransaction("RU");

        RuleResult result = rule.evaluate(transaction);

        assertFalse(result.isPassed());
        assertEquals(DecisionType.REJECTED, result.getDecisionType());
        assertEquals("Transaction country is blocked", result.getMessage());
    }

    @Test
    void shouldRejectTransactionFromBlockedCountryIgnoringCaseAndSpaces() {
        CountryBlockedRule rule = new CountryBlockedRule(Set.of("ru", "kp", "ir"));

        Transaction transaction = validTransaction(" Ru ");

        RuleResult result = rule.evaluate(transaction);

        assertFalse(result.isPassed());
        assertEquals(DecisionType.REJECTED, result.getDecisionType());
        assertEquals("Transaction country is blocked", result.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenBlockedCountriesIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new CountryBlockedRule(null);
        });
    }

    @Test
    void shouldThrowExceptionWhenBlockedCountriesIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            new CountryBlockedRule(Set.of());
        });
    }

    @Test
    void shouldThrowExceptionWhenBlockedCountriesContainsBlank() {
        Set<String> blockedCountries = new HashSet<>();
        blockedCountries.add("RU");
        blockedCountries.add("   ");

        assertThrows(IllegalArgumentException.class, () -> {
            new CountryBlockedRule(blockedCountries);
        });
    }

    @Test
    void shouldThrowExceptionWhenTransactionIsNull() {
        CountryBlockedRule rule = new CountryBlockedRule(Set.of("RU", "KP", "IR"));

        assertThrows(IllegalArgumentException.class, () -> {
            rule.evaluate(null);
        });
    }

    private Transaction validTransaction(String country) {
        return new Transaction(
                "TX-1001",
                new BigDecimal("5000.00"),
                "EUR",
                country,
                Channel.ONLINE,
                RiskLevel.LOW
        );
    }
}
