# PayGuard Architecture

PayGuard Rules is a Java/Spring Boot payment decision engine built as a Maven multi-module project.

The system evaluates payment transactions against configurable database-driven rules and returns a final decision:

```text
APPROVED
REVIEW
REJECTED
```

## High-Level Architecture

```text
Client / Postman / Swagger
        |
        v
TransactionEvaluationController
        |
        v
PayGuardEvaluationService
        |
        v
DatabaseRuleEngineFactory
        |
        v
RuleDefinitionRepository
        |
        v
PostgreSQL rule_definitions
        |
        v
DynamicPaymentRule list
        |
        v
PaymentRuleEngine
        |
        v
TransactionDecision
        |
        v
TransactionEvaluationAuditService
        |
        v
PostgreSQL transaction_evaluation_audit
```

## Modules

The project is split into two Maven modules:

```text
payguard-rules
├── payguard-core
└── payguard-spring-demo
```

## payguard-core

The `payguard-core` module contains the reusable rule engine logic.

It has no Spring dependency.

Main responsibilities:

- transaction domain model
- decision model
- rule interface
- rule result model
- static Java rule implementations
- payment rule engine
- core unit tests

Important packages:

```text
com.payguard.core.model
com.payguard.core.rule
com.payguard.core.rule.impl
com.payguard.core.engine
```

### Core Domain Model

The main domain object is `Transaction`.

It contains:

```text
transactionId
amount
currency
country
channel
customerRiskLevel
```

The main decision enum is `DecisionType`:

```text
APPROVED
REVIEW
REJECTED
```

## PaymentRule Interface

All rules implement the same interface:

```java
public interface PaymentRule {
    RuleResult evaluate(Transaction transaction);
}
```

This allows the engine to work with different rules through one common contract.

This is an application of the Strategy pattern.

## PaymentRuleEngine

`PaymentRuleEngine` receives a list of `PaymentRule` objects.

It supports two evaluation modes.

### Fast Evaluation

Stops at the first failed rule.

```java
RuleResult result = engine.evaluate(transaction);
```

### Full Evaluation

Runs all rules and collects all failed reasons.

```java
TransactionDecision decision = engine.decideWithFullEvaluation(transaction);
```

Decision priority:

```text
REJECTED > REVIEW > APPROVED
```

If at least one rule rejects the transaction, the final decision is `REJECTED`.

If there are no rejected rules but at least one review rule, the final decision is `REVIEW`.

If all rules pass, the final decision is `APPROVED`.

## payguard-spring-demo

The `payguard-spring-demo` module exposes the engine through a Spring Boot REST API.

Main responsibilities:

- REST controllers
- DTOs
- validation
- global exception handling
- JPA entities and repositories
- PostgreSQL integration
- dynamic rule loading
- audit logging
- Swagger/OpenAPI documentation
- MockMvc integration tests

Important packages:

```text
com.payguard.demo.controller
com.payguard.demo.dto
com.payguard.demo.service
com.payguard.demo.exception
com.payguard.demo.audit
com.payguard.demo.ruleconfig
```

## Dynamic Rule Engine

Originally, rules were hardcoded Java classes.

The current version supports database-driven rule definitions.

A rule definition is stored in the `rule_definitions` table and contains:

```text
name
ruleType
fieldName
operator
ruleValue
decisionType
message
active
priority
createdAt
updatedAt
```

Only active rules are loaded into the dynamic engine.

Rules are ordered by priority.

```text
lower priority number = evaluated earlier
```

## Dynamic Evaluation Flow

When a transaction is evaluated:

```text
1. Client sends POST /api/transactions/evaluate
2. Controller validates the request body
3. Service creates a Transaction domain object
4. DatabaseRuleEngineFactory loads active rule definitions from PostgreSQL
5. Each RuleDefinitionEntity is converted into a DynamicPaymentRule
6. PaymentRuleEngine evaluates all dynamic rules
7. TransactionDecision is created
8. Evaluation result is saved to audit log
9. API returns decision and reasons
```

## Supported Dynamic Rule Fields

```text
amount
currency
country
channel
customerRiskLevel
```

## Supported Operators

```text
GREATER_THAN
LESS_THAN
EQUALS
IN
NOT_IN
```

## Example Dynamic Rules

### High Amount Review

