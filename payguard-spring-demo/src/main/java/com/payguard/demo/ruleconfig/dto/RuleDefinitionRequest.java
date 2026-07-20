package com.payguard.demo.ruleconfig.dto;

import com.payguard.core.model.DecisionType;
import com.payguard.demo.ruleconfig.RuleOperator;
import com.payguard.demo.ruleconfig.RuleType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RuleDefinitionRequest {

    @NotBlank(message = "Rule name cannot be blank")
    private String name;

    @NotNull(message = "Rule type cannot be null")
    private RuleType ruleType;

    @NotNull(message = "Rule operator cannot be null")
    private RuleOperator operator;

    @NotBlank(message = "Field name cannot be blank")
    private String fieldName;

    @NotBlank(message = "Rule value cannot be blank")
    private String ruleValue;

    @NotNull(message = "Decision type cannot be null")
    private DecisionType decisionType;

    @NotBlank(message = "Message cannot be blank")
    private String message;

    private boolean active = true;

    @Min(value = 1, message = "Priority must be greater than zero")
    private int priority;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RuleType getRuleType() {
        return ruleType;
    }

    public void setRuleType(RuleType ruleType) {
        this.ruleType = ruleType;
    }

    public RuleOperator getOperator() {
        return operator;
    }

    public void setOperator(RuleOperator operator) {
        this.operator = operator;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getRuleValue() {
        return ruleValue;
    }

    public void setRuleValue(String ruleValue) {
        this.ruleValue = ruleValue;
    }

    public DecisionType getDecisionType() {
        return decisionType;
    }

    public void setDecisionType(DecisionType decisionType) {
        this.decisionType = decisionType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}