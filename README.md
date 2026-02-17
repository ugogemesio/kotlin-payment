# Microservices Project - Order & Payment Processing

A Spring Boot microservices application demonstrating asynchronous event-driven communication using Apache Kafka. This project consists of two independent services: **Order Service** (Producer) and **Payment Service** (Consumer).
<img width="784" height="228" alt="image" src="https://github.com/user-attachments/assets/b28eb5de-b4ac-49f6-b9a2-4d0e9f5df998" />

## Table of Contents

- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Services Description](#services-description)
- [Setup & Installation](#setup--installation)
- [Running the Project](#running-the-project)
- [API Documentation](#api-documentation)
- [Configuration](#configuration)
- [Docker Deployment](#docker-deployment)
- [Event Flow](#event-flow)

## Project Overview

This project demonstrates microservices pattern where services communicate asynchronously through Kafka events. The Order Service handles order creation and publishes order events, while the Payment Service consumes those events and processes payments.

### Key Features

- **Microservices Architecture**: Independent, loosely-coupled services
- **Event-Driven Communication**: Asynchronous processing via Apache Kafka
- **Spring Boot 4.0.2**: Latest Spring Boot framework with Spring Kafka integration
- **Kotlin**: Modern JVM language with concise syntax
- **Docker Support**: Containerized deployment with multi-stage builds
- **Input Validation**: Jakarta Validation (formerly Bean Validation)

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Client                               │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ HTTP POST
                     │
        ┌────────────▼──────────────┐
        │   Order Service           │
        │   (Producer/REST API)     │
        │   Port: 8081              │
        └────────────┬──────────────┘
                     │
                     │ Publish OrderEvent to Kafka
                     │
        ┌────────────▼──────────────────────────────────┐
        │        Apache Kafka Broker                    │
        │   Dev: localhost:9093  │  Docker: kafka:9092 │
        │        Topic: "orders-topic"                 │
        │        Topic: "payments-topic"               │
        └────────────┬──────────────────────────────────┘
                     │
                     │ Consume OrderEvent
                     │
        ┌────────────▼──────────────┐
        │   Payment Service         │
        │   (Consumer)              │
        │   Port: 8082              │
        └───────────────────────────┘
```

## Prerequisites

- **Java 21**: Required by both services
- **Docker & Docker Compose**: For container orchestration
- **Gradle 8.5+**: Build tool (included with wrapper scripts)
- **Git**: Version control

### System Requirements

- Minimum 4GB RAM
- Linux/macOS/Windows (with WSL2)
- At least 2GB disk space for Docker images and containers

## Project Structure

```
kotlin-payment/
├── docker-compose.yml          # Kafka & Zookeeper configuration
├── README.md                   # Project documentation
│
├── order-service/              # Order Service (Producer)
│   ├── src/main/kotlin/
│   │   └── gemesio/ugo/order_service/
│   │       ├── OrderServiceApplication.kt       # Spring Boot entry point
│   │       ├── kafka/                           # Kafka configuration
│   │       └── order/                           # Order management logic
│   ├── src/main/resources/
│   │   ├── application.yaml                     # Default configuration (port 8081)
│   │   ├── application-dev.yaml                 # Development profile (localhost:9093)
│   │   └── application-docker.yaml              # Docker profile (kafka:9092)
│   ├── build.gradle.kts                         # Build configuration
│   └── Dockerfile                               # Container image definition
│
└── payment-service/            # Payment Service (Consumer)
    ├── src/main/kotlin/
    │   └── gemesio/ugo/payment_service/
    │       ├── PaymentServiceApplication.kt     # Spring Boot entry point
    │       ├── order/                           # Order event models
    │       └── payment/                         # Payment processing logic
    ├── src/main/resources/
    │   ├── application.yaml                     # Default configuration (port 8082)
    │   ├── application-dev.yaml                 # Development profile (localhost:9093)
    │   └── application-docker.yaml              # Docker profile (kafka:9092)
    │   
    ├── build.gradle.kts                         # Build configuration
    └── Dockerfile                               # Container image definition
```

## Services Description

### Order Service (Producer)

**Purpose**: Creates orders and publishes them to Kafka for further processing.

**Technology Stack**:
- Spring Boot 4.0.2
- Spring Kafka (Producer)
- Spring Data JPA
- Jakarta Validation
- Jackson for JSON serialization

**Key Components**:
- `OrderController`: REST endpoint for order creation
- `OrderProducer`: Publishes `OrderEvent` to Kafka topic "orders"
- `OrderRequest`: Validated input DTO
- `OrderResponse`: Output DTO
- `OrderEvent`: Kafka event model with order details


### Payment Service (Consumer)

**Purpose**: Consumes order events and processes payments.

**Technology Stack**:
- Spring Boot 4.0.2
- Spring Kafka (Consumer)
- Spring Data JPA
- Jackson for JSON serialization

**Key Components**:
- `PaymentConsumer`: Listens to "orders" Kafka topic
- `PaymentEvent`: Event published to "payments" topic after processing
- Automatic order event deserialization

**Processing Logic**:
- Receives `OrderEvent` from Kafka
- Validates order amount
- Publishes `PaymentEvent` with payment status
- Logs payment processing results

## Setup & Installation

### 1. Clone and Enter the Repository

```bash
git clone https://github.com/ugogemesio/payment-service.git
```

```bash
cd payment-service
```



This skips tests (`-x test`) for faster builds. Remove this flag to run tests during build.

## Running the Project

### Option 1: Docker Compose (Recommended)

```bash
# Start all services (Zookeeper, Kafka, Order Service, Payment Service)
docker compose up -d

# View logs
docker compose logs -f

# Stop services
docker compose down
```

### Option 2: Local Development

#### Terminal 1: Start Kafka Infrastructure

```bash
docker compose up zookeeper kafka
```

Wait for Kafka to be ready (look for "Broker started" in logs).



#### Terminal 2: Start Services


### 2. Verify Java Installation

```bash
java -version
# Expected: OpenJDK 21 or higher
```


### 3. Build Both Services

```bash
# Build Order Service
cd order-service
./gradlew build -x test
cd ..

# Build Payment Service
cd payment-service
./gradlew build -x test
cd ..
```

```bash
cd order-service
./gradlew bootRun
```

Expected output: `Started OrderServiceApplication in ... seconds`

#### Terminal 3: Start Payment Service

```bash
cd payment-service
./gradlew bootRun
```

Expected output: `Started PaymentServiceApplication in ... seconds`

## API Documentation

### Order Service Endpoints

**Base URL**: `http://localhost:8081`

> **Note**: The Order Service exposes an H2 database console but is primarily designed as an event producer for Kafka; it's not implemented yet.

### Payment Service Endpoints

The Payment Service is a background consumer that listens to Kafka events. It doesn't expose REST API endpoints but provides database console for debugging.

#### H2 Database Console

TODO ( )
#### Event Flow

1. Order Service receives event → publishes to Kafka `orders-topic`
2. Payment Service consumes from `orders-topic`
3. Payment Service processes the order
4. Payment Service publishes result to `payments-topic` (if configured)
5. Results are stored in both services' H2 databases (later)

## Configuration

Both services support multiple Spring profiles to adjust configuration for different environments.

### Profiles

| Profile | Usage                              | Kafka Bootstrap | Use Case |
|---------|------------------------------------|-----------------|----------|
| `default` | No profile specified! DO NOT USE | Not configured in default | Testing with H2 only |
| `dev` | Development (`application-dev.yaml`) | `localhost:9093` | Local development |
| `docker` | Docker Compose (`application-docker.yaml`) | `kafka:9092` | Container deployment |

### Order Service Configuration

#### Default Configuration (`application.yaml`)

```yaml
spring:
  application:
    name: order-service
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: true

server:
  port: 8081
```

#### Development Profile (`application-dev.yaml`)

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9093
    consumer:
      group-id: ${spring.application.name}
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"
        spring.json.value.default.type: gemesio.ugo.order_service.order.OrderEvent
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

#### Docker Profile (`application-docker.yaml`)

```yaml
spring:
  kafka:
    bootstrap-servers: kafka:9092
    consumer:
      group-id: ${spring.application.name}
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"
        spring.json.value.default.type: gemesio.ugo.order_service.order.OrderEvent
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

### Payment Service Configuration

#### Default Configuration (`application.yaml`)

```yaml
spring:
  application:
    name: payment-service
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: true

server:
  port: 8082
```

#### Development Profile (`application-dev.yaml`)

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9093
    consumer:
      group-id: ${spring.application.name}
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "gemesio.ugo.order_service,gemesio.ugo.payment_service,*"
        spring.json.value.default.type: gemesio.ugo.payment_service.order.OrderEvent
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      properties:
        spring.json.trusted.packages: "gemesio.ugo.payment_service"
      group-id: payment-service
```

#### Docker Profile (`application-docker.yaml`)

```yaml
spring:
  kafka:
    bootstrap-servers: kafka:9092
    consumer:
      group-id: ${spring.application.name}
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "gemesio.ugo.order_service,gemesio.ugo.payment_service,*"
        spring.json.value.default.type: gemesio.ugo.payment_service.order.OrderEvent
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      properties:
        spring.json.trusted.packages: "gemesio.ugo.payment_service"
      group-id: payment-service
```

### Activating Profiles

#### Option 1: Environment Variable

```bash
export SPRING_PROFILES_ACTIVE=dev
./gradlew bootRun
```

#### Option 2: Command Line Argument

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

#### Option 3: Docker Compose (Auto-configured)

Docker Compose automatically activates the `docker` profile:

```yaml
environment:
  SPRING_PROFILES_ACTIVE: docker
```

## Docker Deployment

### Docker Compose Configuration

The `docker-compose.yml` includes all necessary services:

```yaml
services:
   zookeeper:
      image: confluentinc/cp-zookeeper:7.4.0
      environment:
         ZOOKEEPER_CLIENT_PORT: 2181
      ports:
         - "2181:2181"

   kafka:
      image: confluentinc/cp-kafka:7.4.0
      depends_on:
         - zookeeper
      environment:
         KAFKA_BROKER_ID: 1
         KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
         KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
         KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      ports:
         - "9092:9092"

   order-service:
      build:
         context: ./order-service
         dockerfile: Dockerfile
      ports:
         - "8081:8081"
      depends_on:
         - kafka
      environment:
         - SPRING_PROFILES_ACTIVE=docker
         - SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092

   payment-service:
      build:
         context: ./payment-service
         dockerfile: Dockerfile
      ports:
         - "8082:8082"
      depends_on:
         - kafka
      environment:
         - SPRING_PROFILES_ACTIVE=docker
         - SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
```

### Dockerfile Strategy (Multi-Stage Build)

Both services use an optimized multi-stage build:

**Build Stage**:
- Uses `gradle:8.14-jdk21` for compilation (required by Spring Boot 4.0.2)
- Copies entire project context
- Builds and excludes tests for faster builds
- Creates executable JAR file

**Runtime Stage**:
- Uses lightweight `eclipse-temurin:21-jre` (JRE only, no JDK)
- Copies compiled JAR from build stage
- Reduces final image size by ~70%

### Building Docker Images

```bash
# Build individual services
docker build -t order-service:latest ./order-service
docker build -t payment-service:latest ./payment-service

# Or use docker-compose to build all
docker-compose build
```

### Running with Docker

```bash
# Start all services
docker compose up -d

# View service logs
docker compose logs -f order-service
docker compose logs -f payment-service
docker compose logs -f kafka


# Stop all services
docker compose down

# Remove all data/volumes
docker compose down -v
```

### Docker Commands Reference

```bash
# View running containers
docker ps

# View all containers (including stopped)
docker ps -a

# Access container shell
docker exec -it order-service sh

# View container logs with follow
docker logs -f kafka

# Stop specific container
docker stop order-service

# Remove container
docker rm order-service

# View container resource usage
docker stats
```

## Event Flow

### Complete Order Processing Flow

```
1. User/Application creates order via Kafka
   ↓
2. Order Service publishes to Kafka topic "orders-topic"
   {
     "customerId": "CUST-001",
     "amount": 150.50
   }
   ↓
3. Payment Service consumes from topic "orders-topic"
   ↓
4. Payment Service processes the order
   ↓
5. Payment Service publishes result to Kafka topic "payments-topic"
   {
     "orderId": "550e8400-e29b-41d4-a716-446655440000",
     "status": "SUCCESS",
     "message": "Payment processed"
   }
   ↓
6. Both services store results in H2 database(LATER)
   ↓
7. Data available in H2 console for inspection(LATER)
```

### Kafka Topics

**Topic: `orders-topic`**
- Producer: Order Service
- Consumer: Payment Service
- Message Type: `OrderEvent`
- Partition Key: `orderId` (ensures ordering per order)
- Replication Factor: 1 (development)

**Topic: `payments-topic`**
- Producer: Payment Service
- Consumer: (Optional - for extension)
- Message Type: `PaymentEvent`
- Partition Key: `orderId`

### Asynchronous Processing

The entire flow is asynchronous:

1. Order Service publishes to Kafka and stores in database
2. Payment Service consumes asynchronously in the background
3. Services are decoupled - can be scaled independently
4. Failures in one service don't block the other

### Monitoring Kafka Topics via Docker

```bash
# List all topics
docker exec kafka kafka-topics \
  --list \
  --bootstrap-server localhost:9092

# View messages in "orders-topic"
docker exec kafka kafka-console-consumer \
  --topic orders-topic \
  --bootstrap-server localhost:9092 \
  --from-beginning

# View messages in "payments-topic"
docker exec kafka kafka-console-consumer \
  --topic payments-topic \
  --bootstrap-server localhost:9092 \
  --from-beginning

# Monitor topic consumption in real-time
docker exec kafka kafka-console-consumer \
  --topic orders-topic \
  --bootstrap-server localhost:9092 \
  --from-beginning \
  --max-messages 10
```

### Common Issues and Solutions

#### Issue 1: Kafka Connection Refused

**Error**: `Connection refused: localhost:9092`

**Solution**:
```bash
# Check Kafka is running
docker ps | grep kafka

# Restart Kafka
docker-compose restart kafka

# Wait 30 seconds for Kafka to be ready
sleep 30

# Verify connection
docker exec kafka kafka-broker-api-versions \
  --bootstrap-server localhost:9092
```

#### Issue 2: Port Already in Use

**Error**: `Address already in use: localhost:8080` or `localhost:8082`

**Solution**:
```bash
# Find process using port
lsof -i :8080
lsof -i :8082

# Kill process
kill -9 <PID>

# Or change port in application.yaml
# server:
#   port: 8090
```

#### Issue 3: JSON Deserialization Error

**Error**: `JsonMappingException` in Payment Service

**Solution**:
- Ensure `OrderEvent` class is in the same package path
- Verify `spring.json.trusted.packages` includes the package
- Check that `spring.json.value.default.type` matches the event class

#### Issue 4: Messages Not Being Consumed

**Error**: Orders published but Payment Service not processing

**Solution**:
```bash
# Check if Payment Service is running
curl http://localhost:8082/h2-console

# Verify consumer group offset
docker exec kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --group payment-service \
  --describe

# Reset consumer offset to beginning
docker exec kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --group payment-service \
  --reset-offsets \
  --to-earliest \
  --execute \
  --topic orders-topic
```

#### Issue 5: Out of Memory

**Error**: `OutOfMemoryError: Java heap space`

**Solution - Local Development**:
```bash
# Set JVM heap size for Gradle
export GRADLE_OPTS="-Xmx2g"

# Run service with custom heap
./gradlew bootRun --args='--jvm.heap.size=1g'
```

**Solution - Docker**:
```yaml
# In docker-compose.yml
order-service:
   environment:
      - JAVA_OPTS=-Xms512m -Xmx1024m
```

### Debugging

#### Enable Debug Logging

```yaml
# In application.yaml
logging:
   level:
      gemesio.ugo: DEBUG
      org.springframework.kafka: DEBUG
      org.apache.kafka: INFO
```

#### View Service Logs

```bash
# Order Service
./gradlew bootRun --args='--logging.level.gemesio.ugo=DEBUG'

# Docker logs
docker-compose logs -f order-service --tail=100
```

#### Check Kafka Cluster Status

```bash
# Broker info
docker exec kafka kafka-broker-api-versions \
  --bootstrap-server localhost:9092

# Topic details
docker exec kafka kafka-topics \
  --describe \
  --topic orders-topic \
  --bootstrap-server localhost:9092

# Consumer group lag
docker exec kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --group payment-service \
  --describe
```

## Performance Considerations

### Optimization Tips

1. **Message Batching**: Configure Kafka batch size for better throughput
2. **Thread Pools**: Adjust Kafka listener thread pools in `application.yaml`
3. **Database Indexing**: Add indexes on frequently queried fields
4. **Caching**: Implement caching for order lookups
5. **Monitoring**: Use Kafka metrics and Spring Boot Actuator

### Scaling

```yaml
# application.yaml - Multiple consumer threads
spring:
   kafka:
      listener:
         concurrency: 5  # 5 concurrent message processors

      producer:
         batch-size: 16384  # 16KB batches
         linger-ms: 10  # Wait 10ms for batching
```

## Contributing

1. Create feature branch: `git checkout -b feature/new-feature`
2. Commit changes: `git commit -am 'Add new feature'`
3. Push to branch: `git push origin feature/new-feature`
4. Submit pull request
5. Tests and H2 is pending!

## License

This project is licensed under the MIT License - see LICENSE file for details.

## Contact

For questions or issues, contact the development team at `ugo.gemesio@gmail.com`

## Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Kafka Documentation](https://spring.io/projects/spring-kafka)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Kotlin Language Guide](https://kotlinlang.org/docs/home.html)
- [Docker Documentation](https://docs.docker.com/)
