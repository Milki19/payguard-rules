package com.payguard.core.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransactionTest {

    @Test
    void shouldCreateValidTransaction() {
        Transaction transaction = new Transaction(
                "TX-1001",
                new BigDecimal("125.50"),
                "EUR",
                "RS",
                Channel.ONLINE,
                RiskLevel.LOW
        );
        assertEquals("TX-1001", transaction.getTransactionId());
        assertEquals(new BigDecimal("125.50"), transaction.getAmount());
        assertEquals("EUR", transaction.getCurrency());
        assertEquals("RS", transaction.getCountry());
        assertEquals(Channel.ONLINE, transaction.getChannel());
        assertEquals(RiskLevel.LOW, transaction.getCustomerRiskLevel());
    }

    @Test
    void shouldThrowExceptionWhenAmountIsZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transaction(
                    "TX-1002",
                    BigDecimal.ZERO,
                    "EUR",
                    "RS",
                    Channel.ATM,
                    RiskLevel.MEDIUM
            );
        });
    }

    @Test
    void shouldThrowExceptionWhenTransactionIdIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transaction(
                    "   ",
                    new BigDecimal("100.00"),
                    "EUR",
                    "RS",
                    Channel.POS,
                    RiskLevel.HIGH
            );
        });
    }
}


