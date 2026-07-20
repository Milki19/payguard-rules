package com.payguard.demo.ruleconfig.engine;

import com.payguard.core.engine.PaymentRuleEngine;
import com.payguard.core.rule.PaymentRule;
import com.payguard.demo.ruleconfig.RuleDefinitionEntity;
import com.payguard.demo.ruleconfig.RuleDefinitionRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseRuleEngineFactory {

    private final RuleDefinitionRepository ruleDefinitionRepository;

    public DatabaseRuleEngineFactory(RuleDefinitionRepository ruleDefinitionRepository) {
        this.ruleDefinitionRepository = ruleDefinitionRepository;
    }

    public PaymentRuleEngine createEngineFromActiveRules() {
        List<PaymentRule> rules = ruleDefinitionRepository.findByActiveTrueOrderByPriorityAscCreatedAtDesc()
                .stream()
                .map(this::toPaymentRule)
                .toList();

        if (rules.isEmpty()) {
            throw new IllegalStateException("No active rule definitions found");
        }

        return new PaymentRuleEngine(rules);
    }

    private PaymentRule toPaymentRule(RuleDefinitionEntity entity) {
        return new DynamicPaymentRule(
                entity.getName(),
                entity.getFieldName(),
                entity.getOperator(),
                entity.getRuleValue(),
                entity.getDecisionType(),
                entity.getMessage()
        );
    }
}