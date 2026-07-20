package com.payguard.demo.ruleconfig;

import com.payguard.core.model.DecisionType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rule_definitions")
public class RuleDefinitionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "rule_type", nullable = false)
    private RuleType ruleType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleOperator operator;

    @Column(name = "field_name", nullable = false)
    private String fieldName;

    @Column(name = "rule_value", nullable = false, length = 1000)
    private String ruleValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision_type", nullable = false)
    private DecisionType decisionType;

    @Column(nullable = false, length = 1000)
    private String message;

    private boolean active;

    private int priority;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    protected RuleDefinitionEntity() {
    }

    public RuleDefinitionEntity(
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
    public void update(
            String name,
            RuleType ruleType,
            RuleOperator operator,
            String fieldName,
            String ruleValue,
            DecisionType decisionType,
            String message,
            boolean active,
            int priority,
            LocalDateTime updatedAt
    ) {
        this.name = name;
        this.ruleType = ruleType;
        this.operator = operator;
        this.fieldName = fieldName;
        this.ruleValue = ruleValue;
        this.decisionType = decisionType;
        this.message = message;
        this.active = active;
        this.priority = priority;
        this.updatedAt = updatedAt;
    }

    public void setActive(boolean active, LocalDateTime updatedAt) {
        this.active = active;
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