package com.payguard.demo.controller;

import com.payguard.core.model.DecisionType;
import com.payguard.demo.dto.TransactionEvaluationRequest;
import com.payguard.demo.dto.TransactionEvaluationResponse;
import com.payguard.demo.service.PayGuardEvaluationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionEvaluationController {

    private final PayGuardEvaluationService payGuardEvaluationService;

    public TransactionEvaluationController(PayGuardEvaluationService payGuardEvaluationService) {
        this.payGuardEvaluationService = payGuardEvaluationService;
    }

    @PostMapping("/evaluate")
    public TransactionEvaluationResponse evaluate(@RequestBody TransactionEvaluationRequest request) {
        return payGuardEvaluationService.evaluateTransaction(request);
    }
}