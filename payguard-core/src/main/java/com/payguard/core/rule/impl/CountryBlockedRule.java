package com.payguard.core.rule.impl;

import com.payguard.core.model.DecisionType;
import com.payguard.core.model.Transaction;
import com.payguard.core.rule.PaymentRule;
import com.payguard.core.rule.RuleResult;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class CountryBlockedRule implements PaymentRule {

    private final Set<String> blockedCountries;

    public CountryBlockedRule(Set<String> blockedCountries) {
        if (blockedCountries == null || blockedCountries.isEmpty()) {
            throw new IllegalArgumentException("List of blocked countries cannot be empty or null");
        }

        Set<String> normalizedBlockedCountries = new HashSet<>();

        for (String country : blockedCountries) {
            if (country == null || country.isBlank()) {
                throw new IllegalArgumentException("Blocked countries cannot contain null or blank values");
            }

            normalizedBlockedCountries.add(country.trim().toUpperCase(Locale.ROOT));
        }

        this.blockedCountries = Set.copyOf(normalizedBlockedCountries);
    }

    @Override
    public RuleResult evaluate(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        String transactionCountry = transaction.getCountry()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (blockedCountries.contains(transactionCountry)) {
            return new RuleResult(false, DecisionType.REJECTED, "CountryBlockedRule","Transaction country is blocked");
        }

        return new RuleResult(true, DecisionType.APPROVED, "CountryBlockedRule","Transaction country is allowed");
    }

    public Set<String> getBlockedCountries() {
        return blockedCountries;
    }
}
