package com.payguard.demo.ruleconfig;

import com.payguard.demo.ruleconfig.dto.RuleDefinitionRequest;
import com.payguard.demo.ruleconfig.dto.RuleDefinitionResponse;
import org.springframework.stereotype.Service;
import com.payguard.demo.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RuleDefinitionService {

    private final RuleDefinitionRepository ruleDefinitionRepository;
    private final RuleDefinitionValidator ruleDefinitionValidator;

    public RuleDefinitionService(RuleDefinitionRepository ruleDefinitionRepository, RuleDefinitionValidator ruleDefinitionValidator) {
        this.ruleDefinitionRepository = ruleDefinitionRepository;
        this.ruleDefinitionValidator = ruleDefinitionValidator;
    }

    public RuleDefinitionResponse createRule(RuleDefinitionRequest request) {
        LocalDateTime now = LocalDateTime.now();
        ruleDefinitionValidator.validate(request);

        RuleDefinitionEntity entity = new RuleDefinitionEntity(
                request.getName(),
                request.getRuleType(),
                request.getOperator(),
                request.getFieldName(),
                request.getRuleValue(),
                request.getDecisionType(),
                request.getMessage(),
                request.isActive(),
                request.getPriority(),
                now,
                now
        );

        RuleDefinitionEntity savedEntity = ruleDefinitionRepository.save(entity);

        return toResponse(savedEntity);
    }

    public List<RuleDefinitionResponse> getAllRules() {
        return ruleDefinitionRepository.findAllByOrderByPriorityAscCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<RuleDefinitionResponse> getActiveRules() {
        return ruleDefinitionRepository.findByActiveTrueOrderByPriorityAscCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private RuleDefinitionResponse toResponse(RuleDefinitionEntity entity) {
        return new RuleDefinitionResponse(
                entity.getId(),
                entity.getName(),
                entity.getRuleType(),
                entity.getOperator(),
                entity.getFieldName(),
                entity.getRuleValue(),
                entity.getDecisionType(),
                entity.getMessage(),
                entity.isActive(),
                entity.getPriority(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
    public RuleDefinitionResponse getRuleById(Long id) {
        RuleDefinitionEntity entity = findRuleById(id);
        return toResponse(entity);
    }

    public RuleDefinitionResponse updateRule(Long id, RuleDefinitionRequest request) {
        RuleDefinitionEntity entity = findRuleById(id);
        ruleDefinitionValidator.validate(request);

        entity.update(
                request.getName(),
                request.getRuleType(),
                request.getOperator(),
                request.getFieldName(),
                request.getRuleValue(),
                request.getDecisionType(),
                request.getMessage(),
                request.isActive(),
                request.getPriority(),
                LocalDateTime.now()
        );

        RuleDefinitionEntity savedEntity = ruleDefinitionRepository.save(entity);

        return toResponse(savedEntity);
    }

    public RuleDefinitionResponse setRuleActive(Long id, boolean active) {
        RuleDefinitionEntity entity = findRuleById(id);

        entity.setActive(active, LocalDateTime.now());

        RuleDefinitionEntity savedEntity = ruleDefinitionRepository.save(entity);

        return toResponse(savedEntity);
    }

    private RuleDefinitionEntity findRuleById(Long id) {
        return ruleDefinitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule definition not found with id: " + id));
    }
}