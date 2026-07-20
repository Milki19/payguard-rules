package com.payguard.demo.ruleconfig.engine;

import com.payguard.core.model.DecisionType;
import com.payguard.core.model.Transaction;
import com.payguard.core.rule.PaymentRule;
import com.payguard.core.rule.RuleResult;
import com.payguard.demo.ruleconfig.RuleOperator;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class DynamicPaymentRule implements PaymentRule {

    private final String ruleName;
    private final String fieldName;
    private final RuleOperator operator;
    private final String ruleValue;
    private final DecisionType decisionType;
    private final String message;

    public DynamicPaymentRule(
            String ruleName,
            String fieldName,
            RuleOperator operator,
            String ruleValue,
            DecisionType decisionType,
            String message
    ) {
        if (ruleName == null || ruleName.isBlank()) {
            throw new IllegalArgumentException("Rule name cannot be null or blank");
        }

        if (fieldName == null || fieldName.isBlank()) {
            throw new IllegalArgumentException("Field name cannot be null or blank");
        }

        if (operator == null) {
            throw new IllegalArgumentException("Rule operator cannot be null");
        }

        if (ruleValue == null || ruleValue.isBlank()) {
            throw new IllegalArgumentException("Rule value cannot be null or blank");
        }

        if (decisionType == null) {
            throw new IllegalArgumentException("Decision type cannot be null");
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be null or blank");
        }

        this.ruleName = ruleName;
        this.fieldName = fieldName.trim();
        this.operator = operator;
        this.ruleValue = ruleValue.trim();
        this.decisionType = decisionType;
        this.message = message;
    }

    @Override
    public RuleResult evaluate(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        if (!conditionMatches(transaction)) {
            return RuleResult.approved(ruleName, "Rule condition not matched");
        }

        return resultWhenConditionMatches();
    }

    private boolean conditionMatches(Transaction transaction) {
        String actualValue = readFieldValue(transaction);

        return switch (operator) {
            case GREATER_THAN -> compareAsBigDecimal(actualValue) > 0;
            case LESS_THAN -> compareAsBigDecimal(actualValue) < 0;
            case EQUALS -> normalize(actualValue).equals(normalize(ruleValue));
            case IN -> isInList(actualValue);
            case NOT_IN -> !isInList(actualValue);
        };
    }

    private String readFieldValue(Transaction transaction) {
        return switch (fieldName) {
            case "amount" -> transaction.getAmount().toPlainString();
            case "currency" -> normalize(transaction.getCurrency());
            case "country" -> normalize(transaction.getCountry());
            case "channel" -> transaction.getChannel().name();
            case "customerRiskLevel" -> transaction.getCustomerRiskLevel().name();
            default -> throw new IllegalArgumentException("Unsupported rule field: " + fieldName);
        };
    }

    private int compareAsBigDecimal(String actualValue) {
        try {
            BigDecimal actual = new BigDecimal(actualValue);
            BigDecimal expected = new BigDecimal(ruleValue);

            return actual.compareTo(expected);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "Rule value must be numeric for operator " + operator + " and field " + fieldName
            );
        }
    }

    private boolean isInList(String actualValue) {
        Set<String> allowedValues = Arrays.stream(ruleValue.split(","))
                .map(this::normalize)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toSet());

        if (allowedValues.isEmpty()) {
            throw new IllegalArgumentException("Rule value list cannot be empty");
        }

        return allowedValues.contains(normalize(actualValue));
    }

    private String normalize(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }

        return value.trim().toUpperCase(Locale.ROOT);
    }

    private RuleResult resultWhenConditionMatches() {
        return switch (decisionType) {
            case APPROVED -> RuleResult.approved(ruleName, message);
            case REVIEW -> RuleResult.review(ruleName, message);
            case REJECTED -> RuleResult.rejected(ruleName, message);
        };
    }
}