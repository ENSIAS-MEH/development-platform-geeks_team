#!/bin/bash
# Creates the user-service topics in the local KRaft Kafka
# Run after: docker compose up kafka

set -e

KAFKA_CONTAINER="techhub-kafka"
BOOTSTRAP="localhost:9092"

echo "── Creating TechHub Kafka topics ──"

docker exec "$KAFKA_CONTAINER" kafka-topics.sh \
  --bootstrap-server "$BOOTSTRAP" \
  --create --if-not-exists \
  --topic user.registred \
  --partitions 1 \
  --replication-factor 1

docker exec "$KAFKA_CONTAINER" kafka-topics.sh \
  --bootstrap-server "$BOOTSTRAP" \
  --create --if-not-exists \
  --topic user.passwordchanged \
  --partitions 1 \
  --replication-factor 1

echo ""
echo "── Existing topics ──"
docker exec "$KAFKA_CONTAINER" kafka-topics.sh \
  --bootstrap-server "$BOOTSTRAP" \
  --list
