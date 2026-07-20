package com.payguard.demo.ruleconfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.jayway.jsonpath.JsonPath;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
class RuleDefinitionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateRuleDefinition() throws Exception {
        String requestBody = """
                {
                  "name": "Test high amount review",
                  "ruleType": "AMOUNT_LIMIT",
                  "operator": "GREATER_THAN",
                  "fieldName": "amount",
                  "ruleValue": "10000",
                  "decisionType": "REVIEW",
                  "message": "Transaction amount exceeds review limit",
                  "active": true,
                  "priority": 30
                }
                """;

        mockMvc.perform(post("/api/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name").value("Test high amount review"))
                .andExpect(jsonPath("$.ruleType").value("AMOUNT_LIMIT"))
                .andExpect(jsonPath("$.operator").value("GREATER_THAN"))
                .andExpect(jsonPath("$.fieldName").value("amount"))
                .andExpect(jsonPath("$.ruleValue").value("10000"))
                .andExpect(jsonPath("$.decisionType").value("REVIEW"))
                .andExpect(jsonPath("$.message").value("Transaction amount exceeds review limit"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.priority").value(30));
    }

    @Test
    void shouldReturnAllRuleDefinitions() throws Exception {
        String requestBody = """
                {
                  "name": "Test blocked country rule",
                  "ruleType": "COUNTRY_BLOCKED",
                  "operator": "IN",
                  "fieldName": "country",
                  "ruleValue": "RU,KP,IR",
                  "decisionType": "REJECTED",
                  "message": "Transaction country is blocked",
                  "active": true,
                  "priority": 20
                }
                """;

        mockMvc.perform(post("/api/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", hasItem("Test blocked country rule")))
                .andExpect(jsonPath("$[*].ruleType", hasItem("COUNTRY_BLOCKED")))
                .andExpect(jsonPath("$[*].decisionType", hasItem("REJECTED")));
    }

    @Test
    void shouldReturnValidationErrorWhenRuleNameIsBlank() throws Exception {
        String requestBody = """
                {
                  "name": "",
                  "ruleType": "AMOUNT_LIMIT",
                  "operator": "GREATER_THAN",
                  "fieldName": "amount",
                  "ruleValue": "10000",
                  "decisionType": "REVIEW",
                  "message": "Transaction amount exceeds review limit",
                  "active": true,
                  "priority": 30
                }
                """;

        mockMvc.perform(post("/api/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value("name: Rule name cannot be blank"));
    }
    @Test
    void shouldReturnRuleDefinitionById() throws Exception {
        String requestBody = """
            {
              "name": "Get by id test rule",
              "ruleType": "AMOUNT_LIMIT",
              "operator": "GREATER_THAN",
              "fieldName": "amount",
              "ruleValue": "10000",
              "decisionType": "REVIEW",
              "message": "Transaction amount exceeds review limit",
              "active": true,
              "priority": 30
            }
            """;

        MvcResult createResult = mockMvc.perform(post("/api/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        Integer id = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/api/rules/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Get by id test rule"))
                .andExpect(jsonPath("$.ruleType").value("AMOUNT_LIMIT"))
                .andExpect(jsonPath("$.decisionType").value("REVIEW"));
    }

    @Test
    void shouldUpdateRuleDefinition() throws Exception {
        String createRequestBody = """
            {
              "name": "Rule before update",
              "ruleType": "AMOUNT_LIMIT",
              "operator": "GREATER_THAN",
              "fieldName": "amount",
              "ruleValue": "10000",
              "decisionType": "REVIEW",
              "message": "Old message",
              "active": true,
              "priority": 30
            }
            """;

        MvcResult createResult = mockMvc.perform(post("/api/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequestBody))
                .andExpect(status().isOk())
                .andReturn();

        Integer id = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        String updateRequestBody = """
            {
              "name": "Rule after update",
              "ruleType": "AMOUNT_LIMIT",
              "operator": "GREATER_THAN",
              "fieldName": "amount",
              "ruleValue": "15000",
              "decisionType": "REVIEW",
              "message": "Updated message",
              "active": true,
              "priority": 35
            }
            """;

        mockMvc.perform(put("/api/rules/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Rule after update"))
                .andExpect(jsonPath("$.ruleValue").value("15000"))
                .andExpect(jsonPath("$.message").value("Updated message"))
                .andExpect(jsonPath("$.priority").value(35));
    }

    @Test
    void shouldDeactivateRuleDefinition() throws Exception {
        String requestBody = """
            {
              "name": "Rule to deactivate",
              "ruleType": "COUNTRY_BLOCKED",
              "operator": "IN",
              "fieldName": "country",
              "ruleValue": "RU,KP,IR",
              "decisionType": "REJECTED",
              "message": "Transaction country is blocked",
              "active": true,
              "priority": 20
            }
            """;

        MvcResult createResult = mockMvc.perform(post("/api/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true))
                .andReturn();

        Integer id = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(patch("/api/rules/" + id + "/active")
                        .param("active", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void shouldReturnNotFoundWhenRuleDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/rules/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Rule definition not found with id: 999999"));
    }
    @Test
    void shouldReturnBadRequestWhenAmountRuleUsesInvalidOperator() throws Exception {
        String requestBody = """
            {
              "name": "Bad amount rule",
              "ruleType": "AMOUNT_LIMIT",
              "operator": "IN",
              "fieldName": "amount",
              "ruleValue": "10000,20000",
              "decisionType": "REVIEW",
              "message": "Bad amount rule",
              "active": true,
              "priority": 99
            }
            """;

        mockMvc.perform(post("/api/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value(
                        "Field amount supports only GREATER_THAN, LESS_THAN and EQUALS operators"
                ));
    }

    @Test
    void shouldReturnBadRequestWhenChannelValueIsInvalid() throws Exception {
        String requestBody = """
            {
              "name": "Bad channel rule",
              "ruleType": "CHANNEL_ALLOWED",
              "operator": "IN",
              "fieldName": "channel",
              "ruleValue": "ONLINE,MOBILE",
              "decisionType": "REJECTED",
              "message": "Bad channel rule",
              "active": true,
              "priority": 99
            }
            """;

        mockMvc.perform(post("/api/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Invalid value for field channel: MOBILE"));
    }

    @Test
    void shouldReturnOnlyActiveRuleDefinitions() throws Exception {
        String activeRuleRequestBody = """
            {
              "name": "Active test rule",
              "ruleType": "AMOUNT_LIMIT",
              "operator": "GREATER_THAN",
              "fieldName": "amount",
              "ruleValue": "10000",
              "decisionType": "REVIEW",
              "message": "Transaction amount exceeds review limit",
              "active": true,
              "priority": 10
            }
            """;

        String inactiveRuleRequestBody = """
            {
              "name": "Inactive test rule",
              "ruleType": "COUNTRY_BLOCKED",
              "operator": "IN",
              "fieldName": "country",
              "ruleValue": "RU,KP,IR",
              "decisionType": "REJECTED",
              "message": "Transaction country is blocked",
              "active": false,
              "priority": 20
            }
            """;

        mockMvc.perform(post("/api/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(activeRuleRequestBody))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inactiveRuleRequestBody))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/rules/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", hasItem("Active test rule")))
                .andExpect(jsonPath("$[*].name", not(hasItem("Inactive test rule"))));
    }
}