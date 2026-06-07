#!/usr/bin/env bash
# argocd-setup.sh — Install ArgoCD on Minikube and register team-service.
#
# Prerequisites:
#   - Minikube running          (minikube start)
#   - kubectl configured        (kubectl config current-context == minikube)
#   - team-service image built  (eval $(minikube docker-env) && docker build ...)
#   - secret.yaml pre-applied   (kubectl apply -f k8s/secret.yaml -n techhub)
#
# Usage:
#   bash infra/argocd-setup.sh
#
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ARGOCD_VERSION="v2.11.3"   # pin to avoid surprises; update deliberately
ARGOCD_NS="argocd"

# ── 0. Sanity checks ──────────────────────────────────────────────────────────
echo "==> Checking prerequisites..."

if ! kubectl cluster-info &>/dev/null; then
  echo "ERROR: kubectl cannot reach the cluster. Run: minikube start"
  exit 1
fi

CONTEXT=$(kubectl config current-context 2>/dev/null || echo "none")
echo "     Cluster context: $CONTEXT"

# ── 1. Create argocd namespace ────────────────────────────────────────────────
echo ""
echo "==> [1/5] Creating namespace $ARGOCD_NS..."
kubectl create namespace "$ARGOCD_NS" --dry-run=client -o yaml | kubectl apply -f -

# ── 2. Install ArgoCD ─────────────────────────────────────────────────────────
echo ""
echo "==> [2/5] Installing ArgoCD $ARGOCD_VERSION..."
kubectl apply -n "$ARGOCD_NS" \
  -f "https://raw.githubusercontent.com/argoproj/argo-cd/$ARGOCD_VERSION/manifests/install.yaml"

# ── 3. Wait for ArgoCD to be ready ───────────────────────────────────────────
echo ""
echo "==> [3/5] Waiting for ArgoCD server to be ready (may take 2-3 min)..."
kubectl rollout status deployment/argocd-server -n "$ARGOCD_NS" --timeout=180s
kubectl rollout status deployment/argocd-repo-server -n "$ARGOCD_NS" --timeout=120s
kubectl rollout status deployment/argocd-application-controller -n "$ARGOCD_NS" --timeout=120s 2>/dev/null || \
  kubectl rollout status statefulset/argocd-application-controller -n "$ARGOCD_NS" --timeout=120s

echo "     ArgoCD is ready."

# ── 4. Retrieve initial admin password ───────────────────────────────────────
echo ""
echo "==> [4/5] Getting initial admin password..."
ARGOCD_PASSWORD=$(kubectl -n "$ARGOCD_NS" get secret argocd-initial-admin-secret \
  -o jsonpath="{.data.password}" | base64 -d)

echo ""
echo "  ┌────────────────────────────────────────────────────────────────────┐"
echo "  │  ArgoCD UI credentials                                             │"
echo "  │  Username : admin                                                  │"
printf "  │  Password : %-55s│\n" "$ARGOCD_PASSWORD"
echo "  │                                                                    │"
echo "  │  SAVE this password — the secret is deleted after first login on  │"
echo "  │  newer ArgoCD versions.                                            │"
echo "  └────────────────────────────────────────────────────────────────────┘"

# ── 5. Register the team-service Application ─────────────────────────────────
echo ""
echo "==> [5/5] Registering team-service Application in ArgoCD..."

# Ensure techhub namespace exists (ArgoCD app-of-apps pattern needs it for sync)
kubectl create namespace techhub --dry-run=client -o yaml | kubectl apply -f -

kubectl apply -f "$SCRIPT_DIR/k8s/argocd/app-team-service.yaml"

echo ""
echo "     Application registered. ArgoCD will sync within ~30 seconds."

# ── Done ─────────────────────────────────────────────────────────────────────
echo ""
echo "==========================================================================="
echo "  ArgoCD setup complete!"
echo ""
echo "  Open the UI (in a separate terminal):"
echo "    kubectl port-forward svc/argocd-server -n argocd 8080:443"
echo "    Then open: https://localhost:8080  (accept the self-signed cert)"
echo "    Login: admin / $ARGOCD_PASSWORD"
echo ""
echo "  Watch sync status:"
echo "    kubectl get applications -n argocd"
echo "    kubectl get application team-service -n argocd -o yaml"
echo ""
echo "  Trigger a manual sync (if automated sync is not picking up changes):"
echo "    kubectl patch application team-service -n argocd --type merge \\"
echo "      -p '{\"operation\":{\"initiatedBy\":{\"username\":\"admin\"},\"sync\":{}}}'"
echo "==========================================================================="
