package com.payguard.demo.ruleconfig.engine;

import com.payguard.core.engine.PaymentRuleEngine;
import com.payguard.core.model.Channel;
import com.payguard.core.model.DecisionType;
import com.payguard.core.model.RiskLevel;
import com.payguard.core.model.Transaction;
import com.payguard.core.rule.RuleResult;
import com.payguard.demo.ruleconfig.RuleDefinitionEntity;
import com.payguard.demo.ruleconfig.RuleDefinitionRepository;
import com.payguard.demo.ruleconfig.RuleOperator;
import com.payguard.demo.ruleconfig.RuleType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DatabaseRuleEngineFactoryTest {

    @Test
    void shouldCreateEngineFromActiveRules() {
        RuleDefinitionRepository repository = mock(RuleDefinitionRepository.class);

        RuleDefinitionEntity rule = rule(
                "High amount review",
                RuleType.AMOUNT_LIMIT,
                RuleOperator.GREATER_THAN,
                "amount",
                "10000",
                DecisionType.REVIEW,
                "Transaction amount exceeds review limit",
                true,
                30
        );

        when(repository.findByActiveTrueOrderByPriorityAscCreatedAtDesc())
                .thenReturn(List.of(rule));

        DatabaseRuleEngineFactory factory = new DatabaseRuleEngineFactory(repository);

        PaymentRuleEngine engine = factory.createEngineFromActiveRules();

        Transaction transaction = transaction(new BigDecimal("12500"), "EUR", "RS");

        RuleResult result = engine.evaluate(transaction);

        assertFalse(result.isPassed());
        assertEquals(DecisionType.REVIEW, result.getDecisionType());
        assertEquals("High amount review", result.getRuleName());
        assertEquals("Transaction amount exceeds review limit", result.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNoActiveRulesExist() {
        RuleDefinitionRepository repository = mock(RuleDefinitionRepository.class);

        when(repository.findByActiveTrueOrderByPriorityAscCreatedAtDesc())
                .thenReturn(List.of());

        DatabaseRuleEngineFactory factory = new DatabaseRuleEngineFactory(repository);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                factory::createEngineFromActiveRules
        );

        assertEquals("No active rule definitions found", exception.getMessage());
    }

    @Test
    void shouldEvaluateRulesInRepositoryOrder() {
        RuleDefinitionRepository repository = mock(RuleDefinitionRepository.class);

        RuleDefinitionEntity blockedCountryRule = rule(
                "Blocked countries",
                RuleType.COUNTRY_BLOCKED,
                RuleOperator.IN,
                "country",
                "RU,KP,IR",
                DecisionType.REJECTED,
                "Transaction country is blocked",
                true,
                10
        );

        RuleDefinitionEntity highAmountRule = rule(
                "High amount review",
                RuleType.AMOUNT_LIMIT,
                RuleOperator.GREATER_THAN,
                "amount",
                "10000",
                DecisionType.REVIEW,
                "Transaction amount exceeds review limit",
                true,
                30
        );

        when(repository.findByActiveTrueOrderByPriorityAscCreatedAtDesc())
                .thenReturn(List.of(blockedCountryRule, highAmountRule));

        DatabaseRuleEngineFactory factory = new DatabaseRuleEngineFactory(repository);

        PaymentRuleEngine engine = factory.createEngineFromActiveRules();

        Transaction transaction = transaction(new BigDecimal("12500"), "EUR", "RU");

        RuleResult result = engine.evaluate(transaction);

        assertFalse(result.isPassed());
        assertEquals(DecisionType.REJECTED, result.getDecisionType());
        assertEquals("Blocked countries", result.getRuleName());
        assertEquals("Transaction country is blocked", result.getMessage());
    }

    private RuleDefinitionEntity rule(
            String name,
            RuleType ruleType,
            RuleOperator operator,
            String fieldName,
            String ruleValue,
            DecisionType decisionType,
            String message,
            boolean active,
            int priority
    ) {
        LocalDateTime now = LocalDateTime.now();

        return new RuleDefinitionEntity(
                name,
                ruleType,
                operator,
                fieldName,
                ruleValue,
                decisionType,
                message,
                active,
                priority,
                now,
                now
        );
    }

    private Transaction transaction(BigDecimal amount, String currency, String country) {
        return new Transaction(
                "TX-FACTORY-TEST",
                amount,
                currency,
                country,
                Channel.ONLINE,
                RiskLevel.LOW
        );
    }
}