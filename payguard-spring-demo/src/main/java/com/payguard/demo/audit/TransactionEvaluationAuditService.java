package com.payguard.demo.audit;

import com.payguard.core.model.TransactionDecision;
import com.payguard.demo.audit.dto.TransactionEvaluationAuditResponse;
import com.payguard.demo.dto.TransactionEvaluationRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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

    public List<TransactionEvaluationAuditResponse> getAllEvaluations() {
        return auditRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TransactionEvaluationAuditResponse> getEvaluationsByTransactionId(String transactionId) {
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or blank");
        }

        return auditRepository.findByTransactionIdOrderByCreatedAtDesc(transactionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private String formatReasons(TransactionDecision decision) {
        StringJoiner joiner = new StringJoiner("; ");

        for (String reason : decision.getReasons()) {
            joiner.add(reason);
        }

        return joiner.toString();
    }
    private TransactionEvaluationAuditResponse toResponse(TransactionEvaluationAuditEntity entity) {
        return new TransactionEvaluationAuditResponse(
                entity.getId(),
                entity.getTransactionId(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getCountry(),
                entity.getChannel(),
                entity.getCustomerRiskLevel(),
                entity.getDecisionType(),
                entity.getReasons(),
                entity.getCreatedAt()
        );
    }
}