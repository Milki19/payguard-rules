package com.payguard.demo.controller;

import com.payguard.core.model.DecisionType;
import com.payguard.demo.audit.TransactionEvaluationAuditRepository;
import com.payguard.demo.ruleconfig.RuleDefinitionEntity;
import com.payguard.demo.ruleconfig.RuleDefinitionRepository;
import com.payguard.demo.ruleconfig.RuleOperator;
import com.payguard.demo.ruleconfig.RuleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionEvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RuleDefinitionRepository ruleDefinitionRepository;

    @Autowired
    private TransactionEvaluationAuditRepository auditRepository;

    @BeforeEach
    void setUp() {
        auditRepository.deleteAll();
        ruleDefinitionRepository.deleteAll();

        LocalDateTime now = LocalDateTime.now();

        ruleDefinitionRepository.save(new RuleDefinitionEntity(
                "Unsupported currency",
                RuleType.CURRENCY_ALLOWED,
                RuleOperator.NOT_IN,
                "currency",
                "EUR,USD,RSD",
                DecisionType.REJECTED,
                "Transaction currency is not allowed",
                true,
                10,
                now,
                now
        ));

        ruleDefinitionRepository.save(new RuleDefinitionEntity(
                "Blocked countries",
                RuleType.COUNTRY_BLOCKED,
                RuleOperator.IN,
                "country",
                "RU,KP,IR",
                DecisionType.REJECTED,
                "Transaction country is blocked",
                true,
                20,
                now,
                now
        ));

        ruleDefinitionRepository.save(new RuleDefinitionEntity(
                "High amount review",
                RuleType.AMOUNT_LIMIT,
                RuleOperator.GREATER_THAN,
                "amount",
                "10000",
                DecisionType.REVIEW,
                "Transaction amount exceeds review limit",
                true,
                30,
                now,
                now
        ));

        ruleDefinitionRepository.save(new RuleDefinitionEntity(
                "High risk review",
                RuleType.RISK_LEVEL,
                RuleOperator.EQUALS,
                "customerRiskLevel",
                "HIGH",
                DecisionType.REVIEW,
                "High risk customer requires manual review",
                true,
                40,
                now,
                now
        ));

        ruleDefinitionRepository.save(new RuleDefinitionEntity(
                "Unsupported channel",
                RuleType.CHANNEL_ALLOWED,
                RuleOperator.NOT_IN,
                "channel",
                "ONLINE,POS",
                DecisionType.REJECTED,
                "Transaction channel is not allowed",
                true,
                50,
                now,
                now
        ));
    }

    @Test
    void shouldApproveValidLowRiskTransaction() throws Exception {
        String requestBody = """
                {
                  "transactionId": "TX-1001",
                  "amount": 5000,
                  "currency": "EUR",
                  "country": "RS",
                  "channel": "ONLINE",
                  "customerRiskLevel": "LOW"
                }
                """;

        mockMvc.perform(post("/api/transactions/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decisionType").value("APPROVED"))
                .andExpect(jsonPath("$.reasons[0]").value("All payment rules passed"));
    }

    @Test
    void shouldRejectInvalidTransactionWithMultipleReasons() throws Exception {
        String requestBody = """
                {
                  "transactionId": "TX-1002",
                  "amount": 12500,
                  "currency": "GBP",
                  "country": "RU",
                  "channel": "ATM",
                  "customerRiskLevel": "HIGH"
                }
                """;

        mockMvc.perform(post("/api/transactions/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decisionType").value("REJECTED"))
                .andExpect(jsonPath("$.reasons", hasItem(containsString("Unsupported currency"))))
                .andExpect(jsonPath("$.reasons", hasItem(containsString("Blocked countries"))))
                .andExpect(jsonPath("$.reasons", hasItem(containsString("High amount review"))))
                .andExpect(jsonPath("$.reasons", hasItem(containsString("High risk review"))))
                .andExpect(jsonPath("$.reasons", hasItem(containsString("Unsupported channel"))));
    }

    @Test
    void shouldReturnValidationErrorWhenTransactionIdIsBlank() throws Exception {
        String requestBody = """
                {
                  "transactionId": "",
                  "amount": 5000,
                  "currency": "EUR",
                  "country": "RS",
                  "channel": "ONLINE",
                  "customerRiskLevel": "LOW"
                }
                """;

        mockMvc.perform(post("/api/transactions/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value("transactionId: Transaction ID cannot be blank"));
    }
}