package com.payguard.demo.audit;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.payguard.core.model.Channel;
import com.payguard.core.model.DecisionType;
import com.payguard.core.model.RiskLevel;

@Entity
@Table(name = "transaction_evaluation_audit")
public class TransactionEvaluationAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId;

    private BigDecimal amount;

    private String currency;

    private String country;

    @Enumerated(EnumType.STRING)
    private Channel channel;

    @Enumerated(EnumType.STRING)
    private RiskLevel customerRiskLevel;

    @Enumerated(EnumType.STRING)
    private DecisionType decisionType;

    @Column(length = 2000)
    private String reasons;

    private LocalDateTime createdAt;

    protected TransactionEvaluationAuditEntity() {
    }

    public TransactionEvaluationAuditEntity(String transactionId, BigDecimal amount, String currency, String country, Channel channel, RiskLevel customerRiskLevel, DecisionType decisionType, String reasons, LocalDateTime createdAt) {
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
