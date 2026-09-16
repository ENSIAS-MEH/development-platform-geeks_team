#!/usr/bin/env bash
# deploy-all.sh — Apply all TechHub K8s manifests in dependency order.
# Usage:
#   ./infra/deploy-all.sh               # apply everything
#   DRY_RUN=true ./infra/deploy-all.sh  # preview without applying
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"
KUBECTL="kubectl${DRY_RUN:+ --dry-run=client}"

apply() {
  echo "  --> $1"
  $KUBECTL apply -f "$1"
}

echo "==> [1/5] Namespace"
apply "$SCRIPT_DIR/k8s/namespace.yaml"

# ── Dev/Minikube backing services (skip in production by setting SKIP_DEV_DEPS=true) ──
if [[ "${SKIP_DEV_DEPS:-false}" != "true" ]]; then
  echo ""
  echo "==> [2/5] Dev backing services (Postgres, Redis, Kafka)"
  apply "$ROOT_DIR/techhub/team-service/k8s/dev-deps/postgres.yaml"
  apply "$ROOT_DIR/techhub/team-service/k8s/dev-deps/redis.yaml"
  apply "$ROOT_DIR/techhub/team-service/k8s/dev-deps/kafka.yaml"

  if [[ "${DRY_RUN:-}" != "true" ]]; then
    echo "     Waiting for postgres-team to be ready..."
    kubectl rollout status deployment/postgres-team -n techhub --timeout=120s
    echo "     Waiting for redis to be ready..."
    kubectl rollout status deployment/redis -n techhub --timeout=60s
  fi
else
  echo "==> [2/5] Skipping dev backing services (SKIP_DEV_DEPS=true)"
fi

echo ""
echo "==> [3/5] team-service — ConfigMap & Secret"
apply "$ROOT_DIR/techhub/team-service/k8s/configmap.yaml"
apply "$ROOT_DIR/techhub/team-service/k8s/secret.yaml"

echo ""
echo "==> [4/5] team-service — Deployment & Service"
apply "$ROOT_DIR/techhub/team-service/k8s/deployment.yaml"
apply "$ROOT_DIR/techhub/team-service/k8s/service.yaml"

echo ""
echo "==> [5/5] Waiting for team-service rollout..."
if [[ "${DRY_RUN:-}" != "true" ]]; then
  kubectl rollout status deployment/team-service \
    -n techhub \
    --timeout=180s
fi

echo ""
echo "==> [6/5] Observability stack (Prometheus, Loki, Tempo, Promtail, Grafana)"
if [[ "${SKIP_OBSERVABILITY:-false}" != "true" ]]; then
  apply "$SCRIPT_DIR/k8s/observability/prometheus.yaml"
  apply "$SCRIPT_DIR/k8s/observability/loki.yaml"
  apply "$SCRIPT_DIR/k8s/observability/tempo.yaml"
  apply "$SCRIPT_DIR/k8s/observability/promtail.yaml"
  apply "$SCRIPT_DIR/k8s/observability/grafana.yaml"

  if [[ "${DRY_RUN:-}" != "true" ]]; then
    echo "     Waiting for Grafana..."
    kubectl rollout status deployment/grafana -n techhub --timeout=90s
    echo ""
    echo "     Open Grafana: minikube service grafana -n techhub"
    echo "     Or:           kubectl port-forward svc/grafana 3000:3000 -n techhub"
    echo "     Login:        admin / techhub-dev"
  fi
else
  echo "==> Skipping observability (SKIP_OBSERVABILITY=true)"
fi

echo ""
echo "Done. Verify with:"
echo "  kubectl get pods -n techhub"
echo "  kubectl logs -n techhub -l app=team-service --tail=50"
