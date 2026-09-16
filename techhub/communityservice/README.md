# 🚀 TechHub Community Service — Complete DevOps Setup

## Table of Contents
- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Spring Boot Service (Task 1)](#spring-boot-service-task-1)
- [Testing (Task 2)](#testing-task-2)
- [Docker (Task 3)](#docker-task-3)
- [Kubernetes (Task 4)](#kubernetes-task-4)
- [Minikube Deployment (Task 5)](#minikube-deployment-task-5)
- [GitHub Actions CI/CD (Task 6)](#github-actions-cicd-task-6)
- [ArgoCD GitOps (Task 7)](#argocd-gitops-task-7)
- [Monitoring (Task 8)](#monitoring-task-8)
- [DevOps Concepts Explained](#devops-concepts-explained)

---

## Project Overview

The **Community Service** is a Spring Boot microservice within the **TechHub** platform. It handles:
- **Groups** — Create, join, manage community groups
- **Posts** — Create, upvote, pin posts within groups  
- **Comments** — Nested comments (max depth 2) with upvotes
- **Real-time events** — Kafka events for post creation and comments

### Port Mapping (TechHub Platform)
| Service | Port |
|---------|------|
| userservice | 8080 |
| eventservice | 8081 |
| projectservice | 8082 |
| teamservice | 8083 |
| **community** | **8084** |
| notification | 8085 |

---

## Architecture

```
┌─────────────┐     ┌──────────────┐     ┌───────────────┐
│   Client /   │────▶│  API Gateway  │────▶│  Community    │
│   Frontend   │     │  (JWT valid.) │     │  Service:8084 │
└─────────────┘     └──────────────┘     └───┬───┬───┬───┘
                                              │   │   │
                    ┌─────────────────────────┘   │   └─────────────┐
                    ▼                              ▼                 ▼
              ┌──────────┐                 ┌──────────┐      ┌──────────┐
              │PostgreSQL│                 │  Redis   │      │  Kafka   │
              │community │                 │  Cache   │      │ Producer │
              │   _db    │                 └──────────┘      └────┬─────┘
              └──────────┘                                        │
                                                                  ▼
                                                        ┌─────────────────┐
                                                        │  notification   │
                                                        │  service:8085   │
                                                        └─────────────────┘
```

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Framework** | Spring Boot 3.2.3, Java 17 |
| **Database** | PostgreSQL (community_db) |
| **Cache** | Redis (`@Cacheable` on popular posts) |
| **Messaging** | Apache Kafka (topics: `post-created`, `comment-added`) |
| **Security** | Spring Security + JWT validation |
| **API Docs** | SpringDoc OpenAPI (Swagger UI) |
| **Observability** | Micrometer + Prometheus + OpenTelemetry |
| **Build** | Maven + JaCoCo for test coverage |
| **Container** | Docker (multi-stage build) |
| **Orchestration** | Kubernetes (Minikube for local) |
| **CI/CD** | GitHub Actions (3-stage pipeline) |
| **GitOps** | ArgoCD (automated sync) |
| **Monitoring** | Prometheus + Grafana dashboards |

---

## Spring Boot Service (Task 1)

### Entities

The service manages 4 JPA entities:

1. **Group** (`community_groups`) — Community groups with topics, public/private visibility
2. **GroupMember** (`group_members`) — Membership with roles (OWNER, MODERATOR, MEMBER)
3. **Post** (`posts`) — Posts within groups with upvotes, pinning, and types
4. **Comment** (`comments`) — Self-referencing nested comments (max depth 2)

### Key Design Patterns

#### Atomic Upvotes with `@Modifying @Query`
```java
@Modifying
@Query("UPDATE Post p SET p.upvotes = p.upvotes + 1 WHERE p.id = :postId")
void incrementUpvotes(@Param("postId") UUID postId);
```
> **Why?** This prevents race conditions. Instead of `findById` → modify → save (which can lose updates), the database handles the increment atomically in a single SQL statement.

#### Constructor Injection via `@RequiredArgsConstructor`
```java
@Service
@RequiredArgsConstructor  // Lombok generates constructor
public class PostService {
    private final PostRepository postRepository;  // injected via constructor
    private final GroupService groupService;
}
```
> **Why?** Constructor injection (vs `@Autowired` on fields) makes dependencies explicit, enables immutability, and makes testing easier.

#### Redis Caching with `@Cacheable` / `@CacheEvict`
```java
@Cacheable(value = "popularPosts", key = "'page_' + #page + '_size_' + #size")
public Page<PostResponse> getPopularPosts(int page, int size) { ... }

@CacheEvict(value = "popularPosts", allEntries = true)
public void upvotePost(UUID postId) { ... }
```
> **Why?** Popular posts are read frequently but change infrequently. Caching them in Redis avoids expensive database queries. `@CacheEvict` ensures the cache is invalidated when data changes.

#### Kafka Event Publishing
```java
kafkaEventProducer.publishPostCreated(PostCreatedEvent.builder()
    .postId(post.getId())
    .groupId(post.getGroupId())
    .build());
```
> **Why?** Event-driven architecture decouples services. The notification service consumes these events without the community service needing to know about it.

### Configuration (application.yml)

All sensitive values use **environment variable substitution**:
```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/community_db}
    password: ${DB_PASSWORD:postgres}
```
> **Why?** Never hardcode secrets. The `${VAR:default}` syntax provides a default for local development while requiring env vars in production.

---

## Testing (Task 2)

### Unit Tests (JUnit 5 + Mockito)

| Test Class | What's Tested |
|-----------|---------------|
| `GroupServiceTest` | Create group, get group, delete group, join (+ duplicate join → exception), leave group |
| `PostServiceTest` | Create post, get post, upvote (+ upvote non-existent → exception), popular posts order, update post |
| `CommentServiceTest` | Create comment, nested replies, max depth rejection, upvote, delete |

Key test scenarios:
- **Join group already joined** → `DuplicateMemberException`
- **Upvote non-existent post** → `ResourceNotFoundException`
- **Popular posts returns correct order** → Verifies descending upvote sort

### Integration Tests (MockMvc)

| Test Class | Endpoints Tested |
|-----------|-----------------|
| `GroupControllerIntegrationTest` | POST /api/groups, GET, JOIN, LEAVE, DELETE |
| `PostControllerIntegrationTest` | POST posts, GET sorted by popularity, POST upvote, POST comments |

> **What's MockMvc?** It simulates HTTP requests without starting a real server. The `@SpringBootTest` + `@AutoConfigureMockMvc` combination boots the full Spring context with an in-memory H2 database.

---

## Docker (Task 3)

### Multi-Stage Build

```dockerfile
# Stage 1: Build with full Maven + JDK
FROM maven:3.9-eclipse-temurin-17 AS build
COPY pom.xml .
RUN mvn dependency:go-offline -B    # Cache dependencies
COPY src ./src
RUN mvn package -DskipTests -B

# Stage 2: Run with minimal JRE
FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring                   # Non-root!
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### DevOps Concepts:

- **Multi-stage build** — Stage 1 (build) contains Maven + JDK (~800MB). Stage 2 (runtime) only has JRE (~150MB). This reduces the final image size by ~5x.
- **Dependency caching** — `dependency:go-offline` downloads all deps before copying source. Docker caches this layer, so rebuilds after source changes are fast.
- **Non-root user** — Running as `spring:spring` instead of root prevents container escape attacks. This is a security best practice.

---

## Kubernetes (Task 4)

### Files Created

```
communityservice/k8s/
├── deployment.yaml    # 2 replicas, health probes, resource limits
├── service.yaml       # ClusterIP on port 8084
├── configmap.yaml     # Non-sensitive config (DB URL, Redis host, Kafka)
└── secret.yaml        # Sensitive config (DB_PASSWORD, JWT_SECRET)

infra/k8s/
└── namespace.yaml     # techhub namespace
```

### DevOps Concepts:

#### Deployment
```yaml
replicas: 2                          # High availability
readinessProbe:                      # Only receive traffic when ready
  httpGet:
    path: /actuator/health
    port: 8084
  initialDelaySeconds: 30            # Wait for Spring Boot startup
livenessProbe:                       # Restart if unhealthy
  httpGet:
    path: /actuator/health
    port: 8084
  initialDelaySeconds: 60
resources:
  requests:                          # Minimum guaranteed resources
    cpu: 250m
    memory: 512Mi
  limits:                            # Maximum allowed resources
    cpu: 500m
    memory: 1Gi
```

- **Replicas: 2** — If one pod crashes, the other serves traffic. Zero-downtime deployments.
- **Readiness Probe** — Kubernetes won't send traffic to the pod until `/actuator/health` returns 200. This prevents sending requests to a pod still starting up.
- **Liveness Probe** — If the health check fails 3 times, Kubernetes restarts the pod. This auto-heals stuck processes.
- **Resource Requests/Limits** — `requests` guarantee minimum CPU/memory. `limits` prevent one pod from consuming all node resources.

#### ConfigMap vs Secret
- **ConfigMap** — Non-sensitive config (database URLs, feature flags). Stored as plain text.
- **Secret** — Sensitive data (passwords, tokens). Base64-encoded (NOT encrypted by default — use Sealed Secrets or External Secrets in production).

#### Service (ClusterIP)
```yaml
type: ClusterIP    # Only accessible within the cluster
port: 8084
targetPort: 8084
```
> Other services reach community-service via `http://community-service.techhub:8084`. External access requires an Ingress or LoadBalancer.

---

## Minikube Deployment (Task 5)

### Commands

```bash
# 1. Start Minikube
minikube start --memory=4096 --cpus=2

# 2. Point Docker to Minikube's daemon
eval $(minikube docker-env)

# 3. Build image inside Minikube
docker build -t community-service:local ./communityservice

# 4. Apply K8s manifests
kubectl apply -f infra/k8s/namespace.yaml
kubectl apply -f communityservice/k8s/

# 5. Watch pods come up
kubectl get pods -n techhub -w

# 6. Port-forward for local access
kubectl port-forward svc/community-service 8084:8084 -n techhub &

# 7. Verify
curl http://localhost:8084/actuator/health
curl http://localhost:8084/swagger-ui/index.html
```

> **Why Minikube?** It creates a single-node Kubernetes cluster on your local machine. `eval $(minikube docker-env)` makes your local Docker CLI build images directly into Minikube's Docker daemon, avoiding the need to push to a registry.

---

## GitHub Actions CI/CD (Task 6)

### Pipeline Structure

```
┌──────────────┐     ┌──────────────────┐     ┌─────────────┐
│ build-and-   │────▶│ docker-build-    │────▶│   deploy    │
│ test         │     │ push             │     │             │
│              │     │ (main only)      │     │ (main only) │
└──────────────┘     └──────────────────┘     └─────────────┘
```

**Job 1: build-and-test**
- Checkout → Java 17 setup → Maven cache → `mvn test` → `mvn package` → Upload JAR artifact

**Job 2: docker-build-push** (only on push to main)
- Download JAR → Docker login → Build image → Push with SHA and `latest` tags

**Job 3: deploy** (only on push to main)
- Setup kubectl → Configure kubeconfig → `kubectl set image` → `kubectl rollout status`

### DevOps Concepts:

- **Path-based triggers** — Only runs when `communityservice/**` files change. Saves CI minutes.
- **Artifact upload/download** — The JAR built in Job 1 is shared to Job 2 without rebuilding.
- **SHA-tagged images** — `community-service:abc123` is immutable. `latest` is for convenience.
- **Rolling update** — `kubectl set image` triggers a rolling update. K8s creates new pods before killing old ones → zero downtime.

### Required GitHub Secrets
| Secret | Description |
|--------|-------------|
| `DOCKERHUB_USERNAME` | Docker Hub username |
| `DOCKERHUB_TOKEN` | Docker Hub access token |
| `KUBECONFIG` | Base64-encoded kubeconfig for K8s cluster |

---

## ArgoCD GitOps (Task 7)

### What is ArgoCD?

ArgoCD continuously monitors your Git repository and automatically deploys changes to Kubernetes. This is the **GitOps** pattern:

```
Developer pushes to Git → ArgoCD detects change → ArgoCD syncs K8s manifests → Cluster updated
```

### Key Configuration

```yaml
syncPolicy:
  automated:
    prune: true      # Delete K8s resources removed from Git
    selfHeal: true    # Auto-fix if someone manually changes K8s state
```

- **prune: true** — If you delete a manifest from Git, ArgoCD deletes the corresponding K8s resource.
- **selfHeal: true** — If someone `kubectl edit` a resource directly, ArgoCD will revert it to match Git. Git is the single source of truth.

### File Location
```
infra/argocd/community-app.yaml
```

---

## Monitoring (Task 8)

### Stack: Prometheus + Grafana

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  Community   │────▶│  Prometheus  │────▶│   Grafana    │
│  Service     │     │  (scrapes    │     │  (visualizes │
│  /actuator/  │     │   metrics)   │     │   dashboards)│
│  prometheus  │     └──────────────┘     └──────────────┘
```

### Metrics Exposed

The Spring Boot Actuator + Micrometer automatically exposes:

| Metric | What it measures |
|--------|-----------------|
| `http_server_requests_seconds_count` | HTTP request rate by endpoint |
| `cache_gets_total` | Redis cache hits/misses |
| `kafka_producer_record_send_total` | Kafka messages produced |
| `jvm_memory_used_bytes` | JVM heap memory usage |
| `hikaricp_connections_active` | Active database connections |
| `jvm_gc_pause_seconds_sum` | Garbage collection pause times |

### Grafana Dashboard Panels

1. **HTTP Request Rate** — Requests/sec by method, URI, and status code
2. **HTTP Response Time (p95)** — 95th percentile latency
3. **Redis Cache Hit Ratio** — Gauge showing cache effectiveness
4. **Kafka Messages Produced** — Rate of events sent to Kafka
5. **Kafka Producer Errors** — Error rate (should be 0)
6. **JVM Heap Memory** — Memory usage by area (Eden, Survivor, Old Gen)
7. **JVM Non-Heap Memory** — Metaspace and Code Cache
8. **HikariCP Active Connections** — DB connection pool usage
9. **HikariCP Wait Time** — Time waiting for a DB connection
10. **GC Pause Duration** — Garbage collection impact
11. **Active Threads** — JVM thread count

### Prometheus Configuration

```yaml
# infra/monitoring/prometheus/values.yaml
extraScrapeConfigs: |
  - job_name: 'community-service'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 15s
    static_configs:
      - targets: ['community-service.techhub.svc.cluster.local:8084']
```

---

## DevOps Concepts Explained

### 1. Microservices Architecture
The TechHub platform is decomposed into independent services (user, event, project, team, community, notification). Each service:
- Has its own database (Database-per-Service pattern)
- Communicates via REST APIs and Kafka events
- Can be deployed, scaled, and updated independently

### 2. Containerization (Docker)
Containers package your application + its dependencies into a portable unit that runs the same everywhere (dev laptop, CI server, production).

### 3. Container Orchestration (Kubernetes)
Kubernetes manages containers at scale:
- **Scheduling** — Decides which node runs each pod
- **Self-healing** — Restarts failed pods automatically
- **Scaling** — Add/remove replicas based on load
- **Service discovery** — DNS-based routing between services
- **Rolling updates** — Zero-downtime deployments

### 4. CI/CD (Continuous Integration / Continuous Deployment)
- **CI** — Automatically build and test code on every push
- **CD** — Automatically deploy tested code to production
- **Pipeline** — build → test → package → dockerize → deploy

### 5. GitOps (ArgoCD)
Git is the single source of truth for infrastructure state:
- All K8s manifests live in Git
- Changes to infrastructure = Git commits
- ArgoCD continuously syncs cluster state to match Git
- Drift is automatically corrected

### 6. Observability (Prometheus + Grafana)
The "three pillars" of observability:
- **Metrics** (Prometheus) — Numeric time-series data (request rate, latency, memory)
- **Logs** (ELK/Loki) — Text records of events
- **Traces** (OpenTelemetry) — Request flow across microservices

### 7. Infrastructure as Code (IaC)
All infrastructure is defined in declarative files:
- `deployment.yaml` — How the app runs
- `configmap.yaml` — Configuration
- `values.yaml` — Prometheus config
- `community-ci.yml` — CI/CD pipeline

### 8. Separation of Concerns
- **ConfigMap** — Non-sensitive config → can be version-controlled
- **Secret** — Sensitive data → encrypted at rest, restricted access
- **Environment Variables** — No hardcoded values in code

---

## File Structure

```
techhub/
├── communityservice/
│   ├── src/main/java/com/techhub/community/
│   │   ├── CommunityServiceApplication.java      # @SpringBootApplication @EnableCaching
│   │   ├── config/
│   │   │   ├── KafkaConfig.java                   # Topic declarations
│   │   │   ├── RedisConfig.java                   # RedisCacheManager with TTLs
│   │   │   ├── SecurityConfig.java                # Spring Security (stateless, CSRF disabled)
│   │   │   └── SwaggerConfig.java                 # OpenAPI metadata
│   │   ├── controller/
│   │   │   ├── GroupController.java               # /api/groups CRUD + membership
│   │   │   ├── PostController.java                # /api/groups/{id}/posts + comments
│   │   │   └── PopularPostsController.java        # /api/posts/popular (cached)
│   │   ├── dto/                                   # Request/Response DTOs + Kafka events
│   │   ├── entity/                                # JPA entities (Group, Post, Comment, GroupMember)
│   │   ├── enums/                                 # MemberRole, PostType, Topic
│   │   ├── exception/                             # Global exception handler
│   │   ├── repository/                            # JPA repos with @Modifying @Query
│   │   └── service/                               # Business logic + KafkaEventProducer
│   ├── src/main/resources/
│   │   ├── application.yml                        # Main config (env vars)
│   │   ├── application-test.yml                   # Test profile (H2, no Redis/Kafka)
│   │   ├── schema.sql                             # DDL reference
│   │   ├── data.sql                               # Seed data
│   │   └── db/migration/                          # Flyway migrations
│   ├── src/test/java/                             # Unit + Integration tests
│   ├── Dockerfile                                 # Multi-stage build
│   ├── pom.xml                                    # Dependencies
│   └── k8s/
│       ├── deployment.yaml                        # 2 replicas, probes, resources
│       ├── service.yaml                           # ClusterIP:8084
│       ├── configmap.yaml                         # Non-sensitive config
│       └── secret.yaml                            # DB_PASSWORD, JWT_SECRET
├── infra/
│   ├── k8s/
│   │   └── namespace.yaml                         # techhub namespace
│   ├── argocd/
│   │   └── community-app.yaml                     # GitOps application
│   └── monitoring/
│       ├── prometheus/
│       │   └── values.yaml                        # Scrape config
│       └── grafana/
│           └── dashboards/
│               └── community-dashboard.json       # 11-panel dashboard
└── .github/
    └── workflows/
        └── community-ci.yml                       # 3-job CI/CD pipeline
```

---

## Quick Start (Local Development)

```bash
# 1. Start PostgreSQL + Redis + Kafka (Docker Compose)
docker-compose up -d

# 2. Run the service
cd techhub/communityservice
./mvnw spring-boot:run

# 3. Access
# Swagger UI: http://localhost:8084/swagger-ui.html
# Health:     http://localhost:8084/actuator/health
# Prometheus: http://localhost:8084/actuator/prometheus
```

---

*Built with ❤️ for the TechHub Platform*
