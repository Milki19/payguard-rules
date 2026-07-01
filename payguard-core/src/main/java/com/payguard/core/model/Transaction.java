package com.payguard.core.model;

import java.math.BigDecimal;

public class Transaction {

    private final String transactionId;
    private final BigDecimal amount;
    private final String currency;
    private final String country;
    private final Channel channel;
    private final RiskLevel customerRiskLevel;

    public Transaction(String transactionId, BigDecimal amount, String currency, String country, Channel channel, RiskLevel customerRiskLevel) {

        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or blank");
        }

        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be null or blank");
        }

        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("Country cannot be null or blank");
        }

        if (channel == null) {
            throw new IllegalArgumentException("Channel cannot be null");
        }

        if (customerRiskLevel == null) {
            throw new IllegalArgumentException("Customer risk level cannot be null");
        }

        this.transactionId = transactionId;
        this.amount = amount;
        this.currency = currency;
        this.country = country;
        this.channel = channel;
        this.customerRiskLevel = customerRiskLevel;
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


}
