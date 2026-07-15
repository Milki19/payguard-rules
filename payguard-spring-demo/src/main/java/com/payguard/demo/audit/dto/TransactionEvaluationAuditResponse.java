package com.payguard.demo.audit.dto;

import com.payguard.core.model.Channel;
import com.payguard.core.model.DecisionType;
import com.payguard.core.model.RiskLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionEvaluationAuditResponse {

    private final Long id;
    private final String transactionId;
    private final BigDecimal amount;
    private final String currency;
    private final String country;
    private final Channel channel;
    private final RiskLevel customerRiskLevel;
    private final DecisionType decisionType;
    private final String reasons;
    private final LocalDateTime createdAt;

    public TransactionEvaluationAuditResponse(
            Long id,
            String transactionId,
            BigDecimal amount,
            String currency,
            String country,
            Channel channel,
            RiskLevel customerRiskLevel,
            DecisionType decisionType,
            String reasons,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.transactionId = transactionId;
        this.amount = amount;
        this.currency = currency;
        this.country = country;
        this.channel = channel;
        this.customerRiskLevel = customerRiskLevel;
        this.decisionType = decisionType;
        this.reasons = reasons;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCountry() {
        return country;
    }

    public Channel getChannel() {
        return channel;
    }

    public RiskLevel getCustomerRiskLevel() {
        return customerRiskLevel;
    }

    public DecisionType getDecisionType() {
        return decisionType;
    }

    public String getReasons() {
        return reasons;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}