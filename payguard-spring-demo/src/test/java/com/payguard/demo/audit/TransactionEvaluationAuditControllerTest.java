package com.payguard.demo.audit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.containsString;
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
    @Test
    void shouldReturnPagedAuditEvaluations() throws Exception {
        String requestBody = """
            {
              "transactionId": "TX-AUDIT-PAGED-1",
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

        mockMvc.perform(get("/api/audit/evaluations/search")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.last").exists());
    }
    @Test
    void shouldReturnPagedAuditEvaluationsFilteredByDecisionType() throws Exception {
        String requestBody = """
            {
              "transactionId": "TX-AUDIT-REJECTED-FILTER-1",
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

        mockMvc.perform(get("/api/audit/evaluations/search")
                        .param("decisionType", "REJECTED")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].transactionId", hasItem("TX-AUDIT-REJECTED-FILTER-1")))
                .andExpect(jsonPath("$.content[*].decisionType", hasItem("REJECTED")))
                .andExpect(jsonPath("$.content[*].reasons", hasItem(containsString("CountryBlockedRule"))));
    }

    @Test
    void shouldReturnBadRequestWhenPageSizeIsTooLarge() throws Exception {
        mockMvc.perform(get("/api/audit/evaluations/search")
                        .param("page", "0")
                        .param("size", "200"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Page size must be between 1 and 100"));
    }
}