package com.payguard.demo.dto;

import com.payguard.core.model.Channel;
import com.payguard.core.model.RiskLevel;

import java.math.BigDecimal;

public class TransactionEvaluationRequest {

    private String transactionId;
    private BigDecimal amount;
    private String currency;
    private String country;
    private Channel channel;
    private RiskLevel customerRiskLevel;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public RiskLevel getCustomerRiskLevel() {
        return customerRiskLevel;
    }

    public void setCustomerRiskLevel(RiskLevel customerRiskLevel) {
        this.customerRiskLevel = customerRiskLevel;
    }
}
