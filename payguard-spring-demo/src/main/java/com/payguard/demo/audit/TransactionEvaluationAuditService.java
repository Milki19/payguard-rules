package com.payguard.demo.audit;

import com.payguard.core.model.TransactionDecision;
import com.payguard.demo.dto.TransactionEvaluationRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.StringJoiner;

@Service
public class TransactionEvaluationAuditService {

    private final TransactionEvaluationAuditRepository auditRepository;

    public TransactionEvaluationAuditService(TransactionEvaluationAuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public TransactionEvaluationAuditEntity saveEvaluation(TransactionEvaluationRequest request, TransactionDecision decision) {
        if (request == null) {
            throw new IllegalArgumentException("Transaction evaluation request cannot be null");
        }

        if (decision == null) {
            throw new IllegalArgumentException("Transaction decision cannot be null");
        }

        TransactionEvaluationAuditEntity auditEntity = new TransactionEvaluationAuditEntity(
                request.getTransactionId(),
                request.getAmount(),
                request.getCurrency(),
                request.getCountry(),
                request.getChannel(),
                request.getCustomerRiskLevel(),
                decision.getDecisionType(),
                formatReasons(decision),
                LocalDateTime.now()
        );

        return auditRepository.save(auditEntity);
    }

    private String formatReasons(TransactionDecision decision) {
        StringJoiner joiner = new StringJoiner("; ");

        for (String reason : decision.getReasons()) {
            joiner.add(reason);
        }

        return joiner.toString();
    }
}