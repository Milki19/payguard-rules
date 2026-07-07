package com.payguard.core.rule;

import com.payguard.core.model.DecisionType;

public class RuleResult {

    private final boolean passed;
    private final DecisionType decisionType;
    private final String message;
    private final String ruleName;

    public RuleResult(boolean passed, DecisionType decisionType, String ruleName,String message) {

        if (decisionType == null) {
            throw new IllegalArgumentException("Decision type cannot be null");
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Rule result message cannot be null or blank");
        }

        if (ruleName == null || ruleName.isBlank()) {
            throw new IllegalArgumentException("Rule result rule name cannot be null or blank");
        }

        this.passed = passed;
        this.decisionType = decisionType;
        this.ruleName = ruleName;
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

    public String getRuleName() {return ruleName;}
}
