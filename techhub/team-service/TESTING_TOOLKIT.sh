# ═══════════════════════════════════════════════════════════════════
#  TechHub Team Service — Testing & Operations Toolkit
#  Language: Java 21 / Spring Boot 3.x
#  Port: 8084  |  Context path: /api
# ═══════════════════════════════════════════════════════════════════


# ───────────────────────────────────────────────────────────────────
# SECTION 1 — CONTAINER MANAGEMENT
# ───────────────────────────────────────────────────────────────────

## 1.1  Daily workflow commands

# Start all infrastructure (no app rebuild):
docker compose up postgres-team redis kafka zookeeper -d

# Build + start team-service fresh:
docker compose up team-service --build --force-recreate -d

# Tail application logs with timestamps:
docker compose logs -f --timestamps team-service

# Follow only ERROR lines:
docker compose logs -f team-service 2>&1 | grep -i error

# Check container resource usage (live):
docker stats team-service postgres-team techhub-redis techhub-kafka

# Inspect all service health states at once:
docker compose ps

# Open a shell inside the running container (debug only):
docker compose exec team-service sh

# ─ Cleanup ─
# Stop everything, keep volumes:
docker compose down

# Stop + wipe ALL volumes (full reset — you lose DB data):
docker compose down -v

# Remove dangling images after many builds:
docker image prune -f


## 1.2  Lazy Docker extensions for IntelliJ

# Plugin: "Docker" (JetBrains official)
#   - View running containers, logs, volumes from the IDE
#   - Right-click compose file → "Run" or "Stop"
#   - Attach terminal to container from IDE panel

# Plugin: "Docker Explorer" (community)
#   - Visual tree of images, containers, networks


# ───────────────────────────────────────────────────────────────────
# SECTION 2 — API ENDPOINT TESTING
# ───────────────────────────────────────────────────────────────────

## 2.1  HTTPie — best CLI for REST APIs
# Install: brew install httpie  OR  pip install httpie
# Docs: https://httpie.io/docs/cli

BASE=http://localhost:8084/api
TOKEN="Bearer eyJhbGci..."   # replace with real JWT

# Health check (no auth required):
http GET $BASE/actuator/health

# Create a team:
http POST $BASE/teams \
  Authorization:"$TOKEN" \
  Content-Type:application/json \
  name="AI Research Team" \
  maxMembers:=5 \
  description="ML enthusiasts"

# Get my teams:
http GET $BASE/teams/my Authorization:"$TOKEN"

# Get team by ID:
http GET $BASE/teams/a1000000-0000-0000-0000-000000000001 \
  Authorization:"$TOKEN"

# Send an invitation:
http POST $BASE/teams/a1000000-0000-0000-0000-000000000001/invite \
  Authorization:"$TOKEN" \
  receiverId="u1000000-0000-0000-0000-000000000009"

# Accept an invitation:
http POST $BASE/invitations/c1000000-0000-0000-0000-000000000001/accept \
  Authorization:"$TOKEN"

# Decline an invitation:
http POST $BASE/invitations/c1000000-0000-0000-0000-000000000002/decline \
  Authorization:"$TOKEN"

# Get my invitations:
http GET $BASE/invitations/my Authorization:"$TOKEN"

# Leave a team:
http POST $BASE/teams/a1000000-0000-0000-0000-000000000001/leave \
  Authorization:"$TOKEN"


## 2.2  cURL equivalents (no install needed)

# Health check:
curl -s http://localhost:8084/api/actuator/health | jq

# Create team:
curl -s -X POST http://localhost:8084/api/teams \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"AI Team","maxMembers":5}' | jq

# Pretty-print all response headers + body:
curl -v http://localhost:8084/api/actuator/health 2>&1 | less


## 2.3  Bruno — Git-native API client (replaces Postman)
# Install: https://www.usebruno.com/
# Import collections into IntelliJ via File Explorer panel
# All requests are stored as .bru files alongside the source code
# — commit them to git so the whole team shares the collection

# Create a bruno collection at:
#   team-service/bruno/
#     team-service.bru     (collection config)
#     health.bru
#     create-team.bru
#     get-my-teams.bru
#     invite-user.bru
#     accept-invitation.bru

# Sample .bru file (health check):
# meta {
#   name: Health Check
#   type: http
#   seq: 1
# }
# get {
#   url: {{baseUrl}}/actuator/health
#   body: none
#   auth: none
# }


## 2.4  IntelliJ HTTP Client (.http files)
# Built into IntelliJ IDEA — no plugin needed
# File location: team-service/http/team-api.http

# @baseUrl = http://localhost:8084/api
# @token = {{$jwt}}
#
# ### Health
# GET {{baseUrl}}/actuator/health
#
# ### Create team
# POST {{baseUrl}}/teams
# Authorization: Bearer {{token}}
# Content-Type: application/json
#
# {
#   "name": "AI Research Team",
#   "maxMembers": 5
# }
#
# ### Accept invitation
# POST {{baseUrl}}/invitations/{{invitationId}}/accept
# Authorization: Bearer {{token}}


## 2.5  kcat (formerly kafkacat) — Kafka CLI
# Install: brew install kcat
# Producer: send a test event manually
kcat -P -b localhost:9092 -t team-created \
  -e <<< '{"teamId":"abc","ownerId":"xyz"}'

