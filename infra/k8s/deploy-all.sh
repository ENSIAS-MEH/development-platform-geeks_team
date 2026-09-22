#!/bin/bash

# Create namespace
kubectl apply -f infra/k8s/namespace.yaml

# Deploy Databases & Brokers
kubectl apply -f infra/k8s/middleware/postgres-deployments.yaml
kubectl apply -f infra/k8s/middleware/redis-deployments.yaml
kubectl apply -f infra/k8s/middleware/kafka-deployment.yaml

# Deploy services
kubectl apply -f techhub/project-service/k8s/
kubectl apply -f "Desktop/Projet plateforme/services/event-service/k8s/"
