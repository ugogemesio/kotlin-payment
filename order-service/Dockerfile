# Build stage
FROM gradle:8.14-jdk21 as builder
WORKDIR /app
COPY . .
RUN gradle build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/order-service-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]