# Consumer: tail the team-created topic
kcat -C -b localhost:9092 -t team-created -o beginning

# List all topics:
kcat -L -b localhost:9092 | grep topic


# ───────────────────────────────────────────────────────────────────
# SECTION 3 — LOAD & PERFORMANCE TESTING
# ───────────────────────────────────────────────────────────────────

## 3.1  k6 — modern load testing tool (JavaScript DSL)
# Install: brew install k6  OR  https://k6.io/docs/get-started/installation/
# Run:     k6 run load-tests/smoke.js

# ── smoke-test.js (just verify the endpoint responds) ──
# import http from 'k6/http';
# import { check } from 'k6';
# export const options = { vus: 1, duration: '10s' };
# export default function () {
#   const res = http.get('http://localhost:8084/api/actuator/health');
#   check(res, { 'status is 200': (r) => r.status === 200 });
# }

# ── load-test.js (ramp up to 50 VUs over 5 min) ──
# export const options = {
#   stages: [
#     { duration: '1m', target: 10 },   // ramp up
#     { duration: '3m', target: 50 },   // hold
#     { duration: '1m', target: 0 },    // ramp down
#   ],
#   thresholds: {
#     http_req_duration: ['p(95)<500'],  // 95% of requests < 500ms
#     http_req_failed:   ['rate<0.01'],  // <1% error rate
#   },
# };

# Run load test with output to Grafana k6 dashboard:
k6 run --out influxdb=http://localhost:8086/k6 load-tests/load-test.js

# Run with live terminal output:
k6 run --no-usage-report load-tests/smoke.js


## 3.2  Gatling — JVM-based, integrates with Maven
# Add to pom.xml then run: mvn gatling:test
# Generates HTML reports in target/gatling/

# Dependency to add to pom.xml:
# <dependency>
#   <groupId>io.gatling.highcharts</groupId>
#   <artifactId>gatling-charts-highcharts</artifactId>
#   <version>3.10.5</version>
#   <scope>test</scope>
# </dependency>


## 3.3  Apache Bench (ab) — quick one-liner load test
# Pre-installed on macOS and most Linux distros

# 1000 requests, 10 concurrent connections:
ab -n 1000 -c 10 -H "Authorization: Bearer $TOKEN" \
  http://localhost:8084/api/actuator/health

# POST with body:
ab -n 500 -c 5 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -p /tmp/team-payload.json \
  http://localhost:8084/api/teams


## 3.4  Locust — Python-based, scriptable
# Install: pip install locust
# Run:     locust -f locustfile.py --host http://localhost:8084
# Open:    http://localhost:8089 for the web UI

# locustfile.py:
# from locust import HttpUser, task, between
# class TeamServiceUser(HttpUser):
#     wait_time = between(1, 3)
#     @task
#     def health_check(self):
#         self.client.get("/api/actuator/health")
#     @task(3)
#     def get_my_teams(self):
#         self.client.get("/api/teams/my",
#           headers={"Authorization": "Bearer TOKEN"})


# ───────────────────────────────────────────────────────────────────
# SECTION 4 — DATABASE INSPECTION
# ───────────────────────────────────────────────────────────────────

## 4.1  Connect to PostgreSQL inside Docker
docker compose exec postgres-team psql \
  -U team_user -d team_db

# Then inside psql:
# \dt                         -- list all tables
# \d teams                    -- describe teams table
# SELECT * FROM teams;
# SELECT * FROM team_members;
# SELECT * FROM team_invitations WHERE status = 'PENDING';

## 4.2  TablePlus (GUI)
# Download: https://tableplus.com/
# Connection: postgresql://team_user:team_pass_local@localhost:5433/team_db

## 4.3  DBeaver (free GUI)
# Download: https://dbeaver.io/


# ───────────────────────────────────────────────────────────────────
# SECTION 5 — REDIS INSPECTION
# ───────────────────────────────────────────────────────────────────

## 5.1  Redis CLI inside Docker
docker compose exec redis redis-cli

# Inside redis-cli:
# KEYS techhub:team:*          -- list all team cache keys
# GET "techhub:team:members:abc"
# TTL "techhub:team:members:abc"
# DBSIZE                       -- total key count
# FLUSHDB                      -- clear all keys (dev only — DISABLED in compose!)

## 5.2  RedisInsight (GUI)
# Download: https://redis.com/redis-enterprise/redis-insight/
# Connect to: localhost:6379


# ───────────────────────────────────────────────────────────────────
# SECTION 6 — INTELLIJ PLUGINS CHECKLIST
# ───────────────────────────────────────────────────────────────────
#
#  Plugin                          Purpose
#  ────────────────────────────────────────────────────────────────
#  Docker (JetBrains)              Compose file UI, container logs, exec
#  HTTP Client (built-in)          .http files for API testing without Postman
#  Kubernetes (JetBrains)          K8s manifest editing + cluster browser
#  Database Tools (built-in)       PostgreSQL queries inside the IDE
#  EnvFile                         Load .env into run configurations
#  Lombok                          Annotation support for @Data, @Builder etc.
#  MapStruct Support               Navigate mapper → DTO → Entity
#  GitToolBox                      Enhanced git blame + branch info
#  SonarLint                       Real-time code quality (matches CI gate)
#  Checkstyle-IDEA                 Enforce code style on save
#  JaCoCo Coverage                 Coverage gutter overlay after tests run
