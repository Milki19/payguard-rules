package com.payguard.core.rule.impl;

import com.payguard.core.model.Channel;
import com.payguard.core.model.DecisionType;
import com.payguard.core.model.RiskLevel;
import com.payguard.core.model.Transaction;
import com.payguard.core.rule.RuleResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChannelAllowedRuleTest {

    @Test
    void shouldApproveTransactionWithAllowedChannel() {
        ChannelAllowedRule rule = new ChannelAllowedRule(Set.of(
                Channel.ONLINE,
                Channel.POS
        ));

        Transaction transaction = validTransaction(Channel.ONLINE);

        RuleResult result = rule.evaluate(transaction);

        assertTrue(result.isPassed());
        assertEquals(DecisionType.APPROVED, result.getDecisionType());
        assertEquals("ChannelAllowedRule", result.getRuleName());
        assertEquals("Transaction channel is allowed", result.getMessage());
    }

    @Test
    void shouldRejectTransactionWithNotAllowedChannel() {
        ChannelAllowedRule rule = new ChannelAllowedRule(Set.of(
                Channel.ONLINE,
                Channel.POS
        ));

        Transaction transaction = validTransaction(Channel.ATM);

        RuleResult result = rule.evaluate(transaction);

        assertFalse(result.isPassed());
        assertEquals(DecisionType.REJECTED, result.getDecisionType());
        assertEquals("ChannelAllowedRule", result.getRuleName());
        assertEquals("Transaction channel is not allowed", result.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAllowedChannelsIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ChannelAllowedRule(null);
        });
    }

    @Test
    void shouldThrowExceptionWhenAllowedChannelsIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ChannelAllowedRule(Set.of());
        });
    }

    @Test
    void shouldThrowExceptionWhenAllowedChannelsContainsNull() {
        Set<Channel> allowedChannels = new HashSet<>();
        allowedChannels.add(Channel.ONLINE);
        allowedChannels.add(null);

        assertThrows(IllegalArgumentException.class, () -> {
            new ChannelAllowedRule(allowedChannels);
        });
    }

    @Test
    void shouldThrowExceptionWhenTransactionIsNull() {
        ChannelAllowedRule rule = new ChannelAllowedRule(Set.of(
                Channel.ONLINE,
                Channel.POS
        ));

        assertThrows(IllegalArgumentException.class, () -> {
            rule.evaluate(null);
        });
    }

    private Transaction validTransaction(Channel channel) {
        return new Transaction(
                "TX-1001",
                new BigDecimal("5000.00"),
                "EUR",
                "RS",
                channel,
                RiskLevel.LOW
        );
    }
}