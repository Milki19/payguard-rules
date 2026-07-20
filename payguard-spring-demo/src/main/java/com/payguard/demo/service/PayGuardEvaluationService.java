package com.payguard.demo.service;

import com.payguard.core.engine.PaymentRuleEngine;
import com.payguard.core.model.Transaction;
import com.payguard.core.model.TransactionDecision;
import com.payguard.demo.audit.TransactionEvaluationAuditService;
import com.payguard.demo.dto.TransactionEvaluationRequest;
import com.payguard.demo.dto.TransactionEvaluationResponse;
import com.payguard.demo.ruleconfig.engine.DatabaseRuleEngineFactory;
import org.springframework.stereotype.Service;

@Service
public class PayGuardEvaluationService {

    private final DatabaseRuleEngineFactory databaseRuleEngineFactory;
    private final TransactionEvaluationAuditService auditService;

    public PayGuardEvaluationService(DatabaseRuleEngineFactory databaseRuleEngineFactory, TransactionEvaluationAuditService auditService) {
        this.databaseRuleEngineFactory = databaseRuleEngineFactory;
        this.auditService = auditService;
    }

    public TransactionEvaluationResponse evaluateTransaction(TransactionEvaluationRequest request) {
         Transaction transaction = new Transaction(
                 request.getTransactionId(),
                 request.getAmount(),
                 request.getCurrency(),
                 request.getCountry(),
                 request.getChannel(),
                 request.getCustomerRiskLevel()
         );

        PaymentRuleEngine paymentRuleEngine = databaseRuleEngineFactory.createEngineFromActiveRules();
        TransactionDecision decision = paymentRuleEngine.decideWithFullEvaluation(transaction);
        auditService.saveEvaluation(request, decision);

        return new TransactionEvaluationResponse(decision.getDecisionType(), decision.getReasons());
     }

}
