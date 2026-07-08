package com.payguard.core.engine;

import com.payguard.core.model.DecisionType;
import com.payguard.core.model.Transaction;
import com.payguard.core.model.TransactionDecision;
import com.payguard.core.rule.PaymentRule;
import com.payguard.core.rule.RuleResult;

import java.util.ArrayList;
import java.util.List;

public class PaymentRuleEngine {

    private final List<PaymentRule> rules;

    public PaymentRuleEngine(List<PaymentRule> rules) {
        if (rules == null || rules.isEmpty()) {
            throw new IllegalArgumentException("Payment rules cannot be empty or null");
        }

        for (PaymentRule rule : rules) {
            if (rule == null) {
                throw new IllegalArgumentException("Payment rules cannot contain null values");
            }
        }

        this.rules = List.copyOf(rules);
    }

    public RuleResult evaluate(Transaction transaction) {

        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        for (PaymentRule rule : rules) {
            RuleResult result = rule.evaluate(transaction);

            if (!result.isPassed()) {
                return result;
            }
        }
        return RuleResult.approved("PaymentRuleEngine", "Payment rules have been evaluated successfully");
    }

    public TransactionDecision decide(Transaction transaction) {
        RuleResult result = evaluate(transaction);

        String reason = result.getRuleName() + ": " + result.getMessage();

        return new TransactionDecision(result.getDecisionType(), List.of(reason));
    }

    public List<RuleResult> evaluateAll(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        List<RuleResult> results = new ArrayList<>();
        for (PaymentRule rule : rules) {
            RuleResult result = rule.evaluate(transaction);
            results.add(result);
        }

        return List.copyOf(results);
    }

    public TransactionDecision decideWithFullEvaluation(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        List<RuleResult> results = evaluateAll(transaction);
        List<String> reasons = new ArrayList<>();

        boolean hasRejected = false;
        boolean hasReviewed = false;

        for (RuleResult result : results) {
            if (!result.isPassed())
                reasons.add(result.getRuleName() + ": " + result.getMessage());

            if (result.getDecisionType() == DecisionType.REJECTED)
                hasRejected = true;

            if (result.getDecisionType() == DecisionType.REVIEW)
                hasReviewed = true;
        }

        if (hasRejected)
            return new TransactionDecision(DecisionType.REJECTED, reasons);
        if (hasReviewed)
            return new TransactionDecision(DecisionType.REVIEW, reasons);


        return new TransactionDecision(DecisionType.APPROVED, List.of("All payment rules passed"));

    }

    public List<PaymentRule> getRules() {
        return rules;
    }
}
