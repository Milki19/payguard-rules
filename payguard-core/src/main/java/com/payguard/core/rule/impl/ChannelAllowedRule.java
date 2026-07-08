package com.payguard.core.rule.impl;

import com.payguard.core.model.Channel;
import com.payguard.core.model.Transaction;
import com.payguard.core.rule.PaymentRule;
import com.payguard.core.rule.RuleResult;

import java.util.Set;

public class ChannelAllowedRule implements PaymentRule {

    private final Set<Channel> allowedChannels;
    private static final String RULE_NAME = "ChannelAllowedRule";

    public ChannelAllowedRule(Set<Channel> allowedChannels) {
        if  (allowedChannels == null || allowedChannels.isEmpty()) {
            throw new IllegalArgumentException("Channel allowed cannot be null or empty");
        }
        for (Channel channel : allowedChannels) {
            if (channel == null) {
                throw new IllegalArgumentException("Allowed channels cannot contain null values");
            }
        }

        this.allowedChannels = Set.copyOf(allowedChannels);
    }

    @Override
    public RuleResult evaluate(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction object cannot be null");
        }
        if (!allowedChannels.contains(transaction.getChannel()))
            return RuleResult.rejected(RULE_NAME, "Transaction channel is not allowed");
        else
            return RuleResult.approved(RULE_NAME, "Transaction channel is allowed");
    }
}
