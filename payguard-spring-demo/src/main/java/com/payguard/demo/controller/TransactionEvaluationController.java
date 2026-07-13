package com.payguard.demo.controller;

import com.payguard.demo.dto.TransactionEvaluationRequest;
import com.payguard.demo.dto.TransactionEvaluationResponse;
import com.payguard.demo.service.PayGuardEvaluationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/transactions")
@Tag(
        name = "Transaction Evaluation",
        description = "API for evaluating payment transactions using PayGuard rules"
)
public class TransactionEvaluationController {

    private final PayGuardEvaluationService payGuardEvaluationService;

    public TransactionEvaluationController(PayGuardEvaluationService payGuardEvaluationService) {
        this.payGuardEvaluationService = payGuardEvaluationService;
    }

    @Operation(
            summary = "Evaluate a payment transaction",
            description = "Evaluates a payment transaction against configured PayGuard rules and returns a final decision with reasons."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction evaluated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid transaction request")
    })
    @PostMapping("/evaluate")
    public TransactionEvaluationResponse evaluate(@Valid @RequestBody TransactionEvaluationRequest request) {
        return payGuardEvaluationService.evaluateTransaction(request);
    }
}