package com.payguard.demo.dto;

import com.payguard.core.model.DecisionType;

import java.util.List;

public class TransactionEvaluationResponse {

    private DecisionType decisionType;
    private List<String> reasons;

    public TransactionEvaluationResponse(DecisionType decisionType, List<String> reasons) {
        this.decisionType = decisionType;
        this.reasons = reasons;
    }

    public DecisionType getDecisionType() {
        return decisionType;
    }

    public List<String> getReasons() {
        return reasons;
    }
}