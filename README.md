# PayGuard Rules

[![PayGuard Maven CI](https://github.com/Milki19/payguard-rules/actions/workflows/maven-ci.yml/badge.svg)](https://github.com/Milki19/payguard-rules/actions/workflows/maven-ci.yml)

PayGuard Rules is a Java/Spring Boot payment decision engine.

It evaluates payment transactions against configurable database-driven rules and returns a final decision:

- `APPROVED`
- `REVIEW`
- `REJECTED`

The project demonstrates clean Java backend design, Maven multi-module structure, REST APIs, PostgreSQL persistence, dynamic rule evaluation, audit logging, Docker Compose, and GitHub Actions CI.

## Tech Stack

- Java 17
- Maven multi-module
- Spring Boot
- Spring Web
- Spring Data JPA
- Bean Validation
- PostgreSQL
- H2 for tests/local default profile
- Docker Compose
- JUnit 5
- Mockito
- MockMvc
- Swagger/OpenAPI
- GitHub Actions

## Project Structure

```text
payguard-rules
├── payguard-core
│   └── Core domain model, rules, and rule engine
│
└── payguard-spring-demo
    └── Spring Boot REST API, database, audit log, dynamic rules
```

## Main Features

### Core Rule Engine

The `payguard-core` module contains the reusable payment rule engine.

It supports:

- fast evaluation, stops at first failed rule
- full evaluation, collects all failed rule reasons
- final decision aggregation with priority:

```text
REJECTED > REVIEW > APPROVED
```

### Dynamic Database Rules

The Spring Boot module stores rule definitions in PostgreSQL.

A rule definition contains:

- name
- rule type
- transaction field
- operator
- rule value
- decision type
- message
- active flag
- priority

Only active rules are used by the dynamic payment engine.

Supported transaction fields:

```text
amount
currency
country
channel
customerRiskLevel
```

Supported operators:

```text
GREATER_THAN
LESS_THAN
EQUALS
IN
NOT_IN
```

### Audit Log

Every transaction evaluation is saved to the audit table with:

- transaction data
- final decision
- decision reasons
- creation timestamp

## Running with Docker Compose

From the project root:

```bash
docker compose up --build
```

Run in background:

```bash
docker compose up --build -d
```

Check containers:

```bash
docker compose ps
```

Expected containers:

```text
payguard-postgres
payguard-app
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

Stop containers:

```bash
docker compose down
```

Remove containers and local PostgreSQL data:

```bash
docker compose down -v
```

## API Usage

### 1. Create a Rule

```http
POST /api/rules
```

Example high amount review rule:

```json
{
  "name": "High amount review",
  "ruleType": "AMOUNT_LIMIT",
  "operator": "GREATER_THAN",
  "fieldName": "amount",
  "ruleValue": "10000",
  "decisionType": "REVIEW",
  "message": "Transaction amount exceeds review limit",
  "active": true,
  "priority": 30
}
```

Example blocked country rule:

```json
{
  "name": "Blocked countries",
  "ruleType": "COUNTRY_BLOCKED",
  "operator": "IN",
  "fieldName": "country",
  "ruleValue": "RU,KP,IR",
  "decisionType": "REJECTED",
  "message": "Transaction country is blocked",
  "active": true,
  "priority": 10
}
```

### 2. List Rules

```http
GET /api/rules
```

List only active rules used by the engine:

```http
GET /api/rules/active
```

Get rule by ID:

```http
GET /api/rules/{id}
```

Update rule:

```http
PUT /api/rules/{id}
```

Activate or deactivate rule:

```http
PATCH /api/rules/{id}/active?active=true
```

### 3. Evaluate a Transaction

```http
POST /api/transactions/evaluate
```

Example request:

```json
{
  "transactionId": "TX-1001",
  "amount": 12500,
  "currency": "EUR",
  "country": "RS",
  "channel": "ONLINE",
  "customerRiskLevel": "LOW"
}
```

Example response:

```json
{
  "decisionType": "REVIEW",
  "reasons": [
    "High amount review: Transaction amount exceeds review limit"
  ]
}
```

If no active rules exist, the API returns:

```json
{
  "error": "Rule Configuration Error",
  "message": "No active rule definitions found"
}
```

### 4. Read Audit Log

```http
GET /api/audit/evaluations
```

Get audit records by transaction ID:

```http
GET /api/audit/evaluations/{transactionId}
```

Search audit records:

```http
GET /api/audit/evaluations/search?page=0&size=10
```

Filter by decision type:

```http
GET /api/audit/evaluations/search?decisionType=REJECTED&page=0&size=10
```

## Local Database Access

Connect to PostgreSQL container:

```bash
docker exec -it payguard-postgres psql -U payguard -d payguarddb
```

Show rule definitions:

```sql
SELECT id, name, rule_type, field_name, operator, rule_value, decision_type, active, priority
FROM rule_definitions
ORDER BY priority ASC;
```

Show audit log:

```sql
SELECT id, transaction_id, decision_type, reasons, created_at
FROM transaction_evaluation_audit
ORDER BY created_at DESC;
```

## Running Tests

From the project root:

```bash
mvn clean test
```

Or run tests from IntelliJ IDEA.

The test suite includes:

- core rule engine unit tests
- dynamic rule unit tests
- Mockito factory tests
- MockMvc API tests
- audit endpoint tests
- rule CRUD tests
- validation tests

## Continuous Integration

GitHub Actions runs the Maven test suite on every push and pull request.

Workflow file:

```text
.github/workflows/maven-ci.yml
```

CI command:

```bash
mvn clean test
```

## Architecture

Detailed architecture documentation is available here:

```text
docs/architecture.md
```

## Why This Project Exists

This project was built as a practical Java backend refresh project focused on:

- clean OOP design
- rule engine architecture
- Spring Boot APIs
- validation and error handling
- database persistence
- test coverage
- Docker-based local setup
- CI pipeline

It shows how a small Java rule engine can evolve into a configurable payment decision platform.
