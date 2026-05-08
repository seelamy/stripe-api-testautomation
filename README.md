# Stripe API Automation Framework

Production-grade API test automation framework for Stripe's payment APIs built with Java, TestNG, and RestAssured.

## Tech Stack

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 17 | Programming language |
| TestNG | 7.9 | Test framework with DataProviders, parallel execution, groups |
| RestAssured | 5.4 | HTTP client for API testing |
| Jackson | 2.17 | JSON to POJO deserialization |
| Lombok | 1.18 | Reduces boilerplate (getters, setters) |
| AssertJ | 3.25 | Readable fluent assertions |
| ExtentReports | 5.1 | HTML test reports with API request/response details |
| Logback | 1.2 | Logging framework |

## Architecture

| Layer | Responsibility |
|-------|---------------|
| Config | Environment-aware configuration with property files |
| Constants | API endpoints and HTTP status codes in one place |
| Spec | Reusable request and response specifications |
| Service | BaseService with common HTTP methods, API-specific services |
| Model | Lightweight POJOs with Jackson for response deserialization |
| Builder | Fluent builder pattern for test data creation |
| Utils | Reporting, retry logic, random data generators, ID validators |
| Test | Organized by API with smoke and regression suites |

## Project Structure
stripe-api-automation/
├── pom.xml
├── testng-smoke.xml                     — smoke suite (CRUD flows)
├── testng-regression.xml                — full regression suite
├── README.md
├── .gitignore
│
├── src/main/java/com/stripe/automation/
│   ├── config/
│   │   └── ConfigManager.java           — loads environment config
│   ├── constants/
│   │   ├── Endpoints.java               — all API URL paths
│   │   └── StatusCodes.java             — HTTP status code constants
│   ├── models/
│   │   ├── Customer.java                — response POJO
│   │   ├── Address.java                 — nested address POJO
│   │   ├── ListResponse.java            — generic list response (works for all APIs)
│   │   └── ErrorResponse.java           — error response (shared across all APIs)
│   ├── services/
│   │   ├── BaseService.java             — common HTTP methods (post, get, delete)
│   │   └── CustomerService.java         — customer API operations
│   ├── builders/
│   │   └── CustomerBuilder.java         — fluent test data builder
│   ├── spec/
│   │   ├── RequestSpec.java             — base request specification (auth, headers)
│   │   └── ResponseSpec.java            — reusable response validations
│   └── utils/
│       ├── DataHelper.java              — random data generators + ID validators
│       ├── RetryAnalyzer.java           — retries flaky tests (max 2 retries)
│       ├── ExtentReportManager.java     — manages ExtentReports instance
│       └── ExtentTestListener.java      — captures test start/pass/fail/skip
│
├── src/test/java/com/stripe/automation/
│   ├── base/
│   │   └── BaseTest.java                — setup, teardown, cleanup, logging
│   └── customer/
│       ├── CustomerCRUDFlowTest.java    — smoke: Create → Get → Update → Get → Delete → Get
│       ├── CreateCustomerTest.java      — positive + negative create scenarios
│       ├── UpdateCustomerTest.java      — update edge cases
│       └── DeleteCustomerTest.java      — delete edge cases
│
├── src/test/resources/
│   ├── config/
│   │   └── dev.properties.example       — config template (copy and add your key)
│   ├── allure.properties
│   └── logback.xml                      — logging configuration
│
└── test-reports/                         — auto-generated after test run
├── smoke-report.html
└── regression-report.html

## Setup

### Prerequisites
- Java 17 or higher
- Maven 3.8 or higher
- Stripe account with test API key

### Installation

1. Clone the repository
```bash
git clone https://github.com/seelamy/stripe-api-testautomation.git
cd stripe-api-testautomation
```

2. Create config file
```bash
cp src/test/resources/config/dev.properties.example src/test/resources/config/dev.properties
```

3. Add your Stripe test secret key in dev.properties
   base.url=https://api.stripe.com
   api.key=sk_test_YOUR_KEY_HERE
   api.version=2024-06-20
4. Install dependencies
```bash
mvn clean install -DskipTests
```

## How to Run Tests

### Regression — all tests with parallel execution (default)
```bash
mvn test
```

### Smoke — CRUD flow only
```bash
mvn test -Psmoke
```

### Custom thread count
```bash
mvn test -Pregression -DthreadCount=10
```

### Specific test class
```bash
mvn test -Dtest=CreateCustomerTest
```

### Specific test method
```bash
mvn test -Dtest=CreateCustomerTest#shouldCreateCustomerWithAllFields
```

### Multiple test classes
```bash
mvn test -Dtest=CustomerCRUDFlowTest,CreateCustomerTest
```

### Sequential execution (no parallel)
```bash
mvn test -Dparallel=none
```

### Clean build then run
```bash
mvn clean test -Psmoke
```

## Test Reports

Reports are auto-generated in the test-reports/ folder after each run.

