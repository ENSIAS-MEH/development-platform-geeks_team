#!/bin/bash
# Sends a test UserRegisteredEvent to Kafka
# Simulates what user-service would publish
# Usage: ./scripts/test-produce.sh

KAFKA_CONTAINER="techhub-kafka"

EVENT='{
  "eventId": "test-event-001",
  "eventType": "USER_REGISTERED",
  "timestamp": "2025-01-01T10:00:00Z",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "displayName": "Ratwak Dev",
  "email": "ratwak@techhub.com",
  "role": "DEVELOPER"
}'

echo "── Publishing UserRegisteredEvent ──"
echo "$EVENT" | docker exec -i "$KAFKA_CONTAINER" \
  kafka-console-producer.sh \
  --bootstrap-server localhost:9092 \
  --topic user.registred \
  --property "parse.key=true" \
  --property "key.separator=|" <<< "test-event-001|$EVENT"

echo "✅ Event sent to user.registred"
