# ═══════════════════════════════════════════════════════════════════
#  TechHub — Team Service Dockerfile
#  Governance: doc 02 §3.1 §3.2
#  Base images: maven:3.9-eclipse-temurin-21 (build)
#               eclipse-temurin:21-jre-alpine  (runtime)
#  Port: 8084 (port registry §2.3)
#  Owner: Member 3
# ═══════════════════════════════════════════════════════════════════

# ──────────────────────────────────────────────────────────────────
# STAGE 1 — Dependency cache
# Copies only pom.xml first so Maven's dependency layer is cached
# independently from source code. Rebuilding after a source-only
# change skips this expensive layer entirely.
# ──────────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS deps

WORKDIR /build

# Copy the Maven wrapper if your project uses it (comment out if not)
# COPY .mvn/ .mvn/
# COPY mvnw .

# Copy only the build descriptor — maximises Docker layer cache
COPY pom.xml .

# Download all compile + runtime + test dependencies offline.
# -B = batch mode (no progress bars in CI logs)
# -q = quiet (remove for debugging slow builds)
RUN mvn dependency:go-offline -B -q

# ──────────────────────────────────────────────────────────────────
# STAGE 2 — Compile & package
# Inherits the pre-populated Maven cache from the deps stage.
# ──────────────────────────────────────────────────────────────────
FROM deps AS builder

# Copy application source (src/ only — pom.xml already present)
COPY src ./src

# Build the fat JAR.
# -DskipTests  : tests run in CI, not during image build
# -B           : batch / non-interactive
# clean        : removes leftover target/ from previous local builds
#                that may have been bind-mounted in dev
RUN mvn clean package -DskipTests -B

# ──────────────────────────────────────────────────────────────────
# STAGE 3 — Extract Spring Boot layered JAR
# Spring Boot 3.x produces a layered JAR. Extracting the layers
# here means Docker can cache lib/ separately from app classes —
# only the application/ layer changes on most deploys.
# ──────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS extractor

WORKDIR /extract

# Copy the fat JAR from the builder stage
COPY --from=builder /build/target/*.jar app.jar

# Split into four layers (ordered by volatility: least → most):
#   1. dependencies       — third-party jars  (rarely changes)
#   2. spring-boot-loader — Spring Boot loader (rarely changes)
#   3. snapshot-dependencies — SNAPSHOT jars  (changes on dep updates)
#   4. application        — your compiled code (changes every commit)
RUN java -Djarmode=layertools -jar app.jar extract --destination extracted

# ──────────────────────────────────────────────────────────────────
# STAGE 4 — Final runtime image (production)
# Minimal Alpine JRE — no compiler, no Maven, no shell scripts.
# ──────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

# ── Security hardening ────────────────────────────────────────────
# Run as a dedicated non-root system user (governance §3.2)
# -S = system group/user, -G = assign group, no home directory
RUN addgroup -S techhub && adduser -S teamservice -G techhub

WORKDIR /app

# ── Copy Spring Boot layers in dependency order ───────────────────
# Docker caches each COPY as a separate layer.
# The least-volatile layers go first so they are reused across builds.
COPY --from=extractor --chown=teamservice:techhub /extract/extracted/dependencies/          ./
COPY --from=extractor --chown=teamservice:techhub /extract/extracted/spring-boot-loader/    ./
COPY --from=extractor --chown=teamservice:techhub /extract/extracted/snapshot-dependencies/ ./
COPY --from=extractor --chown=teamservice:techhub /extract/extracted/application/           ./

# Switch to non-root user AFTER file copies (need root to chown)
USER teamservice

# ── Port declaration ──────────────────────────────────────────────
# Port 8083 — Team Service allocation (governance §2.3) and matches
# the application-docker.yml configuration used at runtime
EXPOSE 8083

# ── Health check ──────────────────────────────────────────────────
# Governance §14.1: /actuator/health is mandatory
# --start-period=60s : gives Spring Boot time to start before
#                      unhealthy retries count against the container
# --interval=30s     : check every 30 s (matches K8s probe cadence)
# --timeout=10s      : fail if no response in 10 s
# --retries=3        : mark unhealthy after 3 consecutive failures
HEALTHCHECK \
  --interval=30s \
  --timeout=10s \
  --start-period=60s \
  --retries=3 \
  CMD wget --quiet --tries=1 --spider \
    http://localhost:8083/api/actuator/health || exit 1

# ── JVM tuning for containers ────────────────────────────────────
# +UseContainerSupport   : read cgroup memory limits (not host RAM)
# MaxRAMPercentage=75.0  : use 75% of container memory for heap
# -Djava.security.egd   : faster random seed (avoids /dev/random block)
# -Dspring.profiles.active=docker : activates application-docker.yml
ENV JAVA_OPTS="\
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -XX:+UseG1GC \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/tmp/heap-dump.hprof \
  -Djava.security.egd=file:/dev/./urandom"

# ── Entry point ───────────────────────────────────────────────────
# Spring Boot 3.x launcher class (changed from 2.x JarLauncher path)
# ENTRYPOINT exec form — receives OS signals correctly (no PID 1 issue)
ENTRYPOINT ["sh", "-c", \
  "java $JAVA_OPTS \
  -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-docker} \
  org.springframework.boot.loader.launch.JarLauncher"]
