package com.payguard.demo.ruleconfig.dto;

import com.payguard.core.model.DecisionType;
import com.payguard.demo.ruleconfig.RuleOperator;
import com.payguard.demo.ruleconfig.RuleType;

import java.time.LocalDateTime;

public class RuleDefinitionResponse {

    private final Long id;
    private final String name;
    private final RuleType ruleType;
    private final RuleOperator operator;
    private final String fieldName;
    private final String ruleValue;
    private final DecisionType decisionType;
    private final String message;
    private final boolean active;
    private final int priority;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public RuleDefinitionResponse(
            Long id,
            String name,
            RuleType ruleType,
            RuleOperator operator,
            String fieldName,
            String ruleValue,
            DecisionType decisionType,
            String message,
            boolean active,
            int priority,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.ruleType = ruleType;
        this.operator = operator;
        this.fieldName = fieldName;
        this.ruleValue = ruleValue;
        this.decisionType = decisionType;
        this.message = message;
        this.active = active;
        this.priority = priority;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RuleType getRuleType() {
        return ruleType;
    }

    public RuleOperator getOperator() {
        return operator;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getRuleValue() {
        return ruleValue;
    }

    public DecisionType getDecisionType() {
        return decisionType;
    }

    public String getMessage() {
        return message;
    }

    public boolean isActive() {
        return active;
    }

    public int getPriority() {
        return priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}