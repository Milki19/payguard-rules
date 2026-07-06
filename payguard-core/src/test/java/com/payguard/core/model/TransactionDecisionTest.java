package com.payguard.core.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionDecisionTest {

    @Test
    void shouldCreateApprovedDecision() {
        TransactionDecision decision = new TransactionDecision(
                DecisionType.APPROVED,
                List.of("All payment rules passed")
        );

        assertEquals(DecisionType.APPROVED, decision.getDecisionType());
        assertEquals(List.of("All payment rules passed"), decision.getReasons());
        assertTrue(decision.isApproved());
        assertFalse(decision.requiresReview());
        assertFalse(decision.isRejected());
    }

    @Test
    void shouldCreateReviewDecision() {
        TransactionDecision decision = new TransactionDecision(
                DecisionType.REVIEW,
                List.of("Transaction amount exceeds review limit")
        );

        assertEquals(DecisionType.REVIEW, decision.getDecisionType());
        assertTrue(decision.requiresReview());
        assertFalse(decision.isApproved());
        assertFalse(decision.isRejected());
    }

    @Test
    void shouldCreateRejectedDecision() {
        TransactionDecision decision = new TransactionDecision(
                DecisionType.REJECTED,
                List.of("Transaction country is blocked")
        );

        assertEquals(DecisionType.REJECTED, decision.getDecisionType());
        assertTrue(decision.isRejected());
        assertFalse(decision.isApproved());
        assertFalse(decision.requiresReview());
    }

    @Test
    void shouldThrowExceptionWhenDecisionTypeIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new TransactionDecision(null, List.of("Reason"));
        });
    }

    @Test
    void shouldThrowExceptionWhenReasonsIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new TransactionDecision(DecisionType.APPROVED, null);
        });
    }

    @Test
    void shouldThrowExceptionWhenReasonsIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            new TransactionDecision(DecisionType.APPROVED, List.of());
        });
    }

    @Test
    void shouldThrowExceptionWhenReasonsContainsBlank() {
        assertThrows(IllegalArgumentException.class, () -> {
            new TransactionDecision(DecisionType.APPROVED, List.of("   "));
        });
    }

    @Test
    void shouldThrowExceptionWhenReasonsContainsNull() {
        List<String> reasons = new ArrayList<>();
        reasons.add("Valid reason");
        reasons.add(null);

        assertThrows(IllegalArgumentException.class, () -> {
            new TransactionDecision(DecisionType.APPROVED, reasons);
        });
    }

    @Test
    void shouldProtectReasonsFromExternalModification() {
        List<String> reasons = new ArrayList<>();
        reasons.add("Initial reason");

        TransactionDecision decision = new TransactionDecision(
                DecisionType.APPROVED,
                reasons
        );

        reasons.add("Modified outside");

        assertEquals(1, decision.getReasons().size());
        assertEquals("Initial reason", decision.getReasons().get(0));
    }
}