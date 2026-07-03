package com.payguard.core.rule;

import com.payguard.core.model.DecisionType;

public class RuleResult {

    private final boolean passed;
    private final DecisionType decisionType;
    private final String message;

    public RuleResult(boolean passed, DecisionType decisionType, String message) {

        if (decisionType == null) {
            throw new IllegalArgumentException("Decision type cannot be null");
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Rule result message cannot be null or blank");
        }

        this.passed = passed;
        this.decisionType = decisionType;
        this.message = message;
    }

    public boolean isPassed() {
        return passed;
    }

    public DecisionType getDecisionType() {
        return decisionType;
    }

    public String getMessage() {
        return message;
    }
}
