package com.payguard.demo.dto;

import com.payguard.core.model.Channel;
import com.payguard.core.model.RiskLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


import java.math.BigDecimal;

public class TransactionEvaluationRequest {

    @NotBlank(message = "Transaction ID cannot be blank")
    private String transactionId;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Currency cannot be blank")
    private String currency;

    @NotBlank(message = "Country cannot be blank")
    private String country;

    @NotNull(message = "Channel cannot be null")
    private Channel channel;

    @NotNull(message = "Customer risk level cannot be null")
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
