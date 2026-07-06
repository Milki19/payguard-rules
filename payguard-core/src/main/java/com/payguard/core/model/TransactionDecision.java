package com.payguard.core.model;

import java.util.List;

public class TransactionDecision {
    private final DecisionType decisionType;
    private final List<String> reasons;

    public TransactionDecision(DecisionType decisionType, List<String> reasons) {
        if (decisionType == null){
            throw new IllegalArgumentException("Decision type cannot be null");
        }
        if (reasons == null || reasons.isEmpty()) {
            throw new IllegalArgumentException("Reasons cannot be empty or null");
        }
        for (String reason : reasons) {
            if (reason == null || reason.isBlank()) throw new IllegalArgumentException("Reason cannot be null or blank");
        }

        this.decisionType = decisionType;
        this.reasons = List.copyOf(reasons);
    }

    public boolean isApproved() {
        return decisionType == DecisionType.APPROVED;
    }
    public boolean requiresReview() {
        return decisionType == DecisionType.REVIEW;
    }
    public boolean isRejected() {
        return decisionType == DecisionType.REJECTED;
    }

    public DecisionType getDecisionType() {
        return decisionType;
    }

    public List<String> getReasons() {
        return reasons;
    }
}
