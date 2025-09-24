# Build stage
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Copy Gradle files FIRST
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew

# Copy source AFTER
COPY src src

# Build trong BUILDER stage
RUN ./gradlew build -x test --no-daemon --parallel

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Install dependencies và setup user
RUN apk add --no-cache curl && \
    addgroup -S app && \
    adduser -S app -G app

WORKDIR /app

# Copy JAR từ builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Set permissions
RUN chown app:app app.jar
USER app

# Expose port (8082 theo config của bạn)
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8082/actuator/health || exit 1

# Run app
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+UseG1GC", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
