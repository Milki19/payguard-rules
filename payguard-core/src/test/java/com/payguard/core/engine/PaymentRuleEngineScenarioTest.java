package com.payguard.core.engine;

import com.payguard.core.model.Channel;
import com.payguard.core.model.DecisionType;
import com.payguard.core.model.RiskLevel;
import com.payguard.core.model.Transaction;
import com.payguard.core.rule.RuleResult;
import com.payguard.core.rule.impl.AmountLimitRule;
import com.payguard.core.rule.impl.ChannelAllowedRule;
import com.payguard.core.rule.impl.CountryBlockedRule;
import com.payguard.core.rule.impl.CurrencyAllowedRule;
import com.payguard.core.rule.impl.RiskLevelRule;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentRuleEngineScenarioTest {

    @Test
    void shouldApproveTransactionWhenAllRealRulesPass() {
        PaymentRuleEngine engine = defaultEngine();

        Transaction transaction = transaction(
                new BigDecimal("5000.00"),
                "EUR",
                "RS",
                Channel.ONLINE,
                RiskLevel.LOW
        );

        RuleResult result = engine.evaluate(transaction);

        assertTrue(result.isPassed());
        assertEquals(DecisionType.APPROVED, result.getDecisionType());
        assertEquals("PaymentRuleEngine", result.getRuleName());
        assertEquals("Payment rules have been evaluated successfully", result.getMessage());
    }

    @Test
    void shouldRejectTransactionWhenCountryIsBlocked() {
        PaymentRuleEngine engine = defaultEngine();

        Transaction transaction = transaction(
                new BigDecimal("5000.00"),
                "EUR",
                "RU",
                Channel.ONLINE,
                RiskLevel.LOW
        );

        RuleResult result = engine.evaluate(transaction);

        assertFalse(result.isPassed());
        assertEquals(DecisionType.REJECTED, result.getDecisionType());
        assertEquals("CountryBlockedRule", result.getRuleName());
        assertEquals("Transaction country is blocked", result.getMessage());
    }

    @Test
    void shouldRejectTransactionWhenCurrencyIsNotAllowed() {
        PaymentRuleEngine engine = defaultEngine();

        Transaction transaction = transaction(
                new BigDecimal("5000.00"),
                "GBP",
                "RS",
                Channel.ONLINE,
                RiskLevel.LOW
        );

        RuleResult result = engine.evaluate(transaction);

        assertFalse(result.isPassed());
        assertEquals(DecisionType.REJECTED, result.getDecisionType());
        assertEquals("CurrencyAllowedRule", result.getRuleName());
        assertEquals("Transaction currency is not allowed", result.getMessage());
    }

    @Test
    void shouldRejectTransactionWhenChannelIsNotAllowed() {
        PaymentRuleEngine engine = defaultEngine();

        Transaction transaction = transaction(
                new BigDecimal("5000.00"),
                "EUR",
                "RS",
                Channel.ATM,
                RiskLevel.LOW
        );

        RuleResult result = engine.evaluate(transaction);

        assertFalse(result.isPassed());
        assertEquals(DecisionType.REJECTED, result.getDecisionType());
        assertEquals("ChannelAllowedRule", result.getRuleName());
        assertEquals("Transaction channel is not allowed", result.getMessage());
    }

    @Test
    void shouldReviewTransactionWhenAmountExceedsLimit() {
        PaymentRuleEngine engine = defaultEngine();

        Transaction transaction = transaction(
                new BigDecimal("12500.00"),
                "EUR",
                "RS",
                Channel.ONLINE,
                RiskLevel.LOW
        );

        RuleResult result = engine.evaluate(transaction);

        assertFalse(result.isPassed());
        assertEquals(DecisionType.REVIEW, result.getDecisionType());
        assertEquals("AmountLimitRule", result.getRuleName());
        assertEquals("Transaction amount exceeds review limit", result.getMessage());
    }

    @Test
    void shouldReviewTransactionWhenCustomerRiskIsHigh() {
        PaymentRuleEngine engine = defaultEngine();

        Transaction transaction = transaction(
                new BigDecimal("5000.00"),
                "EUR",
                "RS",
                Channel.ONLINE,
                RiskLevel.HIGH
        );

        RuleResult result = engine.evaluate(transaction);

        assertFalse(result.isPassed());
        assertEquals(DecisionType.REVIEW, result.getDecisionType());
        assertEquals("RiskLevelRule", result.getRuleName());
        assertEquals("High risk customer requires manual review", result.getMessage());
    }

    private PaymentRuleEngine defaultEngine() {
        return new PaymentRuleEngine(List.of(
                new CurrencyAllowedRule(Set.of("EUR", "USD", "RSD")),
                new CountryBlockedRule(Set.of("RU", "KP", "IR")),
                new ChannelAllowedRule(Set.of(Channel.ONLINE, Channel.POS)),
                new AmountLimitRule(new BigDecimal("10000.00")),
                new RiskLevelRule(new BigDecimal("7500.00"))
        ));
    }

    private Transaction transaction(
            BigDecimal amount,
            String currency,
            String country,
            Channel channel,
            RiskLevel riskLevel
    ) {
        return new Transaction(
                "TX-1001",
                amount,
                currency,
                country,
                channel,
                riskLevel
        );
    }
}