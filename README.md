# Stripe API Automation Framework

Automated API testing framework for Stripe's payment APIs.

## Tech Stack
- Java 17
- TestNG 7.9
- RestAssured 5.4
- Allure Reports 2.25
- Jackson
- Lombok
- AssertJ

## Setup
1. Clone the repo
2. Copy `src/test/resources/config/dev.properties.example` to `dev.properties`
3. Add your Stripe test secret key in `dev.properties`

## How to Run
```bash
mvn test
```