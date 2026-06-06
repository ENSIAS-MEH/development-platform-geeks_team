# ═══════════════════════════════════════════════════════════════
#  Community Service – Docker build (uses pre-built JAR)
#  Port: 8084  •  Java 17  •  Non-root user
# ═══════════════════════════════════════════════════════════════

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the built artifact (expected to be built by Maven on the host/runner)
COPY target/community-service-*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
