package com.payguard.demo.audit;

import com.payguard.demo.audit.dto.TransactionEvaluationAuditResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit/evaluations")
@Tag(
        name = "Audit Log",
        description = "API for reading transaction evaluation audit records"
)
public class TransactionEvaluationAuditController {

    private final TransactionEvaluationAuditService auditService;

    public TransactionEvaluationAuditController(TransactionEvaluationAuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @Operation(
            summary = "Get all transaction evaluation audit records",
            description = "Returns all saved transaction evaluation audit records ordered by creation time descending."
    )
    public List<TransactionEvaluationAuditResponse> getAllEvaluations() {
        return auditService.getAllEvaluations();
    }

    @GetMapping("/{transactionId}")
    @Operation(
            summary = "Get audit records by transaction ID",
            description = "Returns saved audit records for a specific transaction ID ordered by creation time descending."
    )
    public List<TransactionEvaluationAuditResponse> getEvaluationsByTransactionId(
            @PathVariable("transactionId") String transactionId
    ) {
        return auditService.getEvaluationsByTransactionId(transactionId);
    }
}