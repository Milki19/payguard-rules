# PayGuard Rules

PayGuard Rules is a Java payment transaction validation project built as a small rule engine library with a Spring Boot demo API.

The goal of this project is to demonstrate clean Java backend design through payment-related business rules, unit testing, REST API integration, and modular Maven structure.

## Project Overview

The system evaluates payment transactions and returns a decision:

* `APPROVED`
* `REVIEW`
* `REJECTED`

Each transaction is checked against multiple rules, such as amount limits, blocked countries, allowed currencies, allowed channels, and customer risk level.

The project is split into two Maven modules:

```text
payguard-rules
├── payguard-core
└── payguard-spring-demo
```

## Modules

### payguard-core

Core Java library containing the domain model, rules, rule engine, and tests.

Main packages:

```text
com.payguard.core.model
com.payguard.core.rule
com.payguard.core.rule.impl
com.payguard.core.engine
```

### payguard-spring-demo

Spring Boot demo application that exposes the PayGuard engine through a REST API.

Main packages:

```text
com.payguard.demo.controller
com.payguard.demo.dto
com.payguard.demo.service
com.payguard.demo.config
com.payguard.demo.exception
```

## Core Concepts Used

This project is focused on practicing and demonstrating:

* Java Core
* OOP principles
* Maven multi-module structure
* Immutable domain models
* Interfaces
* Polymorphism
* Strategy pattern
* Rule engine design
* Static factory methods
* Constructor injection
* Spring Boot REST API
* Global exception handling
* Unit testing with JUnit 5
* Clean validation and fail-fast behavior

## Domain Model

### Transaction

Represents a payment transaction.

Main fields:

```text
transactionId
amount
currency
country
channel
customerRiskLevel
```

### DecisionType

Possible decision values:

```text
APPROVED
REVIEW
REJECTED
```

### Channel

Supported transaction channels:

```text
ATM
POS
ONLINE
BANK_TRANSFER
```

### RiskLevel

Customer risk levels:

```text
LOW
MEDIUM
HIGH
```

## Rules

All rules implement the same interface:

```java
public interface PaymentRule {
    RuleResult evaluate(Transaction transaction);
}
```

This allows the engine to evaluate different rules through the same contract.

Implemented rules:

### AmountLimitRule

Sends a transaction to `REVIEW` if the amount is above the configured limit.

### CountryBlockedRule

Rejects a transaction if the country is in the blocked countries list.

### CurrencyAllowedRule

Rejects a transaction if the currency is not in the allowed currencies list.

### ChannelAllowedRule

Rejects a transaction if the channel is not in the allowed channels list.

### RiskLevelRule

Sends a transaction to `REVIEW` if:

* customer risk level is `HIGH`
* customer risk level is `MEDIUM` and amount is above the configured medium-risk limit

## PaymentRuleEngine

`PaymentRuleEngine` evaluates transactions using a list of `PaymentRule` implementations.

It supports two evaluation modes:

### Fast evaluation

Stops at the first failed rule.

```java
RuleResult result = engine.evaluate(transaction);
```

### Full evaluation

Runs all rules and returns all rule results.

```java
List<RuleResult> results = engine.evaluateAll(transaction);
```

### Decision methods

The engine can also return a final `TransactionDecision`.

```java
TransactionDecision decision = engine.decide(transaction);
```

or:

```java
TransactionDecision decision = engine.decideWithFullEvaluation(transaction);
```

Full evaluation collects all failed rule reasons.

Decision priority:

```text
REJECTED > REVIEW > APPROVED
```

## Spring Boot API

The Spring Boot module exposes the engine through a REST endpoint.

### Evaluate Transaction

```http
POST /api/transactions/evaluate
```

Example request:

```json
{
  "transactionId": "TX-1001",
  "amount": 5000,
  "currency": "EUR",
  "country": "RS",
  "channel": "ONLINE",
  "customerRiskLevel": "LOW"
}
```

Example response:

```json
{
  "decisionType": "APPROVED",
  "reasons": [
    "All payment rules passed"
  ]
}
```

Example rejected request:

```json
{
  "transactionId": "TX-1002",
  "amount": 12500,
  "currency": "GBP",
  "country": "RU",
  "channel": "ATM",
  "customerRiskLevel": "HIGH"
}
```

Example response:

```json
{
  "decisionType": "REJECTED",
  "reasons": [
    "CurrencyAllowedRule: Transaction currency is not allowed",
    "CountryBlockedRule: Transaction country is blocked",
    "ChannelAllowedRule: Transaction channel is not allowed",
    "AmountLimitRule: Transaction amount exceeds review limit",
    "RiskLevelRule: High risk customer requires manual review"
  ]
}
```

## Error Handling

The Spring demo uses a global exception handler with `@RestControllerAdvice`.

Invalid requests return a clean JSON error response.

Example invalid request:

```json
{
  "transactionId": "",
  "amount": 5000,
  "currency": "EUR",
  "country": "RS",
  "channel": "ONLINE",
  "customerRiskLevel": "LOW"
}
```

Example response:

```json
{
  "error": "Bad Request",
  "message": "Transaction ID cannot be null or blank"
}
```

## How to Run Tests

From the root project:

```bash
mvn test
```

Or run tests directly from IntelliJ IDEA.

## How to Run Spring Demo

Run the main class:

```text
PayGuardDemoApplication
```

Then send requests to:

```text
http://localhost:8080/api/transactions/evaluate
```

## Example curl Request

```bash
curl -X POST http://localhost:8080/api/transactions/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "TX-1001",
    "amount": 5000,
    "currency": "EUR",
    "country": "RS",
    "channel": "ONLINE",
    "customerRiskLevel": "LOW"
  }'
```

## Project Structure

```text
payguard-rules
├── pom.xml
├── payguard-core
│   ├── pom.xml
│   └── src
│       ├── main/java/com/payguard/core
│       │   ├── engine
│       │   ├── model
│       │   ├── rule
│       │   └── rule/impl
│       └── test/java/com/payguard/core
│
└── payguard-spring-demo
    ├── pom.xml
    └── src/main/java/com/payguard/demo
        ├── config
        ├── controller
        ├── dto
        ├── exception
        └── service
```

## Why This Project Exists

This project was built as a practical Java refresh project focused on backend engineering, clean architecture, payment-style business logic, testing, and Spring Boot API development.

It is designed to show how a small core library can be separated from the REST API layer and reused independently.

## Future Improvements

Planned improvements:

* Add database support for configurable rules
* Add Spring validation annotations
* Add integration tests for REST endpoints
* Add Docker support
* Add GitHub Actions CI
* Add OpenAPI/Swagger documentation
* Add audit logging
* Add rule priorities
* Add rule activation/deactivation
* Add PostgreSQL support
