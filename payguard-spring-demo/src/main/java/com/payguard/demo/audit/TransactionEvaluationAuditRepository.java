package com.payguard.demo.audit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionEvaluationAuditRepository extends JpaRepository<TransactionEvaluationAuditEntity, Long> {

    List<TransactionEvaluationAuditEntity> findByTransactionId(String transactionId);
}
