# ─────────────────────────────────────────────
# Stage 1 — Build
# ─────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom first — cache dependency layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ─────────────────────────────────────────────
# Stage 2 — Runtime
# ─────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Non-root user for security
RUN addgroup -S techhub && adduser -S techhub -G techhub

# Copy jar from builder
COPY --from=builder /app/target/*.jar app.jar

# .env is injected via docker-compose env_file — no COPY needed
RUN chown techhub:techhub app.jar

USER techhub

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "app.jar"]
