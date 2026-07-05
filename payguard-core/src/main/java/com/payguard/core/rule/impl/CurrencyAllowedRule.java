package com.payguard.core.rule.impl;

import com.payguard.core.model.DecisionType;
import com.payguard.core.model.Transaction;
import com.payguard.core.rule.PaymentRule;
import com.payguard.core.rule.RuleResult;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class CurrencyAllowedRule implements PaymentRule {

    private final Set<String> allowedCurrencies;

    public CurrencyAllowedRule(Set<String> allowedCurrencies) {
        if (allowedCurrencies == null || allowedCurrencies.isEmpty()) {
            throw new IllegalArgumentException("Set of allowed currencies cannot be empty or null");
        }

        Set<String> normalizedAllowedCurrencies = new HashSet<>();

        for (String currency : allowedCurrencies) {
            if (currency == null || currency.isBlank()) {
                throw new IllegalArgumentException("Set of allowed currencies cannot contain null or blank values");
            }

            normalizedAllowedCurrencies.add(currency.trim().toUpperCase(Locale.ROOT));
        }

        this.allowedCurrencies = Set.copyOf(normalizedAllowedCurrencies);
    }

    @Override
    public RuleResult evaluate(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        String transactionCurrency = transaction.getCurrency().trim().toUpperCase(Locale.ROOT);

        if (!allowedCurrencies.contains(transactionCurrency))
            return new RuleResult(false, DecisionType.REJECTED, "Transaction currency is not allowed");
        else
            return new RuleResult(true, DecisionType.APPROVED, "Transaction currency is allowed");

    }

    public Set<String> getAllowedCurrencies() {
        return allowedCurrencies;
    }
}
