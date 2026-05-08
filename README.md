# Stripe API Automation Framework

Production-grade API test automation framework for Stripe's payment APIs.

## Tech Stack

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 17 | Programming language |
| TestNG | 7.9 | Test framework — DataProviders, parallel, groups |
| RestAssured | 5.4 | HTTP client for API testing |
| Jackson | 2.17 | JSON to POJO deserialization |
| Lombok | 1.18 | Reduces boilerplate code |
| AssertJ | 3.25 | Readable fluent assertions |
| ExtentReports | 5.1 | HTML test reports |
| Logback | 1.2 | Logging framework |

## Architecture

```text
Tests → Services → BaseService → RequestSpec → ConfigManager
```

| Layer | Files | Responsibility |
|-------|-------|---------------|
| Config | ConfigManager.java | Loads API key, base URL from property files |
| Constants | Endpoints.java, StatusCodes.java | All URL paths and HTTP codes in one place |
| Models | Customer.java, Address.java, ErrorResponse.java, ListResponse.java | Lightweight response POJOs |
| Services | BaseService.java, CustomerService.java | HTTP methods and API-specific operations |
| Builders | CustomerBuilder.java | Fluent builder pattern for test data |
| Specs | RequestSpec.java, ResponseSpec.java | Reusable request/response configurations |
| Utils | DataHelper.java, RetryAnalyzer.java, ExtentReportManager.java | Helpers and reporting |
| Tests | CustomerCRUDFlowTest, CreateCustomerTest, UpdateCustomerTest, DeleteCustomerTest | Test classes organized by API |

## Project Structure

```text
src/main/java/com/stripe/automation/
├── config/          ConfigManager
├── constants/       Endpoints, StatusCodes
├── models/          Customer, Address, ErrorResponse, ListResponse
├── services/        BaseService, CustomerService
├── builders/        CustomerBuilder
├── spec/            RequestSpec, ResponseSpec
└── utils/           DataHelper, RetryAnalyzer, ExtentReportManager, ExtentTestListener

src/test/java/com/stripe/automation/
├── base/            BaseTest
└── customer/        CustomerCRUDFlowTest, CreateCustomerTest, UpdateCustomerTest, DeleteCustomerTest

src/test/resources/
├── config/          dev.properties.example
├── allure.properties
└── logback.xml

Root files:
├── pom.xml
├── testng-smoke.xml
├── testng-regression.xml
└── README.md
```

## Setup

### Prerequisites

- Java 17 or higher
- Maven 3.8 or higher
- Stripe account with test API key

### Installation

```bash
git clone https://github.com/seelamy/stripe-api-testautomation.git
cd stripe-api-testautomation
cp src/test/resources/config/dev.properties.example src/test/resources/config/dev.properties
```

Add your Stripe test secret key in `dev.properties`:

```text
base.url=https://api.stripe.com
api.key=sk_test_YOUR_KEY_HERE
api.version=2024-06-20
```

Install dependencies:

```bash
mvn clean install -DskipTests
```

## How to Run

| Command | What It Does |
|---------|-------------|
| `mvn test` | Runs regression suite (default, parallel with 5 threads) |
| `mvn test -Psmoke` | Runs smoke suite (CRUD flow only) |
| `mvn test -Pregression -DthreadCount=10` | Regression with 10 parallel threads |
| `mvn test -Dtest=CreateCustomerTest` | Runs specific test class |
| `mvn test -Dtest=CreateCustomerTest#shouldCreateCustomerWithAllFields` | Runs specific test method |
| `mvn test -Dparallel=none` | Runs sequentially (no parallel) |
| `mvn test -Denv=staging` | Runs against staging environment |

## Test Reports

Reports are auto-generated after each run:

| Suite | Report Location |
|-------|----------------|
| Smoke | `test-reports/smoke-report.html` |
| Regression | `test-reports/regression-report.html` |

Open the HTML file in any browser. Reports include:
- Pass/fail/skip dashboard
- Tests grouped by class
- Full API request details (method, endpoint, params)
- Full API response (status code, JSON body)
- Failure details with stack trace

## Test Coverage

### Smoke Suite (1 test)

| Test | Flow |
|------|------|
| Complete CRUD lifecycle | Create → Retrieve → Update → Retrieve → Delete → Retrieve (soft delete) |

### Regression Suite (18 tests)

**Create Customer (10 tests)**

| # | Test Case | Type |
|---|-----------|------|
| TC-01 | Create with all valid fields, verify via GET | Positive |
| TC-02 | Create with only email, others null | Positive |
| TC-03 | Create with empty body | Positive |
| TC-04 | Create with special characters in name | Positive |
| TC-05 | Create with duplicate email, different IDs | Positive |
| TC-06 | Create with 50 metadata keys (max limit) | Boundary |
| TC-07 | Create with invalid country code (accepted by Stripe) | Boundary |
| TC-08 | Create with metadata key exceeding 40 chars | Negative |
| TC-09 | Create without authentication | Security |
| TC-10 | Create with invalid API key | Security |

**Update Customer (4 tests)**

| # | Test Case | Type |
|---|-----------|------|
| 1 | Update single field, others unchanged | Positive |
| 2 | Metadata merge — new keys added, old kept | Positive |
| 3 | Delete metadata key with empty value | Positive |
| 4 | Update non-existent customer | Negative |

**Delete Customer (3 tests)**

| # | Test Case | Type |
|---|-----------|------|
| 1 | Delete existing customer, verify deleted flag | Positive |
| 2 | Double delete, second also returns 200 | Positive |
| 3 | Delete non-existent customer | Negative |

### Status Codes Covered

| Code | Description | Where |
|------|-------------|-------|
| 200 | Success | All positive tests |
| 400 | Bad request | Create — boundary violations |
| 401 | Unauthorized | Create — auth tests |
| 404 | Not found | Update, Delete — non-existent customer |

## Design Decisions

**Why lightweight POJOs?**
Stripe returns 40-80 fields per resource. We model only 10-15 fields we test. `@JsonIgnoreProperties(ignoreUnknown = true)` ignores the rest. New fields don't break existing tests.

**Why Maps for requests?**
Stripe uses `x-www-form-urlencoded` with bracket notation (`metadata[key]`, `address[city]`). Maps with Builder pattern handle this cleanly across all APIs.

**Why BaseService?**
All Stripe APIs use the same HTTP methods. BaseService implements post, get, delete once. Adding a new API = one small service class extending BaseService.

**Why ExtentReports?**
Single HTML file. No CLI tools, no server. Open in any browser. Shows full API request/response for every test.

## Adding a New API

To add PaymentIntent (or any Stripe API):

1. Add endpoint in `Endpoints.java` (1 line)
2. Create `PaymentIntentService.java` extending BaseService (5-6 methods)
3. Create `PaymentIntent.java` POJO (10-15 fields)
4. Create `PaymentIntentBuilder.java` (fluent builder)
5. Create test classes in `com.stripe.automation.paymentintent`
6. Add to `testng-regression.xml` and `testng-smoke.xml`

No existing files change.

## Upcoming APIs

- [ ] PaymentIntent — create, confirm, capture, cancel, decline codes
- [ ] PaymentMethod — attach, detach, list
- [ ] Refund — full, partial
- [ ] Subscription — create, cancel, pause
- [ ] Invoice — create, finalize, pay
- [ ] Dispute — evidence submission