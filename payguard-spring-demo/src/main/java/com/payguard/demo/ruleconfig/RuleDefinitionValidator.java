package com.payguard.demo.ruleconfig;

import com.payguard.core.model.Channel;
import com.payguard.core.model.RiskLevel;
import com.payguard.demo.ruleconfig.dto.RuleDefinitionRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;

@Component
public class RuleDefinitionValidator {

    private static final Set<String> SUPPORTED_FIELDS = Set.of(
            "amount",
            "currency",
            "country",
            "channel",
            "customerRiskLevel"
    );

    public void validate(RuleDefinitionRequest request) {
        validateFieldName(request.getFieldName());
        validateOperatorForField(request.getFieldName(), request.getOperator());
        validateRuleValueForField(request.getFieldName(), request.getRuleValue());
    }

    private void validateFieldName(String fieldName) {
        if (!SUPPORTED_FIELDS.contains(fieldName)) {
            throw new IllegalArgumentException("Unsupported rule field: " + fieldName);
        }
    }

    private void validateOperatorForField(String fieldName, RuleOperator operator) {
        switch (fieldName) {
            case "amount" -> validateAmountOperator(operator);
            case "currency", "country", "channel", "customerRiskLevel" -> validateTextOperator(fieldName, operator);
            default -> throw new IllegalArgumentException("Unsupported rule field: " + fieldName);
        }
    }

    private void validateAmountOperator(RuleOperator operator) {
        if (operator != RuleOperator.GREATER_THAN
                && operator != RuleOperator.LESS_THAN
                && operator != RuleOperator.EQUALS) {
            throw new IllegalArgumentException("Field amount supports only GREATER_THAN, LESS_THAN and EQUALS operators");
        }
    }

    private void validateTextOperator(String fieldName, RuleOperator operator) {
        if (operator != RuleOperator.EQUALS
                && operator != RuleOperator.IN
                && operator != RuleOperator.NOT_IN) {
            throw new IllegalArgumentException("Field " + fieldName + " supports only EQUALS, IN and NOT_IN operators");
        }
    }

    private void validateRuleValueForField(String fieldName, String ruleValue) {
        switch (fieldName) {
            case "amount" -> validateNumericValue(ruleValue);
            case "channel" -> validateEnumValues(ruleValue, Channel.class, "channel");
            case "customerRiskLevel" -> validateEnumValues(ruleValue, RiskLevel.class, "customerRiskLevel");
            case "currency", "country" -> validateListValues(ruleValue, fieldName);
            default -> throw new IllegalArgumentException("Unsupported rule field: " + fieldName);
        }
    }

    private void validateNumericValue(String ruleValue) {
        try {
            new BigDecimal(ruleValue);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Rule value must be numeric for field amount");
        }
    }

    private <E extends Enum<E>> void validateEnumValues(String ruleValue, Class<E> enumClass, String fieldName) {
        for (String value : splitValues(ruleValue)) {
            try {
                Enum.valueOf(enumClass, value);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid value for field " + fieldName + ": " + value);
            }
        }
    }

    private void validateListValues(String ruleValue, String fieldName) {
        for (String value : splitValues(ruleValue)) {
            if (value.isBlank()) {
                throw new IllegalArgumentException("Rule value list contains blank value for field " + fieldName);
            }
        }
    }

    private String[] splitValues(String ruleValue) {
        return Arrays.stream(ruleValue.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .filter(value -> !value.isBlank())
                .toArray(String[]::new);
    }
}