# Fraud Transaction App

A real-time fraud detection engine built with Spring Boot that analyzes financial transactions and returns risk assessments. The system uses a rule-based approach combined with machine learning (Isolation Forest) to detect potentially fraudulent transactions.

## Features

- **Real-time Transaction Analysis**: Process transactions and get instant fraud risk scores
- **Multiple Fraud Detection Rules**:
  - Blacklist checks (cards, IPs, merchants)
  - Velocity checks (transaction frequency limits)
  - Geo-velocity analysis (impossible travel detection)
  - IP risk scoring
  - Merchant risk scoring
  - Abnormal amount detection
  - First transaction analysis
  - Isolation Forest ML-based anomaly detection
- **Risk Scoring**: Returns a 0-100 risk score with decisions (ALLOW, REVIEW, BLOCK)
- **Rate Limiting**: Per-IP rate limiting to prevent abuse
- **Admin Dashboard**: View flagged transactions and manage blacklists
- **API Key Authentication**: JWT-based authentication with API key management
- **Swagger/OpenAPI Documentation**: Interactive API documentation

## Tech Stack

- **Java 17**
- **Spring Boot 4.0.3**
- **Spring Security** (JWT authentication)
- **Spring Data JPA and JDBC**
- **Spring WebFlux** (reactive client)
- **SQL Server / PostgreSQL** (database)
- **Redis** (caching)
- **Flyway** (database migrations)
- **SMILE ML Library** (Isolation Forest algorithm)
- **Caffeine** (in-memory caching)
- **SpringDoc OpenAPI** (API documentation)
- **Micrometer** (metrics/observability)

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- SQL Server 
- Redis server

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd FraudTransactionApp
```

### 2. Configure Database

Create a database for the application. The default configuration uses SQL Server:

```sql
CREATE DATABASE fraud_db;
```

### 3. Configure Application Properties

Update `src/main/resources/application-dev.properties` with your database credentials:

```properties
# SQL Server Configuration
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=fraud_db;encrypt=true;trustServerCertificate=true
spring.datasource.username=your_username
spring.datasource.password=your_password

# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379

# JWT Configuration
jwt_secret=your_secure_secret_key
jwt_expiration=864000
```

[//]: # (For PostgreSQL, uncomment and configure:)

[//]: # (```properties)

[//]: # (spring.datasource.url=jdbc:postgresql://localhost:5432/fraud_app_db)

[//]: # (spring.datasource.username=postgres)

[//]: # (spring.datasource.password=your_password)

[//]: # (spring.datasource.driver-class-name=org.postgresql.Driver)

[//]: # (spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect)

[//]: # (```)

### 4. Configure Fraud Thresholds (Optional)

Adjust fraud detection thresholds in `application-dev.properties`:

```properties
# Fraud Engine Thresholds
fraud.threshold.block=70        # Score >= 70 blocks the transaction
fraud.threshold.review=30       # Score >= 30 flags for review

# Velocity Limits
fraud.velocity.card.1min=5      # Max 5 transactions per card per minute
fraud.velocity.card.1hour=20    # Max 20 transactions per card per hour
fraud.velocity.card.24hour=100  # Max 100 transactions per card per day
fraud.velocity.ip.1min=10       # Max 10 transactions per IP per minute
fraud.velocity.ip.1hour=50      # Max 50 transactions per IP per hour

# Risk Thresholds
fraud.risk.ip.threshold=50
fraud.risk.merchant.threshold=50
```

### 5. Start Redis

```bash
redis-server
```

### 6. Build and Run

Using Maven wrapper:

```bash
# On Linux/Mac
./mvnw spring-boot:run

# On Windows
mvnw.cmd spring-boot:run
```

Or build and run the JAR:

```bash
./mvnw clean package
java -jar target/FraudTransactionApp-0.0.1-SNAPSHOT.jar
```

The application will start on port **8088** by default.

## API Documentation

Once the application is running, access the Swagger UI at:

```
http://localhost:8088/swagger-ui.html
```

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/create-key` | Create a new API key |
| POST | `/api/v1/auth/login` | Login with API key to get JWT token |
| DELETE | `/api/v1/auth/revoke-key/{id}` | Revoke an API key |

### Fraud Processing

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/fraud/process` | Process a transaction for fraud analysis |

### Admin Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/admin/flagged` | Get all flagged transactions (paginated) |
| GET | `/api/v1/admin/flagged/blocked` | Get blocked transactions |
| GET | `/api/v1/admin/flagged/review` | Get transactions under review |
| GET | `/api/v1/admin/flagged/ip/{ip}` | Get flagged transactions by IP |
| GET | `/api/v1/admin/flagged/card/{cardNo}` | Get flagged transactions by card |
| GET | `/api/v1/admin/flagged/merchant/{merchantId}` | Get flagged transactions by merchant |
| GET | `/api/v1/admin/blacklisted/all` | Get all blacklisted entities |
| GET | `/api/v1/admin/blacklisted/cards` | Get all blacklisted cards |
| GET | `/api/v1/admin/blacklisted/ips` | Get all blacklisted IPs |
| GET | `/api/v1/admin/blacklisted/merchants` | Get all blacklisted merchants |

## Usage Example

### 1. Create an API Key

```bash
curl -X POST "http://localhost:8088/api/v1/auth/create-key?ownerName=MyApp"
```

### 2. Login to Get JWT Token

```bash
curl -X POST "http://localhost:8088/api/v1/auth/login" \
  -H "X-API-KEY: your_api_key"
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

```json
{
  "success": true,
  "message": "Fraud Check Completed",
  "data": {
    "riskScore": 25,
    "decision": "ALLOW",
    "triggeredRules": {
      "VelocityRule": "Card velocity within limits",
      "IpRiskRule": "IP risk score acceptable"
    }
  }
}
```

## Project Structure

```
src/main/java/com/interswitch/fraudtransactionapp/
├── client/                 # External API clients
├── config/                 # Configuration classes (JWT, Security, etc.)
├── controller/             # REST controllers
├── dao/                    # Data access objects
├── dto/                    # Request/Response DTOs
├── exception/              # Custom exceptions
├── fraudEngine/
│   ├── model/              # Fraud rule models
│   └── rule/               # Fraud detection rules
├── model/                  # Domain models
├── repository/             # JPA repositories
├── service/                # Business logic services
└── util/                   # Utility classes and mappers
```

## Health Checks

The application exposes health endpoints via Spring Actuator:

```
http://localhost:8088/actuator/health
http://localhost:8088/actuator/info
http://localhost:8088/actuator/metrics
```

## Running Tests

```bash
./mvnw test
```

## Database Migrations

Flyway handles database migrations automatically on startup. Migration scripts are located in:

```
src/main/resources/db/migration/
```

## Contributing

1. Create a feature branch from `main`
2. Make your changes
3. Write/update tests
4. Submit a pull request

## License

Proprietary - Interswitch
