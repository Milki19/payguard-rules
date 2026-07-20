package com.payguard.demo.ruleconfig;

import com.payguard.demo.ruleconfig.dto.RuleDefinitionRequest;
import com.payguard.demo.ruleconfig.dto.RuleDefinitionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rules")
@Tag(
        name = "Rule Definitions",
        description = "API for managing configurable PayGuard rule definitions"
)
public class RuleDefinitionController {

    private final RuleDefinitionService ruleDefinitionService;

    public RuleDefinitionController(RuleDefinitionService ruleDefinitionService) {
        this.ruleDefinitionService = ruleDefinitionService;
    }

    @PostMapping
    @Operation(
            summary = "Create rule definition",
            description = "Creates a new configurable rule definition. Created rules are stored in the database but are not yet connected to the payment engine."
    )
    public RuleDefinitionResponse createRule(@Valid @RequestBody RuleDefinitionRequest request) {
        return ruleDefinitionService.createRule(request);
    }

    @GetMapping
    @Operation(
            summary = "Get all rule definitions",
            description = "Returns all configured rule definitions ordered by priority."
    )
    public List<RuleDefinitionResponse> getAllRules() {
        return ruleDefinitionService.getAllRules();
    }

    @GetMapping("/active")
    @Operation(
            summary = "Get active rule definitions",
            description = "Returns only active rule definitions ordered by priority. These are the rules used by the dynamic payment engine."
    )
    public List<RuleDefinitionResponse> getActiveRules() {
        return ruleDefinitionService.getActiveRules();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get rule definition by ID",
            description = "Returns a single rule definition by its ID."
    )
    public RuleDefinitionResponse getRuleById(@PathVariable("id") Long id) {
        return ruleDefinitionService.getRuleById(id);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update rule definition",
            description = "Updates an existing configurable rule definition."
    )
    public RuleDefinitionResponse updateRule(
            @PathVariable("id") Long id,
            @Valid @RequestBody RuleDefinitionRequest request
    ) {
        return ruleDefinitionService.updateRule(id, request);
    }

    @PatchMapping("/{id}/active")
    @Operation(
            summary = "Activate or deactivate rule definition",
            description = "Changes only the active status of a rule definition."
    )
    public RuleDefinitionResponse setRuleActive(
            @PathVariable("id") Long id,
            @RequestParam(name = "active") boolean active
    ) {
        return ruleDefinitionService.setRuleActive(id, active);
    }
}