| Suite | Report File | How to View |
|-------|------------|-------------|
| Smoke | test-reports/smoke-report.html | Open in browser |
| Regression | test-reports/regression-report.html | Open in browser |

Reports include:
- Dashboard with total/passed/failed/skipped counts
- Tests grouped by class (CustomerCRUDFlowTest, CreateCustomerTest, etc.)
- Full API request details (HTTP method, endpoint, parameters)
- Full API response details (status code, JSON body)
- Failure details with assertion error and filtered stack trace
- System info (framework, environment, Java version)

## Test Coverage

### Customer API — Smoke (1 test)

| Test | Flow |
|------|------|
| shouldCompleteCustomerCRUDLifecycle | Create → Retrieve → Update → Retrieve → Delete → Retrieve (soft delete) |

### Customer API — Create (10 tests)

| Test | Type | What It Validates |
|------|------|-------------------|
| TC-01: Create with all valid fields | Positive | All fields saved and returned correctly, verified via GET |
| TC-02: Create with only email | Positive | Other fields are null, metadata is empty |
| TC-03: Create with empty body | Positive | Customer created with just an auto-generated ID |
| TC-04: Create with special characters | Positive | Accents and hyphens in name not corrupted |
| TC-05: Create with duplicate email | Positive | Two different customer IDs created for same email |
| TC-06: Create with 50 metadata keys | Boundary | Maximum metadata limit accepted |
| TC-07: Create with invalid country code | Boundary | Stripe accepts without validation (documented behavior) |
| TC-08: Create with long metadata key | Negative | 400 error for key exceeding 40 chars |
| TC-09: Create without authentication | Security | 401 unauthorized |
| TC-10: Create with invalid API key | Security | 401 unauthorized |

### Customer API — Update (4 tests)

| Test | Type | What It Validates |
|------|------|-------------------|
| Update single field | Positive | Only sent field changes, others unchanged |
| Metadata merge | Positive | New keys added, old keys preserved |
| Delete metadata key | Positive | Empty value removes the key |
| Update non-existent customer | Negative | 404 not found |

### Customer API — Delete (3 tests)

| Test | Type | What It Validates |
|------|------|-------------------|
| Delete existing customer | Positive | 200 with deleted:true flag |
| Double delete | Positive | Second delete also returns 200 (idempotent) |
| Delete non-existent customer | Negative | 404 not found |

### Status Codes Covered

| Code | Description | Tested In |
|------|-------------|-----------|
| 200 | Success | All positive tests |
| 400 | Bad request | Create — invalid inputs, boundary violations |
| 401 | Unauthorized | Create — missing auth, invalid API key |
| 404 | Not found | Update and Delete — non-existent customer |

## Design Decisions

### Why lightweight POJOs instead of full models?
Stripe responses have 40-80 fields per resource. We model only 10-15 fields we actually validate. @JsonIgnoreProperties(ignoreUnknown = true) handles the rest. When Stripe adds new fields, nothing breaks. When we need to test a new field, we add one line to the POJO.

### Why Maps for request data instead of request POJOs?
Stripe expects x-www-form-urlencoded format with bracket notation (metadata[key], address[city]). Maps with the Builder pattern handle this cleanly. One pattern works for all APIs — Customer, PaymentIntent, Subscription.

### Why BaseService pattern?
Every Stripe API uses the same HTTP methods (POST for create/update, GET for retrieve/list, DELETE for delete). BaseService implements these once. Each API service extends it with 5-6 one-line methods. Adding a new API takes 10 minutes.

### Why ExtentReports?
Single HTML file output — no CLI tools needed, no server required. Open in any browser. Shows full API request/response details for every test. Easy to share with team or attach to CI/CD builds.

### Why both console logging and report logging?
Console logs (Logback) give real-time feedback during test execution. Report logs (ExtentReports) give after-run analysis with formatted API details. Both capture the same information in different formats for different purposes.

## Adding a New API

Adding PaymentIntent API (or any new Stripe API) requires these steps:

1. Add endpoint in Endpoints.java — one line
2. Create PaymentIntentService.java — extends BaseService, 5-6 methods
3. Create PaymentIntent.java — lightweight POJO, 10-15 fields
4. Create PaymentIntentBuilder.java — fluent builder
5. Create test classes in com.stripe.automation.paymentintent package
6. Add classes to testng-regression.xml
7. Add flow test to testng-smoke.xml

No existing files need to change. The framework scales to any number of APIs.

## Environment Switching

```bash
# Uses dev.properties (default)
mvn test

# Uses staging.properties
mvn test -Denv=staging
```

Create src/test/resources/config/staging.properties with staging API keys to test against a different environment.

## Upcoming APIs

- [ ] PaymentIntent — create, confirm, capture, cancel, decline codes, 3DS
- [ ] PaymentMethod — attach, detach, list
- [ ] Refund — full refund, partial refund
- [ ] Subscription — create, cancel, pause, dunning
- [ ] Invoice — create, finalize, pay
- [ ] Dispute — evidence submission
