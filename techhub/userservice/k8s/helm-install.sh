#!/bin/bash
# Run this ONCE to install Kafka and Redis via Helm
# Make sure Minikube is running and techhub namespace exists before running this

echo "Adding Helm repos..."
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update

echo "Installing Redis..."
helm install redis bitnami/redis \
  --namespace techhub \
  --set auth.enabled=false \
  --set master.persistence.enabled=false

#echo "Installing Kafka (includes Zookeeper)..."
#helm install kafka bitnami/kafka \
#  --namespace techhub \
#  --set replicaCount=1 \
#  --set zookeeper.enabled=true \
#  --set persistence.enabled=false \
#  --set kraft.enabled=false

echo "Done! Check status with:"
echo "kubectl get pods -n techhub"