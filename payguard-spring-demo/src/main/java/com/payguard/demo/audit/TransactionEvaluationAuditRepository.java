package com.payguard.demo.audit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import com.payguard.core.model.DecisionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionEvaluationAuditRepository extends JpaRepository<TransactionEvaluationAuditEntity, Long> {

    List<TransactionEvaluationAuditEntity> findByTransactionId(String transactionId);
    List<TransactionEvaluationAuditEntity> findAllByOrderByCreatedAtDesc();
    List<TransactionEvaluationAuditEntity> findByTransactionIdOrderByCreatedAtDesc(String transactionId);
    Page<TransactionEvaluationAuditEntity> findByDecisionType(DecisionType decisionType, Pageable pageable);
}
