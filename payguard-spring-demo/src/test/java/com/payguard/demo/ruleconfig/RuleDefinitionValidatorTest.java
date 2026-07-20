package com.payguard.demo.ruleconfig;

import com.payguard.core.model.DecisionType;
import com.payguard.demo.ruleconfig.dto.RuleDefinitionRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuleDefinitionValidatorTest {

    private final RuleDefinitionValidator validator = new RuleDefinitionValidator();

    @Test
    void shouldAcceptValidAmountRule() {
        RuleDefinitionRequest request = request(
                "High amount review",
                RuleType.AMOUNT_LIMIT,
                RuleOperator.GREATER_THAN,
                "amount",
                "10000",
                DecisionType.REVIEW,
                "Transaction amount exceeds review limit"
        );

        assertDoesNotThrow(() -> validator.validate(request));
    }

    @Test
    void shouldRejectUnsupportedFieldName() {
        RuleDefinitionRequest request = request(
                "Unsupported field rule",
                RuleType.AMOUNT_LIMIT,
                RuleOperator.GREATER_THAN,
                "merchantCategory",
                "10000",
                DecisionType.REVIEW,
                "Unsupported field"
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(request)
        );

        assertEquals("Unsupported rule field: merchantCategory", exception.getMessage());
    }

    @Test
    void shouldRejectInvalidOperatorForAmountField() {
        RuleDefinitionRequest request = request(
                "Bad amount rule",
                RuleType.AMOUNT_LIMIT,
                RuleOperator.IN,
                "amount",
                "10000,20000",
                DecisionType.REVIEW,
                "Bad amount rule"
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(request)
        );

        assertEquals(
                "Field amount supports only GREATER_THAN, LESS_THAN and EQUALS operators",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectNonNumericAmountValue() {
        RuleDefinitionRequest request = request(
                "Bad amount value",
                RuleType.AMOUNT_LIMIT,
                RuleOperator.GREATER_THAN,
                "amount",
                "banana",
                DecisionType.REVIEW,
                "Bad amount value"
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(request)
        );

        assertEquals("Rule value must be numeric for field amount", exception.getMessage());
    }

    @Test
    void shouldRejectInvalidChannelValue() {
        RuleDefinitionRequest request = request(
                "Bad channel rule",
                RuleType.CHANNEL_ALLOWED,
                RuleOperator.IN,
                "channel",
                "ONLINE,MOBILE",
                DecisionType.REJECTED,
                "Bad channel value"
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(request)
        );

        assertEquals("Invalid value for field channel: MOBILE", exception.getMessage());
    }

    @Test
    void shouldRejectInvalidRiskLevelValue() {
        RuleDefinitionRequest request = request(
                "Bad risk rule",
                RuleType.RISK_LEVEL,
                RuleOperator.EQUALS,
                "customerRiskLevel",
                "EXTREME",
                DecisionType.REVIEW,
                "Bad risk value"
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(request)
        );

        assertEquals("Invalid value for field customerRiskLevel: EXTREME", exception.getMessage());
    }

    private RuleDefinitionRequest request(
            String name,
            RuleType ruleType,
            RuleOperator operator,
            String fieldName,
            String ruleValue,
            DecisionType decisionType,
            String message
    ) {
        RuleDefinitionRequest request = new RuleDefinitionRequest();
        request.setName(name);
        request.setRuleType(ruleType);
        request.setOperator(operator);
        request.setFieldName(fieldName);
        request.setRuleValue(ruleValue);
        request.setDecisionType(decisionType);
        request.setMessage(message);
        request.setActive(true);
        request.setPriority(10);
        return request;
    }
}