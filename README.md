# Fraud Transaction App

A real-time fraud detection engine built with Spring Boot that analyzes financial transactions and returns risk assessments. The system combines a rule-based approach with machine learning (Isolation Forest) to detect potentially fraudulent transactions with high accuracy and low latency.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
  - [Local Development](#local-development)
  - [Docker Deployment](#docker-deployment)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
  - [Authentication](#authentication)
  - [Fraud Processing](#fraud-processing)
  - [Admin Operations](#admin-operations)
- [Usage Examples](#usage-examples)
- [Fraud Detection Rules](#fraud-detection-rules)
- [Security](#security)
- [Observability & Monitoring](#observability--monitoring)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Database Migrations](#database-migrations)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

The Fraud Transaction App processes incoming transaction requests and evaluates them against a multi-layered fraud detection engine. Each transaction is scored on a scale of **0-100**, and a decision is returned:

| Decision | Risk Score | Action |
|----------|-----------|--------|
| `ALLOW`  | < 30      | Approve the transaction |
| `REVIEW` | 30 - 69   | Flag for manual review |
| `BLOCK`  | >= 70     | Reject the transaction |

Key capabilities include blacklist verification, velocity checks, geo-velocity analysis, IP/merchant risk scoring, ML-based anomaly detection, and comprehensive admin APIs for audit and blacklist management.

---

## Features

- **Real-time Transaction Analysis** — Sub-second fraud scoring with asynchronous rule execution
- **Multi-layered Rule Engine** — 9+ detection rules working in parallel
- **Machine Learning Anomaly Detection** — Isolation Forest algorithm via the SMILE library
- **Blacklist Management** — Cards, IPs, and merchants with role-based admin APIs
- **Rate Limiting** — Per-IP request throttling to prevent API abuse
- **JWT Authentication** — Secure API key generation and token-based access
- **Role-based Access Control (RBAC)** — Spring Security with method-level authorization
- **External Provider Integration** — Sikker API for enriched IP blacklist data
- **Caching Layer** — Redis for distributed caching and Caffeine for in-memory caching
- **Database Optimization** — SQL Server stored procedures for high-performance velocity checks
- **Observability** — Prometheus metrics, Zipkin distributed tracing, and custom health indicators
- **Interactive API Docs** — Swagger/OpenAPI UI for easy exploration
- **Database Versioning** — Flyway migrations for schema evolution

---

## Tech Stack

| Category | Technology |
|----------|------------|
| Language | Java 25 |
| Framework | Spring Boot 4.0.3 |
| Web | Spring MVC + Spring WebFlux (reactive client) |
| Security | Spring Security + JWT (JJWT 0.12.6) |
| Data Access | Spring Data JPA + Spring JDBC |
| Database | SQL Server 2022 / PostgreSQL (runtime) |
| Migrations | Flyway 9.22.3 |
| Caching | Redis 7 + Caffeine |
| ML | SMILE Core 5.2.1 (Isolation Forest) |
| API Docs | SpringDoc OpenAPI 3.0.2 |
| Observability | Micrometer + Prometheus Registry + Zipkin Brave |
| Build Tool | Maven 3.6+ |
| Containerization | Docker + Docker Compose |

---

## Architecture

```
┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐
│   API Client    │────▶│  Fraud App (8088)│────▶│  SQL Server     │
│                 │     │                  │     │  (Primary DB)   │
└─────────────────┘     │  ┌────────────┐  │     └─────────────────┘
                        │  │ JWT Filter │  │              │
                        │  └────────────┘  │     ┌─────────────────┐
                        │  ┌────────────┐  │────▶│  Flyway         │
                        │  │Rate Limiter│  │     │  (Migrations)   │
                        │  └────────────┘  │     └─────────────────┘
                        │  ┌────────────┐  │
                        │  │Fraud Engine│  │     ┌─────────────────┐
                        │  │(Rules + ML)│  │────▶│  Redis          │
                        │  └────────────┘  │     │  (Cache/Rate)   │
                        │                  │     └─────────────────┘
                        │  ┌────────────┐  │
                        │  │Sikker API  │  │     ┌─────────────────┐
                        │  │Client      │──│────▶│  Prometheus     │
                        │  └────────────┘  │     │  / Zipkin       │
                        └──────────────────┘     └─────────────────┘
```

1. **Incoming Request** → Rate limiter validates IP.
2. **Authentication** → JWT token validated via `Authorization: Bearer <token>`.
3. **Fraud Engine** → Rules execute in parallel using `AsyncFraudConfig`.
4. **Data Layer** → JPA repositories, JDBC DAOs, and stored procedures query SQL Server.
5. **Caching** → Redis caches blacklist and risk data; Caffeine provides L1 in-memory cache.
6. **Response** → `FraudDecision` with `riskScore`, `decision`, and `triggeredRules`.

---

## Prerequisites

- **Java 25** or higher
- **Maven 3.6+** (or use the provided wrapper `./mvnw`)
- **SQL Server 2022** (or PostgreSQL)
- **Redis 7+**
- **Docker & Docker Compose** (optional, for containerized setup)

---

## Getting Started

### Local Development

#### 1. Clone the Repository


```bash
git clone <repository-url>
cd FraudTransactionApp
```

#### 2. Start Infrastructure

**Option A: Docker Compose (recommended for local dev)**

```bash
# Create a .env file
echo "SA_PASSWORD=YourStrong@Passw0rd" > .env
echo "REDIS_PORT=6379" >> .env

# Start only SQL Server and Redis
docker-compose up -d sqlserver redis
```

**Option B: Manual Setup**

- Install and start SQL Server 2022.
- Install and start Redis 7 (`redis-server`).

#### 3. Configure Application

The committed `application.properties` contains **no hardcoded secrets** — only placeholder references (e.g., `${DB_URL}`) and non-sensitive defaults. 
All secret and environment-specific values are provided via **`application-dev.properties`**, which is **gitignored** 


Create or update `src/main/resources/application-dev.properties` with your local credentials:

```properties
# Server
server.port=${SERVER_PORT}

# Database
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=${DB_DRIVER}

#REDIS
spring.data.redis.host=${REDIS_HOST}
spring.data.redis.port=${REDIS_PORT}

#SIKKARI

sikkari.api.key=${SIKKARI_API_KEY}
sikkari.base.url=${SIKKARI_URL}

# JWT
jwt_secret=your-super-secure-jwt-secret-key-min-32-characters
jwt_expiration=your-expiration

# Connection Pool
spring.datasource.hikari.minimum-idle=${DB_POOL_MIN_IDLE}
spring.datasource.hikari.maximum-pool-size=${DB_POOL_MAX_SIZE}
spring.datasource.hikari.idle-timeout=${DB_POOL_IDLE_TIMEOUT}
spring.datasource.hikari.connection-timeout=${DB_POOL_CONNECTION_TIMEOUT}
spring.datasource.hikari.max-lifetime=${DB_POOL_MAX_LIFETIME}
spring.datasource.hikari.pool-name=${DB_POOL_NAME}
```

> **Note:** `application.properties` uses placeholders (e.g., `${DB_URL}`) that resolve from `application-dev.properties` or from real environment variables. This keeps secrets out of source control and makes the app ready for containerized and cloud deployments.
>
> **Security:** `application-dev.properties` is already listed in `.gitignore`. Never commit it to GitHub. For production, pass values as OS environment variables or use a secrets manager instead of `application-dev.properties`.


#### 4. Build and Run

```bash
# Linux / Mac
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

The application will start on port **8088** by default.

Access Swagger UI at: [http://localhost:8088/swagger-ui.html](http://localhost:8088/swagger-ui.html)

---

### Docker Deployment

Build and run the entire stack using Docker Compose. Secrets are injected via a `.env` file which is **gitignored** so sensitive data is never committed:

```bash
# Create environment file (not tracked by git)
echo "SA_PASSWORD=YourStrong@Passw0rd" > .env
echo "REDIS_PORT=6379" >> .env

# Build and start all services
docker-compose up --build -d
```

Services will be available at:

| Service | URL |
|---------|-----|
| Fraud App | `http://localhost:8089` |
| SQL Server | `localhost:1433` |
| Redis | `localhost:6379` |

> **Note:** The Dockerfile exposes port `8086`, while `docker-compose.yml` maps host port `8089` to the container.

---

## Configuration

### Fraud Engine Thresholds

Configure in `application-dev.properties`:

```properties
# Decision thresholds
fraud.threshold.block=70   
fraud.threshold.review=30    # Review transactions with score >= 30

# Velocity limits
fraud.velocity.card.1min=5    # Max 5 transactions per card per minute
fraud.velocity.card.1hour=20  # Max 20 transactions per card per hour
fraud.velocity.card.24hour=100 # Max 100 transactions per card per day
fraud.velocity.ip.1min=10     # Max 10 transactions per IP per minute
fraud.velocity.ip.1hour=50    # Max 50 transactions per IP per hour

# Risk thresholds
fraud.risk.ip.threshold=50
fraud.risk.merchant.threshold=50


```

### Observability

```properties
# Actuator / Prometheus
ACTUATOR_EXPOSED_ENDPOINTS=health,info,metrics,prometheus
ACTUATOR_HEALTH_SHOW_DETAILS=always
ACTUATOR_HEALTH_SHOW_COMPONENTS=always
ACTUATOR_HEALTH_DEFAULTS_ENABLED=true

# Zipkin tracing
TRACING_SAMPLING_PROBABILITY=1.0
ZIPKIN_ENDPOINT=http://localhost:9411/api/v2/spans
TRACING_ENABLED=true
```

### Logging

Application logs are written to `logs/fraud-app.log` with a rolling policy:

```properties
LOG_FILE_NAME=logs/fraud-app.log
LOG_FILE_PATH=logs
LOG_MAX_FILE_SIZE=10MB
LOG_MAX_HISTORY=30
LOG_TOTAL_SIZE_CAP=500MB
LOG_PATTERN_FILE=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

---

## API Documentation

### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/api/v1/auth/create-key` | Generate a new API key | No |
| `POST` | `/api/v1/auth/login` | Exchange API key for JWT token | API Key Header |
| `DELETE` | `/api/auth/revoke-key/{id}` | Revoke an existing API key | JWT |

### Fraud Processing

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/api/v1/fraud/process` | Analyze transaction for fraud | JWT |
| `GET` | `/api/v1/fraud/trace-check` | Simple tracing test endpoint | No |

### Admin Operations

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `GET` | `/api/v1/admin/flagged` | Get all flagged transactions (paginated) | JWT + `VIEW_FLAGGED_TRANSACTIONS` |
| `GET` | `/api/v1/admin/flagged/blocked` | Get blocked transactions | JWT + `VIEW_FLAGGED_TRANSACTIONS` |
| `GET` | `/api/v1/admin/flagged/review` | Get transactions under review | JWT + `VIEW_FLAGGED_TRANSACTIONS` |
| `GET` | `/api/v1/admin/flagged/ip/{ip}` | Get flagged transactions by IP | JWT + `VIEW_FLAGGED_TRANSACTIONS` |
| `GET` | `/api/v1/admin/flagged/card/{cardNo}` | Get flagged transactions by card | JWT + `VIEW_FLAGGED_TRANSACTIONS` |
| `GET` | `/api/v1/admin/flagged/merchant/{merchantId}` | Get flagged transactions by merchant | JWT + `VIEW_FLAGGED_TRANSACTIONS` |
| `GET` | `/api/v1/admin/blacklisted/all` | Get all blacklisted entities | JWT + `GET_ALL_BLACKLISTED` |
| `GET` | `/api/v1/admin/blacklisted/cards` | Get all blacklisted cards | JWT + `GET_ALL_BLACKLISTED` |
| `GET` | `/api/v1/admin/blacklisted/cards/{cardNo}` | Get a specific blacklisted card | JWT + `GET_ALL_BLACKLISTED` |
| `GET` | `/api/v1/admin/blacklisted/ips` | Get all blacklisted IPs | JWT + `GET_ALL_BLACKLISTED` |
| `GET` | `/api/v1/admin/blacklisted/ips/{ip}` | Get a specific blacklisted IP | JWT + `GET_ALL_BLACKLISTED` |
| `GET` | `/api/v1/admin/blacklisted/merchants` | Get all blacklisted merchants | JWT + `GET_ALL_BLACKLISTED` |
| `GET` | `/api/v1/admin/blacklisted/merchants/{merchantId}` | Get a specific blacklisted merchant | JWT + `GET_ALL_BLACKLISTED` |

> **Tip:** Explore all endpoints interactively via Swagger UI at `/swagger-ui.html`.

---

## Usage Examples


### 1. Create an API Key

```bash
curl -X POST "http://localhost:8088/api/v1/auth/create-key?ownerName=MyApp"
```

**Response:**
```json
{
  "success": true,
  "message": "Created successfully",
  "data": "fraud-api-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
}
```

### 2. Login to Get JWT Token

```bash
curl -X POST "http://localhost:8088/api/v1/auth/login" \

  -H "X-API-KEY: fraud-api-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful!",
  "data": "eyJhbGciOiJIUzI1NiIs..."
}

```

### 3. Process a Transaction

```bash
curl -X POST "http://localhost:8088/api/v1/fraud/process" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your_jwt_token" \
  -d '{
    "cardNo": "4111111111111111",
    "amount": 1000.00,
    "merchantId": "MERCHANT_001",
    "transactionTime": "2024-01-15T10:30:00Z",
    "channelType": "WEB",
    "ipAddress": "192.168.1.100",
    "deviceFingerprint": "fp_abc123",
    "userAgent": "Mozilla/5.0",
    "latitude": 6.5244,
    "longitude": 3.3792,
    "countryCode": "NG",
    "currency": "NGN"
  }'
```

### Sample Response
>>>>>>> 925e42ed252517df7dd81d6059c0479ca3347347

```json
{
  "success": true,
  "message": "Fraud Check Completed",
  "data": {
    "riskScore": 25,
    "decision": "ALLOW",
    "triggeredRules": {
      "VelocityRule": "Card velocity within limits",
      "IpRiskRule": "IP risk score acceptable",
      "MerchantRiskRule": "Merchant risk score acceptable",
      "IsolationForestFraudRule": "Transaction is normal"

    }
  }
}
```

### 4. Get Blocked Transactions (Admin)

```bash
curl -X GET "http://localhost:8088/api/v1/admin/flagged/blocked" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..."
```

### 5. Get All Blacklisted Data (Admin)

```bash
curl -X GET "http://localhost:8088/api/v1/admin/blacklisted/all" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..."
```

---

## Fraud Detection Rules

The fraud engine evaluates each transaction against the following rules. Results are aggregated into a final risk score.

| Rule | Description | Score Impact |
|------|-------------|--------------|
| **BlacklistRule** | Checks if card, IP, or merchant exists in local blacklist | High (Block) |
| **VelocityRule** | Enforces transaction frequency limits per card and IP | Medium |
| **GeoVelocityRule** | Detects impossible travel (e.g., Lagos to London in 5 mins) | High |
| **IpRiskRule** | Evaluates IP reputation from internal risk database | Low-Medium |
| **MerchantRiskRule** | Evaluates merchant reputation from internal risk database | Low-Medium |
| **AbnormalAmountRule** | Flags transactions with statistically abnormal amounts | Medium |
| **FirstTransactionRule** | Elevates risk for first-time card/merchant combinations | Low |
| **IsolationForestFraudRule** | ML anomaly detection using Isolation Forest | Medium-High |
| **BlockingRule** | Final enforcement rule that applies hard thresholds | Deterministic |

Rules are executed asynchronously using a dedicated thread pool (`AsyncFraudConfig`) for optimal throughput.

---

## Security

- **API Key Authentication** — Clients must first create an API key and exchange it for a JWT token.
- **JWT Tokens** — Short-lived tokens signed with HS256. Configurable expiration via `jwt_expiration`.
- **Rate Limiting** — Per-IP rate limiting prevents brute force and abuse. Returns HTTP 429 when exceeded.
- **RBAC** — Endpoints are secured with `@PreAuthorize` using authorities like `VIEW_FLAGGED_TRANSACTIONS` and `GET_ALL_BLACKLISTED`.
- **Input Validation** — Jakarta Bean Validation (`@Valid`) on all request DTOs.
- **Non-root Docker User** — Container runs as `appuser` for reduced attack surface.

---

## Observability & Monitoring

### Health Endpoints

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Overall health status |
| `/actuator/health/db` | Database connectivity |
| `/actuator/health/fraudService` | Fraud service availability |
| `/actuator/health/rateLimiter` | Rate limiter status |
| `/actuator/info` | Application build info |
| `/actuator/metrics` | JVM and application metrics |
| `/actuator/prometheus` | Prometheus scrape endpoint |

### Distributed Tracing

Zipkin integration provides distributed trace IDs across requests. Trace and span IDs are automatically injected into log output:

```
INFO [FraudTransactionApp,traceId,spanId] - Processing transaction...
```

### Prometheus

A sample `prometheus.yml` is included in the project root. Point Prometheus to `http://localhost:8088/actuator/prometheus` to scrape metrics.

---

## Testing

Run the full test suite:

```bash
# Linux / Mac
./mvnw test

# Windows
mvnw.cmd test
```

### Test Coverage

| Test Class | Description |
|------------|-------------|
| `FraudTransactionAppApplicationTests` | Spring context load test |
| `RateLimiterTest` | Rate limiting functionality |
| `AbnormalAmountRuleTest` | Abnormal amount rule logic |
| `IsolationForestFraudRuleTest` | ML anomaly detection logic |
| `BlacklistServiceImplTest` | Blacklist service operations |
| `FraudServiceImplTest` | Core fraud processing logic |

---

## Project Structure

```
FraudTransactionApp/
├── docker-compose.yml              # Full stack orchestration
├── Dockerfile                      # Multi-stage app build
├── prometheus.yml                  # Prometheus scrape config
├── pom.xml                         # Maven build configuration
├── src/
│   ├── main/
│   │   ├── java/com/interswitch/fraudtransactionapp/
│   │   │   ├── client/             # External API clients (Sikker)
│   │   │   ├── config/             # Security, JWT, Redis, RateLimiter, Async, Observability
│   │   │   ├── controller/         # REST controllers (Auth, Fraud, Admin)
│   │   │   ├── dao/                # Data Access Objects (JDBC)
│   │   │   ├── dto/                # Request & Response DTOs
│   │   │   ├── exception/          # Global exception handling
│   │   │   ├── fraudEngine/        # Core fraud detection engine
│   │   │   │   ├── model/          # Rule context & result models
│   │   │   │   └── rule/           # Individual fraud rules
│   │   │   ├── health/             # Custom Actuator health indicators
│   │   │   ├── model/              # JPA entity models
│   │   │   ├── repository/         # Spring Data JPA & custom repositories
│   │   │   ├── service/            # Business logic & service implementations
│   │   │   └── util/               # Mappers, helpers, normalizers
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── db/migration/       # Flyway SQL migrations (V1-V29)
│   └── test/                       # Unit & integration tests
└── logs/                           # Application log files
```

---

## Database Migrations

Flyway manages schema versioning automatically on startup. Migrations are located in:


```
src/main/resources/db/migration/
```

Notable migrations:

| Version | Description |
|---------|-------------|
| V1 | Create fraud events table |
| V2-V4 | Create IP risks, blacklisted cards, merchant risks tables |
| V5, V18, V19 | Stored procedures for velocity and abnormal amount checks |
| V9 | Create API key table |
| V16-V17 | Provider blacklisted IP integration tables |
| V27-V29 | Performance indexes and procedure optimizations |

To trigger a Flyway repair manually, the project includes a `FlywayRepairTool` configuration bean.

---

## Troubleshooting

### Application won't start

- Ensure SQL Server and Redis are running.
- Verify database credentials in `application-dev.properties`.
- Check that the `fraud_db` database exists.

### Rate limited (429)

- The default limit is 10 requests per IP per minute for the fraud process endpoint.
- Wait 60 seconds or adjust `fraud.velocity.ip.1min` in properties.

### JWT token expired

- Tokens expire based on `jwt_expiration` (default: 864000 ms ≈ 10 days).
- Re-authenticate via `/api/v1/auth/login`.

### Docker port conflicts

- Change the host port mapping in `docker-compose.yml`:
  ```yaml
  ports:
    - "9090:8089"  # Map host 9090 to container 8089
  ```

### Flyway migration errors

- Run the application with `spring.flyway.baseline-on-migrate=true` once if starting from an existing schema.
- Use the `FlywayRepairTool` bean or execute `flyway repair` via CLI.

---

## Contributing

1. Fork the repository and create a feature branch from `main`.
2. Follow existing code style (Lombok for boilerplate, constructor injection via `@RequiredArgsConstructor`).
3. Add or update tests for any new functionality.
4. Ensure all tests pass (`./mvnw test`).
5. Update this README if you change public APIs or configuration.
6. Submit a pull request with a clear description.

---

## License

Proprietary — Tijani_Salami

> Unauthorized copying, distribution, or use of this software is strictly prohibited without prior written consent.

