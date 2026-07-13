package com.payguard.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionEvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
                .andExpect(jsonPath("$.reasons", hasSize(5)))
                .andExpect(jsonPath("$.reasons", hasItem("CurrencyAllowedRule: Transaction currency is not allowed")))
                .andExpect(jsonPath("$.reasons", hasItem("CountryBlockedRule: Transaction country is blocked")))
                .andExpect(jsonPath("$.reasons", hasItem("ChannelAllowedRule: Transaction channel is not allowed")))
                .andExpect(jsonPath("$.reasons", hasItem("AmountLimitRule: Transaction amount exceeds review limit")))
                .andExpect(jsonPath("$.reasons", hasItem("RiskLevelRule: High risk customer requires manual review")));
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