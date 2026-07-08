package com.payguard.core.engine;

import com.payguard.core.model.Channel;
import com.payguard.core.model.DecisionType;
import com.payguard.core.model.RiskLevel;
import com.payguard.core.model.Transaction;
import com.payguard.core.model.TransactionDecision;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentRuleEngineFullEvaluationTest {

    @Test
    void shouldEvaluateAllRulesEvenWhenSomeRulesFail() {
        PaymentRuleEngine engine = defaultEngine();

        Transaction transaction = transaction(
                new BigDecimal("12500.00"),
                "GBP",
                "RU",
                Channel.ATM,
                RiskLevel.HIGH
        );

        List<RuleResult> results = engine.evaluateAll(transaction);

        assertEquals(5, results.size());

        assertEquals(DecisionType.REJECTED, results.get(0).getDecisionType());
        assertEquals("CurrencyAllowedRule", results.get(0).getRuleName());

        assertEquals(DecisionType.REJECTED, results.get(1).getDecisionType());
        assertEquals("CountryBlockedRule", results.get(1).getRuleName());

        assertEquals(DecisionType.REJECTED, results.get(2).getDecisionType());
        assertEquals("ChannelAllowedRule", results.get(2).getRuleName());

        assertEquals(DecisionType.REVIEW, results.get(3).getDecisionType());
        assertEquals("AmountLimitRule", results.get(3).getRuleName());

        assertEquals(DecisionType.REVIEW, results.get(4).getDecisionType());
        assertEquals("RiskLevelRule", results.get(4).getRuleName());
    }

    @Test
    void shouldReturnRejectedDecisionWhenFullEvaluationHasRejectedAndReviewResults() {
        PaymentRuleEngine engine = defaultEngine();

        Transaction transaction = transaction(
                new BigDecimal("12500.00"),
                "EUR",
                "RU",
                Channel.ONLINE,
                RiskLevel.HIGH
        );

        TransactionDecision decision = engine.decideWithFullEvaluation(transaction);

        assertTrue(decision.isRejected());
        assertEquals(DecisionType.REJECTED, decision.getDecisionType());
        assertEquals(3, decision.getReasons().size());

        assertTrue(decision.getReasons().contains(
                "CountryBlockedRule: Transaction country is blocked"
        ));
        assertTrue(decision.getReasons().contains(
                "AmountLimitRule: Transaction amount exceeds review limit"
        ));
        assertTrue(decision.getReasons().contains(
                "RiskLevelRule: High risk customer requires manual review"
        ));
    }

    @Test
    void shouldReturnReviewDecisionWhenFullEvaluationHasOnlyReviewResults() {
        PaymentRuleEngine engine = defaultEngine();

        Transaction transaction = transaction(
                new BigDecimal("12500.00"),
                "EUR",
                "RS",
                Channel.ONLINE,
                RiskLevel.LOW
        );

        TransactionDecision decision = engine.decideWithFullEvaluation(transaction);

        assertTrue(decision.requiresReview());
        assertEquals(DecisionType.REVIEW, decision.getDecisionType());
        assertEquals(List.of(
                "AmountLimitRule: Transaction amount exceeds review limit"
        ), decision.getReasons());
    }

    @Test
    void shouldReturnApprovedDecisionWhenAllRulesPassInFullEvaluation() {
        PaymentRuleEngine engine = defaultEngine();

        Transaction transaction = transaction(
                new BigDecimal("5000.00"),
                "EUR",
                "RS",
                Channel.ONLINE,
                RiskLevel.LOW
        );

        TransactionDecision decision = engine.decideWithFullEvaluation(transaction);

        assertTrue(decision.isApproved());
        assertEquals(DecisionType.APPROVED, decision.getDecisionType());
        assertEquals(List.of("All payment rules passed"), decision.getReasons());
    }

    @Test
    void shouldThrowExceptionWhenEvaluateAllTransactionIsNull() {
        PaymentRuleEngine engine = defaultEngine();

        assertThrows(IllegalArgumentException.class, () -> {
            engine.evaluateAll(null);
        });
    }

    @Test
    void shouldThrowExceptionWhenFullEvaluationTransactionIsNull() {
        PaymentRuleEngine engine = defaultEngine();

        assertThrows(IllegalArgumentException.class, () -> {
            engine.decideWithFullEvaluation(null);
        });
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