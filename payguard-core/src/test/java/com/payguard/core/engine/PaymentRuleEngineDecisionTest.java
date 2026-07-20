package com.payguard.core.engine;

import com.payguard.core.model.Channel;
import com.payguard.core.model.DecisionType;
import com.payguard.core.model.RiskLevel;
import com.payguard.core.model.Transaction;
import com.payguard.core.model.TransactionDecision;
import com.payguard.core.rule.impl.AmountLimitRule;
import com.payguard.core.rule.impl.CountryBlockedRule;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentRuleEngineDecisionTest {

    @Test
    void shouldReturnApprovedDecisionWhenAllRulesPass() {
        PaymentRuleEngine engine = new PaymentRuleEngine(List.of(
                new AmountLimitRule(new BigDecimal("10000.00"))
        ));

        Transaction transaction = validTransaction(
                new BigDecimal("5000.00"),
                "EUR",
                "RS",
                RiskLevel.LOW
        );

        TransactionDecision decision = engine.decide(transaction);

        assertTrue(decision.isApproved());
        assertEquals(DecisionType.APPROVED, decision.getDecisionType());
        assertEquals(List.of("PaymentRuleEngine: Payment rules have been evaluated successfully"), decision.getReasons());
    }

    @Test
    void shouldReturnRejectedDecisionWhenRuleFails() {
        PaymentRuleEngine engine = new PaymentRuleEngine(List.of(
                new CountryBlockedRule(Set.of("RU", "KP", "IR"))
        ));

        Transaction transaction = validTransaction(
                new BigDecimal("5000.00"),
                "EUR",
                "RU",
                RiskLevel.LOW
        );

        TransactionDecision decision = engine.decide(transaction);

        assertTrue(decision.isRejected());
        assertEquals(DecisionType.REJECTED, decision.getDecisionType());
        assertEquals(List.of("CountryBlockedRule: Transaction country is blocked"), decision.getReasons());
    }

    @Test
    void shouldReturnReviewDecisionWhenRuleRequiresReview() {
        PaymentRuleEngine engine = new PaymentRuleEngine(List.of(
                new AmountLimitRule(new BigDecimal("10000.00"))
        ));

        Transaction transaction = validTransaction(
                new BigDecimal("12500.00"),
                "EUR",
                "RS",
                RiskLevel.LOW
        );

        TransactionDecision decision = engine.decide(transaction);

        assertTrue(decision.requiresReview());
        assertEquals(DecisionType.REVIEW, decision.getDecisionType());
        assertEquals(List.of("AmountLimitRule: Transaction amount exceeds review limit"), decision.getReasons());
    }

    private Transaction validTransaction(
            BigDecimal amount,
            String currency,
            String country,
            RiskLevel riskLevel
    ) {
        return new Transaction(
                "TX-1001",
                amount,
                currency,
                country,
                Channel.ONLINE,
                riskLevel
        );
    }
}