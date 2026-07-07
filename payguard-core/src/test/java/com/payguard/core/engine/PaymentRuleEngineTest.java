package com.payguard.core.engine;

import com.payguard.core.model.Channel;
import com.payguard.core.model.DecisionType;
import com.payguard.core.model.RiskLevel;
import com.payguard.core.model.Transaction;
import com.payguard.core.rule.PaymentRule;
import com.payguard.core.rule.RuleResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PaymentRuleEngineTest {

    @Test
    void shouldApproveWhenAllRulesPass() {
        PaymentRule firstRule = transaction ->
                new RuleResult(true, DecisionType.APPROVED, "FirstTestRule","First rule passed");

        PaymentRule secondRule = transaction ->
                new RuleResult(true, DecisionType.APPROVED, "SecondTestRule","Second rule passed");

        PaymentRuleEngine engine = new PaymentRuleEngine(List.of(firstRule, secondRule));

        RuleResult result = engine.evaluate(validTransaction());

        assertTrue(result.isPassed());
        assertEquals(DecisionType.APPROVED, result.getDecisionType());
        assertEquals("Payment rules have been evaluated successfully", result.getMessage());
    }

    @Test
    void shouldReturnFirstFailedRuleResult() {
        PaymentRule firstRule = transaction ->
                new RuleResult(true, DecisionType.APPROVED, "FirstTestRule","First rule passed");

        PaymentRule secondRule = transaction ->
                new RuleResult(false, DecisionType.REVIEW, "SecondTestRule","Second rule failed");

        PaymentRule thirdRule = transaction ->
                new RuleResult(false, DecisionType.REJECTED, "ThirdTestRule","Third rule failed");

        PaymentRuleEngine engine = new PaymentRuleEngine(List.of(firstRule, secondRule, thirdRule));

        RuleResult result = engine.evaluate(validTransaction());

        assertFalse(result.isPassed());
        assertEquals(DecisionType.REVIEW, result.getDecisionType());
        assertEquals("Second rule failed", result.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRulesListIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PaymentRuleEngine(null);
        });
    }

    @Test
    void shouldThrowExceptionWhenRulesListIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PaymentRuleEngine(List.of());
        });
    }

    @Test
    void shouldThrowExceptionWhenTransactionIsNull() {
        PaymentRule rule = transaction ->
                new RuleResult(true, DecisionType.APPROVED, "RuleEngine","Rule passed");

        PaymentRuleEngine engine = new PaymentRuleEngine(List.of(rule));

        assertThrows(IllegalArgumentException.class, () -> {
            engine.evaluate(null);
        });
    }

    private Transaction validTransaction() {
        return new Transaction(
                "TX-1001",
                new BigDecimal("5000.00"),
                "EUR",
                "RS",
                Channel.ONLINE,
                RiskLevel.LOW
        );
    }
}