```text
fieldName: amount
operator: GREATER_THAN
ruleValue: 10000
decisionType: REVIEW
```

Meaning:

```text
If transaction amount is greater than 10000, send transaction to review.
```

### Blocked Countries

```text
fieldName: country
operator: IN
ruleValue: RU,KP,IR
decisionType: REJECTED
```

Meaning:

```text
If transaction country is RU, KP, or IR, reject the transaction.
```

### Unsupported Currency

```text
fieldName: currency
operator: NOT_IN
ruleValue: EUR,USD,RSD
decisionType: REJECTED
```

Meaning:

```text
If transaction currency is not EUR, USD, or RSD, reject the transaction.
```

## Rule Validation

Rule definitions are validated before they are saved.

The validator checks:

- supported field names
- valid operator for the selected field
- numeric values for amount rules
- valid enum values for channel rules
- valid enum values for risk level rules

This prevents invalid rule configuration from breaking transaction evaluation later.

Example invalid rule:

```text
fieldName: amount
operator: IN
ruleValue: 10000,20000
```

This is rejected because `amount` does not support the `IN` operator.

## Audit Log

Every transaction evaluation is saved in the `transaction_evaluation_audit` table.

The audit log stores:

```text
transactionId
amount
currency
country
channel
customerRiskLevel
decisionType
reasons
createdAt
```

Audit endpoints allow reading:

```text
all evaluations
evaluations by transaction ID
paged evaluations
filtered evaluations by decision type
```

## Database

The project uses PostgreSQL for the Docker/local production-like setup.

Main tables:

```text
rule_definitions
transaction_evaluation_audit
```

H2 is used for tests and the default local profile.

## API Layer

Main API groups:

### Transaction Evaluation

```text
POST /api/transactions/evaluate
```

### Rule Definitions

```text
POST   /api/rules
GET    /api/rules
GET    /api/rules/active
GET    /api/rules/{id}
PUT    /api/rules/{id}
PATCH  /api/rules/{id}/active
```

### Audit Log

```text
GET /api/audit/evaluations
GET /api/audit/evaluations/{transactionId}
GET /api/audit/evaluations/search
```

## Error Handling

The application uses a global exception handler with `@RestControllerAdvice`.

Examples:

### Validation Error

```json
{
  "error": "Validation Failed",
  "message": "transactionId: Transaction ID cannot be blank"
}
```

### Rule Configuration Error

```json
{
  "error": "Rule Configuration Error",
  "message": "No active rule definitions found"
}
```

### Not Found Error

```json
{
  "error": "Not Found",
  "message": "Rule definition not found with id: 999"
}
```

## Testing Strategy

The project contains several test layers.

### Unit Tests

Used for:

- core rules
- transaction model
- payment rule engine
- dynamic payment rules
- rule validator

### Mockito Tests

Used for:

- `DatabaseRuleEngineFactory`

The repository is mocked so the factory can be tested without a real database.

### MockMvc Tests

Used for:

- transaction evaluation API
- rule CRUD API
- audit API
- validation and error responses

Spring tests use H2 and seed dynamic rules before evaluation tests.

## Docker Architecture

Docker Compose starts:

```text
payguard-postgres
payguard-app
```

The Spring app connects to PostgreSQL using the Docker Compose service name:

```text
jdbc:postgresql://postgres:5432/payguarddb
```

This is different from local IntelliJ execution, where the app connects through:

```text
jdbc:postgresql://localhost:5432/payguarddb
```

## CI Pipeline

GitHub Actions runs the Maven test suite on every push and pull request.

Workflow:

```text
.github/workflows/maven-ci.yml
```

Command:

```bash
mvn clean test
```

## Design Decisions

### Why multi-module Maven?

The core rule engine is separated from the Spring API.

This makes the core logic reusable and easier to test.

### Why database-driven rules?

Database rules allow changing business logic without changing Java code.

Rules can be created, updated, activated, or deactivated through API endpoints.

### Why keep priority on rules?

Priority controls evaluation order.

This is important when several rules can fail and the system needs predictable evaluation behavior.

### Why audit every evaluation?

Payment decisions should be traceable.

Audit logging makes it possible to inspect what decision was made and why.

### Why validate rule definitions?

Invalid rule configuration should fail early.

It is better to reject a bad rule when it is created than to let it break transaction evaluation later.