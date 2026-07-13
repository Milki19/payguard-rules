package com.payguard.demo.service;

import com.payguard.core.engine.PaymentRuleEngine;
import com.payguard.core.model.Transaction;
import com.payguard.core.model.TransactionDecision;
import com.payguard.core.rule.impl.*;
import com.payguard.demo.audit.TransactionEvaluationAuditService;
import com.payguard.demo.dto.TransactionEvaluationRequest;
import com.payguard.demo.dto.TransactionEvaluationResponse;
import org.springframework.stereotype.Service;

@Service
public class PayGuardEvaluationService {

    private final PaymentRuleEngine paymentRuleEngine;
    private final TransactionEvaluationAuditService auditService;

    public PayGuardEvaluationService(PaymentRuleEngine paymentRuleEngine, TransactionEvaluationAuditService auditService) {
        this.paymentRuleEngine = paymentRuleEngine;
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

         TransactionDecision decision = paymentRuleEngine.decideWithFullEvaluation(transaction);
        auditService.saveEvaluation(request, decision);

        return new TransactionEvaluationResponse(decision.getDecisionType(), decision.getReasons());
     }

    public PaymentRuleEngine getPaymentRuleEngine() {
        return paymentRuleEngine;
    }
}
