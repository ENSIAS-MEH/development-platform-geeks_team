# TechHub — Team Service Runbook

> **Scope:** This file covers running `team-service` and the frontend locally,
> deploying to Minikube, and verifying the full DevOps pipeline
> (K8s pods, Prometheus, Grafana, GitHub Actions CI).
>
> **ArgoCD status:** NOT implemented yet — see §7 for what it will look like.

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Run team-service locally (dev mode)](#2-run-team-service-locally-dev-mode)
3. [Run frontend locally](#3-run-frontend-locally)
4. [Deploy to Minikube (full K8s)](#4-deploy-to-minikube-full-k8s)
5. [Check pods & cluster health](#5-check-pods--cluster-health)
6. [Prometheus — metrics](#6-prometheus--metrics)
7. [Grafana — dashboards & traces](#7-grafana--dashboards--traces)
8. [GitHub Actions CI — monitor & debug](#8-github-actions-ci--monitor--debug)
9. [ArgoCD — NOT implemented (future step)](#9-argocd--not-implemented-future-step)
10. [Quick-reference cheatsheet](#10-quick-reference-cheatsheet)

---

## 1. Prerequisites

| Tool | Required version | Install |
|---|---|---|
| Java | 21+ | `sdk install java 21-tem` |
| Maven | 3.9+ | bundled with `./mvnw` |
| Docker Desktop | any recent | docker.com |
| Minikube | 1.32+ | `winget install Kubernetes.minikube` |
| kubectl | 1.29+ | `winget install Kubernetes.kubectl` |
| Node.js | 18+ | `winget install OpenJS.NodeJS.LTS` |

Verify everything is working:

```bash
java -version          # openjdk 21
docker info            # Docker daemon running
minikube version       # v1.35+
kubectl version --client
node --version         # v18+
```

---

## 2. Run team-service locally (dev mode)

This mode uses **docker-compose** to start only the backing services
(Postgres, Redis, Kafka) while running the Spring Boot app directly on your
host JVM — fastest for development.

### Step 1 — Start backing services

```bash
cd techhub/team-service
docker compose -f docker-compose.dev.yml up -d
```

This starts:
- PostgreSQL on host port `5433` (avoids clash with any local Postgres)
- Redis on `6379`
- Kafka (KRaft mode) on `9094`

Verify they are healthy:

```bash
docker compose -f docker-compose.dev.yml ps
# All three should show status "healthy" or "running"
```

### Step 2 — Copy and fill the env file

```bash
cp .env.example .env
# .env is gitignored — safe to edit with real values
```

The `.env.example` already has sensible dev defaults. The only value you
**must** change is `JWT_SECRET` — it must match exactly what `user-service`
is using.

### Step 3 — Run the app

```bash
# IntelliJ / VS Code: just open the project and run TeamServiceApplication.java
# Terminal:
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The service starts on **http://localhost:8083**

### Step 4 — Verify

```bash
curl http://localhost:8083/api/actuator/health
# {"status":"UP","components":{...}}

curl http://localhost:8083/api/actuator/health/liveness
# {"status":"UP"}

curl http://localhost:8083/api/actuator/health/readiness
# {"status":"UP"}
```

Swagger UI (if enabled in dev profile):
```
http://localhost:8083/api/swagger-ui/index.html
```

### Stop backing services

```bash
docker compose -f docker-compose.dev.yml down
# Add -v to also delete persistent volumes (wipes the database)
docker compose -f docker-compose.dev.yml down -v
```

---

## 3. Run frontend locally

The frontend is a **Vite + React** app. In dev mode it uses Vite's dev server
with hot-reload. The nginx proxy config (`nginx.conf`) is only used in the
Docker/K8s deployment.

### Step 1 — Install dependencies

```bash
cd techhub/frontend
npm install
```

### Step 2 — Start the dev server

```bash
npm run dev
```

Opens on **http://localhost:5173**

Vite proxies API calls to the backend services (configured in `vite.config.ts`):
- `/api/auth/**` → user-service on port 8080
- `/api/users/**` → user-service on port 8080
- `/api/**` → team-service on port 8083
- `/notifications/**` → notification-service on port 8086

So team-service must be running on port 8083 for the team pages to work.

### Build for production (optional)

```bash
npm run build
# Output: techhub/frontend/dist/
```

---

## 4. Deploy to Minikube (full K8s)

### Step 1 — Start Minikube

```bash
minikube start --memory=4096 --cpus=4
# Verify it's running:
minikube status
```

### Step 2 — Point Docker to Minikube's daemon

All images must be built **inside Minikube's Docker daemon** — not your host.
This is because Minikube can't pull local images from your host machine.

```bash
# Windows PowerShell:
& minikube -p minikube docker-env --shell powershell | Invoke-Expression

# Or Git Bash / WSL:
eval $(minikube docker-env)
```

> **Important:** This only affects the current terminal session.
> You must re-run this every time you open a new terminal.

### Step 3 — Build the Docker image

```bash
cd techhub/team-service
docker build -t techhub/team-service:latest .
# Takes ~3-5 min first time (downloads Maven deps), ~30s with layer cache
```

Verify it's available inside Minikube:

```bash
minikube image ls | grep team-service
# techhub/team-service:latest
```

### Step 4 — Restore the JWT secret in secret.yaml

The committed `secret.yaml` has a placeholder `CHANGE_ME_...`.
Before applying, edit it locally (it's gitignored):

```bash
# In techhub/team-service/k8s/secret.yaml, set:
# JWT_SECRET: "44kZGaySDVJ87wlmYZnOqJKQmrQNYT1fZdJZNfCQV0caGrZFK6fFQh7MRkoJQ+F9FnZh2XfaACfV8hieLkrQBg=="
# (copy the exact value from user-service)
```

### Step 5 — Deploy everything

```bash
# From the repo root:
chmod +x infra/deploy-all.sh   # only needed once on Linux/Mac
bash infra/deploy-all.sh
```

This runs 6 ordered steps:
1. Creates `techhub` namespace
2. Starts dev backing services (Postgres, Redis, Kafka)
3. Applies ConfigMap + Secret
4. Deploys team-service (Deployment + Service)
5. Waits for rollout to complete
6. Deploys Prometheus, Loki, Tempo, Promtail, Grafana

**Skip flags:**
```bash
# Skip observability (faster if you only need the app):
SKIP_OBSERVABILITY=true bash infra/deploy-all.sh

# Skip dev backing services (if you manage them separately):
SKIP_DEV_DEPS=true bash infra/deploy-all.sh

# Dry run (preview without applying):
DRY_RUN=true bash infra/deploy-all.sh
```

### Step 6 — Access team-service

The service is exposed as ClusterIP (internal only). Use port-forward:

```bash
kubectl port-forward svc/team-service 8083:8083 -n techhub
```

Then test:
```bash
curl http://localhost:8083/api/actuator/health
```

---

## 5. Check pods & cluster health

### View all pods in the techhub namespace

```bash
kubectl get pods -n techhub
```

Expected output when everything is healthy:

```
NAME                            READY   STATUS    RESTARTS
grafana-xxx                     1/1     Running   0
kafka-xxx                       1/1     Running   0-2   # restarts during KRaft election are normal
loki-xxx                        1/1     Running   0
postgres-team-xxx               1/1     Running   0
prometheus-xxx                  1/1     Running   0
promtail-xxx (DaemonSet)        1/1     Running   0
redis-xxx                       1/1     Running   0
team-service-xxx                1/1     Running   0
tempo-xxx                       1/1     Running   0
```

> Kafka may show 1-2 restarts during startup — this is normal (KRaft leader
> election takes ~30s). The team-service handles it gracefully
> (`SPRING_KAFKA_ADMIN_FAIL_FAST=false`).

### Check a specific pod's logs

```bash
# team-service logs (tail the last 100 lines):
kubectl logs -n techhub -l app=team-service --tail=100

# Follow live:
kubectl logs -n techhub -l app=team-service -f

# Any crashing pod — add --previous to see the last crash:
kubectl logs -n techhub <pod-name> --previous
```

### Describe a pod (events, resource usage, probe results)

```bash
kubectl describe pod -n techhub -l app=team-service
# Scroll to "Events:" section at the bottom — most useful for debugging crashes
```

### Check resource usage

```bash
kubectl top pods -n techhub
kubectl top nodes
```

### Check all services and their ports

```bash
kubectl get svc -n techhub
```

### Restart a deployment (force redeploy)

```bash
kubectl rollout restart deployment/team-service -n techhub
# Monitor the rollout:
kubectl rollout status deployment/team-service -n techhub
```

### Force reimport an image after rebuild

```bash
# Rebuild the image (inside minikube docker-env):
docker build -t techhub/team-service:latest techhub/team-service/
# Then restart the deployment to pick it up:
kubectl rollout restart deployment/team-service -n techhub
```

---

## 6. Prometheus — metrics

Prometheus scrapes team-service's `/api/actuator/prometheus` endpoint every
15 seconds.

### Access Prometheus UI

```bash
kubectl port-forward svc/prometheus 9090:9090 -n techhub
```

Open: **http://localhost:9090**

### Check scrape targets (is team-service being scraped?)

1. Go to **Status → Targets** in the Prometheus UI
2. Look for `team-service` — status should be **UP**
3. If it shows DOWN, check:
   - `kubectl get pods -n techhub -l app=team-service` — is the pod Running?
   - `kubectl port-forward svc/team-service 8083:8083 -n techhub` then
     `curl http://localhost:8083/api/actuator/prometheus` — do metrics appear?

### Useful queries

Run these in the Prometheus query box:

```promql
# Is team-service up?
up{job="team-service"}

# JVM heap memory used (bytes):
jvm_memory_used_bytes{area="heap", application="team-service"}

# HTTP request rate (requests per second):
rate(http_server_requests_seconds_count{application="team-service"}[5m])

# HTTP 5xx error rate:
rate(http_server_requests_seconds_count{application="team-service", status=~"5.."}[5m])

# Database connection pool usage:
hikaricp_connections_active{application="team-service"}

# GC pause time:
rate(jvm_gc_pause_seconds_sum{application="team-service"}[5m])
```

---

## 7. Grafana — dashboards & traces

### Access Grafana

**Option A — Minikube direct URL (recommended):**
```bash
minikube service grafana -n techhub
# Opens the browser automatically with the correct URL
```

**Option B — port-forward:**
```bash
kubectl port-forward svc/grafana 3000:3000 -n techhub
# Then open: http://localhost:3000
```

**Login:** `admin` / `techhub-dev`

### Pre-wired datasources

All three datasources are provisioned automatically on first start:

| Datasource | UID | URL |
|---|---|---|
| Prometheus | `prometheus` | `http://prometheus:9090` |
| Loki | `loki` | `http://loki:3100` |
| Tempo | `tempo` | `http://tempo:3200` |

Verify them at: **Connections → Data Sources**

### View logs (Loki)

1. Left sidebar → **Explore**
2. Select datasource: **Loki**
3. Query:
   ```logql
   {app="team-service"}
   ```
4. Press **Run query**

Filter by log level:
```logql
{app="team-service"} |= "ERROR"
{app="team-service"} | json | level="WARN"
```

### View traces (Tempo)

1. Left sidebar → **Explore**
2. Select datasource: **Tempo**
3. Use **Search** tab, filter by `Service Name = team-service`
4. Click any trace → opens the waterfall view

**Trace → Logs correlation** is pre-wired: from a trace span, click the
Loki icon to jump to the matching log lines for that exact request.

### Build a dashboard

1. **Dashboards → New → New Dashboard**
2. **Add visualization**, select **Prometheus**
3. Paste any PromQL query from §6
4. Save with name `team-service — Overview`

---

## 8. GitHub Actions CI — monitor & debug

### Where the workflow lives

```
.github/workflows/team-service-ci.yml
```

### When it runs

| Event | Jobs that run |
|---|---|
| PR opened/updated targeting `main` | `Test & Coverage` only |
| Push merged to `main` | `Test & Coverage` → `Build & Push Image` |

The path filter means the CI only runs when files under
`techhub/team-service/**` or the workflow file itself change.

### How to watch a run

1. Go to your GitHub repo → **Actions** tab
2. Click `team-service CI/CD`
3. Click the latest run → expand each step

### What `Test & Coverage` does

```
actions/checkout
  └── actions/setup-java (Java 21, Maven cache)
       └── mvn verify -B  (with SPRING_PROFILES_ACTIVE=test)
            ├── compile
            ├── test       ← JUnit + Testcontainers + embedded Kafka
            ├── package
            └── jacoco:check  ← FAILS if line coverage < 80%
       └── Upload jacoco-report artifact (HTML, kept 7 days)
```

The `test` profile (`application-test.yml`) is self-contained:
- H2 in-memory database (no real Postgres needed)
- Redis auto-configuration excluded
- Kafka uses `@EmbeddedKafka` from spring-kafka-test
- Own JWT secret (`test_secret_32_characters_long!!`)

### What `Build & Push Image` does (main merge only)

```
docker/setup-buildx-action   (BuildKit with GHA layer cache)
  └── docker/login-action    (GHCR with GITHUB_TOKEN — no extra secrets)
       └── docker/metadata-action  (generates tags)
            └── docker/build-push-action
                 ├── push: ghcr.io/ensias-meh/team-service:sha-<7chars>
                 └── push: ghcr.io/ensias-meh/team-service:latest
```

### Debugging a failed CI run

**Coverage gate failed (`[ERROR] Rule violated for bundle 'team-service'`):**
```bash
# Download the jacoco-report artifact from the Actions run
# Open target/site/jacoco/index.html in a browser
# Classes shown in red = under 80% coverage
```

**Compilation error:**
```bash
# The error appears directly in the "Run tests + Jacoco" step output
# Fix it locally: mvn compile -B from techhub/team-service/
```

**Test failure:**
```bash
# Look for "Tests run: X, Failures: Y, Errors: Z" in the step output
# Run locally with the test profile: mvn test -DSPRING_PROFILES_ACTIVE=test
```

### Adding the JWT_SECRET GitHub secret (optional)

The workflow falls back to a hardcoded CI default if the secret is missing.
To use the real project secret:

1. GitHub repo → **Settings → Secrets and variables → Actions**
2. **New repository secret**
3. Name: `JWT_SECRET`
4. Value: paste the JWT secret from `user-service`

---

## 9. ArgoCD — GitOps setup

ArgoCD watches the `techhub/team-service/k8s/` directory in the `main` branch.
Every time a K8s manifest is merged to main, ArgoCD detects the drift and
re-applies automatically. This is GitOps: the git repo is the single source of
truth for what runs in the cluster.

**Complete pipeline after this step:**

```
developer → git push → GitHub Actions CI → GHCR image push
                                                    │
                              ArgoCD polls git every 3 min
                                                    ↓
                    manifest drift detected → kubectl apply
                                                    ↓
                         team-service pod rolling-updated
```

> **Secret note:** `secret.yaml` is gitignored. You must pre-apply it manually
> once before ArgoCD takes over. ArgoCD will not touch or prune it because the
> file is not in git.

---

### Step-by-step setup

#### Prerequisites — confirm before starting

```bash
# Minikube must be running:
minikube status

# team-service image must be built inside Minikube's Docker:
eval $(minikube docker-env)
docker images | grep team-service     # should show techhub/team-service:latest

# The secret must exist in the cluster (ArgoCD will NOT create it):
kubectl get secret team-service-secret -n techhub
# If missing, restore real JWT value in k8s/secret.yaml then:
# kubectl apply -f techhub/team-service/k8s/secret.yaml -n techhub
```

#### Step 1 — Run the setup script

```bash
bash infra/argocd-setup.sh
```

This script does 5 things automatically:
1. Creates the `argocd` namespace
2. Installs ArgoCD v2.11.3
3. Waits for all ArgoCD pods to be `Running`
4. Prints the initial `admin` password (save it)
5. Applies `infra/k8s/argocd/app-team-service.yaml` — registers team-service

Expected final output:
```
==> [5/5] Registering team-service Application in ArgoCD...
  ArgoCD setup complete!
  Open the UI: kubectl port-forward svc/argocd-server -n argocd 8080:443
  Login: admin / <generated-password>
```

#### Step 2 — Verify pods

```bash
kubectl get pods -n argocd
```

All 5 components should be `Running`:

```
NAME                                          READY   STATUS
argocd-application-controller-0              1/1     Running
argocd-dex-server-xxx                        1/1     Running
argocd-redis-xxx                             1/1     Running
argocd-repo-server-xxx                       1/1     Running
argocd-server-xxx                            1/1     Running
```

#### Step 3 — Open the ArgoCD UI

In a **separate terminal** (keep it open):
```bash
kubectl port-forward svc/argocd-server -n argocd 8080:443
```

Open: **https://localhost:8080**

> Accept the self-signed certificate warning in your browser (click
> Advanced → Proceed). This is normal for a local cluster — no real cert.

Login with:
- **Username:** `admin`
- **Password:** the value printed by the setup script  
  (or retrieve it again: `kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d`)

#### Step 4 — Check the team-service Application

On the ArgoCD home screen you'll see the `team-service` card.

| Field | Expected value |
|---|---|
| **Sync Status** | `Synced` |
| **Health Status** | `Healthy` |
| **Repository** | `ENSIAS-MEH/development-platform-geeks_team` |
| **Target** | `main` |
| **Path** | `techhub/team-service/k8s` |

Click the card → opens the resource graph:

```
Application: team-service
├── ConfigMap: team-service-config       ✅ Synced / Healthy
├── Deployment: team-service             ✅ Synced / Healthy
│   └── ReplicaSet → Pod (Running)
└── Service: team-service                ✅ Synced / Healthy
```

> If any resource shows **OutOfSync** (yellow), click **SYNC** → **SYNCHRONIZE**.
> If a resource shows **Degraded** (red), click it to see the K8s events.

#### Step 5 — Trigger a sync by changing a manifest

This proves the GitOps loop is working:

1. Edit any manifest, e.g. add a label to `configmap.yaml`:
   ```bash
   # In techhub/team-service/k8s/configmap.yaml, add under metadata.labels:
   #   test-label: "argocd-demo"
   ```
2. Commit and push to `main`
3. Wait up to 3 minutes (ArgoCD polls every 3 min by default)
4. Watch the ArgoCD UI — the ConfigMap will show `OutOfSync` briefly then
   flip back to `Synced` once ArgoCD applies it

Force an immediate sync without waiting:
```bash
# Via kubectl:
kubectl patch application team-service -n argocd --type merge \
  -p '{"operation":{"initiatedBy":{"username":"admin"},"sync":{}}}'

# Or click REFRESH then SYNC in the UI
```

---

### Check team-service health in ArgoCD

**Overall application health:**
```bash
kubectl get application team-service -n argocd
# NAME           SYNC STATUS   HEALTH STATUS
# team-service   Synced        Healthy
```

**Detailed sync/health breakdown:**
```bash
kubectl get application team-service -n argocd -o yaml | grep -A 20 "status:"
```

**Watch sync events live:**
```bash
kubectl get events -n argocd --field-selector involvedObject.name=team-service -w
```

**ArgoCD application history (rollback list):**
```bash
kubectl get application team-service -n argocd \
  -o jsonpath='{.status.history[*].deployedAt}{"\n"}'
```

---

### Rollback via ArgoCD

If a bad deployment reaches the cluster:

**Option A — UI rollback:**
1. Open https://localhost:8080 → `team-service`
2. Click **HISTORY AND ROLLBACK** (clock icon)
3. Find the last known-good revision
4. Click **Rollback** → **OK**

**Option B — kubectl rollback (bypasses ArgoCD):**
```bash
kubectl rollout undo deployment/team-service -n techhub
# ArgoCD will detect drift and re-sync from git unless you pause it first:
kubectl patch application team-service -n argocd --type merge \
  -p '{"spec":{"syncPolicy":{"automated":null}}}'
```

---

### What ArgoCD does NOT manage (intentional)

| Resource | Why excluded |
|---|---|
| `Secret team-service-secret` | Gitignored — pre-applied manually. ArgoCD ignores it. |
| `dev-deps/` (Postgres, Redis, Kafka) | `recurse: false` in Application spec — local dev only |
| Observability stack (Prometheus, Grafana…) | Separate `infra/k8s/observability/` — not wired to ArgoCD yet |
| `argocd` namespace resources | Self-managed by ArgoCD itself |

---

### Teardown ArgoCD

```bash
# Remove the Application (also removes all managed K8s resources due to finalizer):
kubectl delete application team-service -n argocd

# Uninstall ArgoCD completely:
kubectl delete namespace argocd

# Or just pause auto-sync (keeps ArgoCD but stops automatic deployments):
kubectl patch application team-service -n argocd --type merge \
  -p '{"spec":{"syncPolicy":{"automated":null}}}'
```

---

## 10. Quick-reference cheatsheet

```bash
# ── Local dev ──────────────────────────────────────────────────────────────
cd techhub/team-service
docker compose -f docker-compose.dev.yml up -d      # start backing services
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
curl http://localhost:8083/api/actuator/health

# ── Frontend ───────────────────────────────────────────────────────────────
cd techhub/frontend
npm run dev                                          # http://localhost:5173

# ── Minikube — first-time setup ────────────────────────────────────────────
minikube start --memory=4096 --cpus=4
eval $(minikube docker-env)                          # or PowerShell version
docker build -t techhub/team-service:latest techhub/team-service/
bash infra/deploy-all.sh

# ── Minikube — after code change ──────────────────────────────────────────
eval $(minikube docker-env)
docker build -t techhub/team-service:latest techhub/team-service/
kubectl rollout restart deployment/team-service -n techhub
kubectl rollout status deployment/team-service -n techhub

# ── Cluster inspection ─────────────────────────────────────────────────────
kubectl get pods -n techhub                          # all pods + status
kubectl logs -n techhub -l app=team-service -f       # live logs
kubectl describe pod -n techhub -l app=team-service  # events + probes
kubectl top pods -n techhub                          # CPU / memory

# ── Port-forwards (open in separate terminals) ─────────────────────────────
kubectl port-forward svc/team-service  8083:8083 -n techhub
kubectl port-forward svc/prometheus    9090:9090 -n techhub
kubectl port-forward svc/grafana       3000:3000 -n techhub
# OR open Grafana via minikube (auto-opens browser):
minikube service grafana -n techhub

# ── Grafana credentials ────────────────────────────────────────────────────
# URL:      http://localhost:3000  (or minikube service URL)
# Login:    admin
# Password: techhub-dev

# ── Run tests locally ──────────────────────────────────────────────────────
cd techhub/team-service
mvn verify -B                                        # tests + coverage gate
mvn test -DSPRING_PROFILES_ACTIVE=test               # tests only, no coverage

# ── ArgoCD setup ──────────────────────────────────────────────────────────
bash infra/argocd-setup.sh                           # install + register app
kubectl port-forward svc/argocd-server -n argocd 8080:443  # open UI

# ArgoCD health checks:
kubectl get application team-service -n argocd       # sync + health status
kubectl get pods -n argocd                           # ArgoCD components

# Force immediate sync (don't wait 3 min):
kubectl patch application team-service -n argocd --type merge \
  -p '{"operation":{"initiatedBy":{"username":"admin"},"sync":{}}}'

# Rollback to previous revision (UI is easier — see §9):
kubectl rollout undo deployment/team-service -n techhub

# ── Teardown ───────────────────────────────────────────────────────────────
kubectl delete application team-service -n argocd    # remove ArgoCD app first
kubectl delete namespace techhub                     # removes app resources
kubectl delete namespace argocd                      # removes ArgoCD
minikube stop                                        # pause cluster
minikube delete                                      # destroy cluster completely
```
