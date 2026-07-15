package com.payguard.demo.audit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionEvaluationAuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnAllAuditEvaluations() throws Exception {
        String requestBody = """
                {
                  "transactionId": "TX-AUDIT-MOCKMVC-1",
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
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/audit/evaluations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].transactionId", hasItem("TX-AUDIT-MOCKMVC-1")))
                .andExpect(jsonPath("$[*].decisionType", hasItem("APPROVED")));
    }

    @Test
    void shouldReturnAuditEvaluationsByTransactionId() throws Exception {
        String requestBody = """
                {
                  "transactionId": "TX-AUDIT-MOCKMVC-2",
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
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/audit/evaluations/TX-AUDIT-MOCKMVC-2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].transactionId", hasItem("TX-AUDIT-MOCKMVC-2")))
                .andExpect(jsonPath("$[*].decisionType", hasItem("REJECTED")))
                .andExpect(jsonPath("$[*].reasons", hasItem(org.hamcrest.Matchers.containsString("CountryBlockedRule"))));
    }
}