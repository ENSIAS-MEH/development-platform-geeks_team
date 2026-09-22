# Notification Service — Local Runbook

## Prerequisites
- Docker Desktop running
- Java 17 (check: `java -version`)
- Maven 3.9+ (check: `mvn -version`)
- IntelliJ IDEA

---

## Option A — Run everything in Docker

### 1. Fill in your .env
```
MAIL_USERNAME=your-gmail@gmail.com
MAIL_PASSWORD=your-16-char-app-password   # Gmail → Security → App Passwords
DB_USERNAME=postgres
DB_PASSWORD=yourpassword
```

### 2. Start all services
```bash
docker compose up -d
```

### 3. Verify all containers are healthy
```bash
docker compose ps
```
Expected:
```
techhub-kafka                  running (healthy)
techhub-kafka-ui               running
techhub-notification-db        running (healthy)
techhub-notification-service   running
```

### 4. Check notification service logs
```bash
docker compose logs -f notification-service
```

### 5. Open Kafka UI
→ http://localhost:8090
→ Topics: you should see user.registred and user.passwordchanged

### 6. Open Swagger UI
→ http://localhost:8082/swagger-ui.html

---

## Option B — Run infra in Docker, app in IntelliJ (recommended for dev)

### 1. Start only infra (Kafka + DB)
```bash
docker compose up -d kafka kafka-ui postgres
```

### 2. Wait for Kafka to be healthy
```bash
docker compose ps   # wait until kafka shows (healthy)
```

### 3. Create topics (first time only)
```bash
bash scripts/create-topics.sh
```

### 4. Configure IntelliJ Run Configuration
```
Main class : com.techhub.notification_service.NotificationServiceApplication
VM options : -Dspring.profiles.active=local
```

Step by step in IntelliJ:
  1. Run → Edit Configurations
  2. Click + → Spring Boot
  3. Main class: NotificationServiceApplication
  4. Modify options → Add VM options
  5. VM options: -Dspring.profiles.active=local
  6. Apply → OK
  7. Click Run (green arrow)

### 5. Watch the console — you should see:
```
Started NotificationServiceApplication in X seconds
[UserEventListener] Container started — listening on user.registred
```

---

## Testing end-to-end

### Test 1 — Manual REST trigger (no Kafka needed)
```bash
curl -X POST http://localhost:8082/api/v1/notifications/send \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "recipientEmail": "yourreal@email.com",
    "displayName": "Test User",
    "type": "WELCOME_EMAIL"
  }'
```
→ Check your inbox
→ Check DB: notification should be status=SENT

### Test 2 — Kafka event trigger
```bash
bash scripts/test-produce.sh
```
→ Watch IntelliJ console for:
```
[UserEventListener] Received event on topic=user.registred
[NotificationService] Notification SENT id=...
```
→ Check your inbox for welcome email

### Test 3 — Query via REST
```bash
# All notifications for a user
curl http://localhost:8082/api/v1/notifications/user/550e8400-e29b-41d4-a716-446655440000

# All failed
curl http://localhost:8082/api/v1/notifications?status=FAILED

# Swagger UI (browser)
open http://localhost:8082/swagger-ui.html
```

---

## Useful Docker commands

```bash
# Stop everything
docker compose down

# Stop and wipe all data (clean slate)
docker compose down -v

# Rebuild notification service image after code change
docker compose build notification-service
docker compose up -d notification-service

# Tail logs
docker compose logs -f notification-service
docker compose logs -f kafka

# Enter Kafka container
docker exec -it techhub-kafka bash

# List topics from inside container
kafka-topics.sh --bootstrap-server localhost:9092 --list

# Consume messages from topic (debug)
docker exec techhub-kafka kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic user.registred \
  --from-beginning
```

---

## Ports summary

| Service              | Host port | Purpose              |
|----------------------|-----------|----------------------|
| notification-service | 8082      | REST API + Swagger   |
| kafka (internal)     | 9092      | Docker network       |
| kafka (external)     | 9094      | IntelliJ / localhost |
| kafka-ui             | 8090      | Browser UI           |
| postgres             | 5433      | DB (avoid 5432 clash)|
