package com.payguard.demo.ruleconfig;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RuleDefinitionRepository extends JpaRepository<RuleDefinitionEntity, Long> {

    List<RuleDefinitionEntity> findAllByOrderByPriorityAscCreatedAtDesc();
}