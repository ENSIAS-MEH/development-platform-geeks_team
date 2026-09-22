// ═══════════════════════════════════════════════════════════════════
//  k6 Load Test — TechHub Team Service
//  Install: brew install k6
//  Run:     k6 run load-tests/team-service-load.js
//  Port:    8084 (governance §2.3)
// ═══════════════════════════════════════════════════════════════════

import http from "k6/http";
import { check, group, sleep } from "k6";
import { Counter, Rate, Trend } from "k6/metrics";

// ── Custom metrics ────────────────────────────────────────────────
const teamCreationErrors = new Counter("team_creation_errors");
const invitationFlowDuration = new Trend("invitation_flow_duration_ms");
const healthCheckFailRate = new Rate("health_check_fail_rate");

// ── Test configuration ────────────────────────────────────────────
// Stages:
//   0→1m  : ramp from 0 to 10 virtual users (warm-up)
//   1→4m  : hold at 50 VUs (sustained load)
//   4→5m  : ramp down to 0 (cool-down)
export const options = {
  stages: [
    { duration: "1m", target: 10 }, // warm-up
    { duration: "3m", target: 50 }, // sustained load
    { duration: "1m", target: 0 },  // cool-down
  ],
  thresholds: {
    // 95th percentile response time must be under 500ms
    http_req_duration: ["p(95)<500", "p(99)<1000"],
    // Less than 1% of requests should fail
    http_req_failed: ["rate<0.01"],
    // Custom metric thresholds
    health_check_fail_rate: ["rate<0.001"],
  },
};

const BASE_URL = __ENV.BASE_URL || "http://localhost:8084/api";

// ── Replace with a valid JWT from your local user-service ─────────
// Generate one via: POST http://localhost:8081/api/auth/login
const AUTH_HEADERS = {
  Authorization: `Bearer ${__ENV.JWT_TOKEN || "REPLACE_WITH_VALID_JWT"}`,
  "Content-Type": "application/json",
};

// ── Default test scenario ──────────────────────────────────────────
export default function () {
  group("Health Check", () => {
    const res = http.get(`${BASE_URL}/actuator/health`);
    const passed = check(res, {
      "health status is 200": (r) => r.status === 200,
      "status is UP": (r) => JSON.parse(r.body).status === "UP",
    });
    healthCheckFailRate.add(!passed);
    sleep(0.5);
  });

  group("Team CRUD Flow", () => {
    // 1. Create a team
    const createStart = Date.now();
    const createRes = http.post(
      `${BASE_URL}/teams`,
      JSON.stringify({
        name: `Load Test Team ${__VU}-${__ITER}`,
        maxMembers: 5,
        description: "k6 load test team",
      }),
      { headers: AUTH_HEADERS }
    );

    const created = check(createRes, {
      "create team status 200 or 201": (r) =>
        r.status === 200 || r.status === 201,
      "response has team id": (r) => {
        try {
          return JSON.parse(r.body).id !== undefined;
        } catch {
          return false;
        }
      },
    });

    if (!created) {
      teamCreationErrors.add(1);
      return; // abort this iteration if create failed
    }

    const teamId = JSON.parse(createRes.body).id;
    invitationFlowDuration.add(Date.now() - createStart);

    sleep(0.2);

    // 2. Fetch the team
    const getRes = http.get(`${BASE_URL}/teams/${teamId}`, {
      headers: AUTH_HEADERS,
    });
    check(getRes, {
      "get team status 200": (r) => r.status === 200,
    });

    sleep(0.2);

    // 3. Get my teams
    const myTeamsRes = http.get(`${BASE_URL}/teams/my`, {
      headers: AUTH_HEADERS,
    });
    check(myTeamsRes, {
      "get my teams status 200": (r) => r.status === 200,
      "my teams is array": (r) => Array.isArray(JSON.parse(r.body)),
    });

    sleep(1); // think time between iterations
  });

  group("Invitations Flow", () => {
    // Fetch pending invitations
    const invRes = http.get(`${BASE_URL}/invitations/my`, {
      headers: AUTH_HEADERS,
    });
    check(invRes, {
      "get invitations status 200": (r) => r.status === 200,
    });

    sleep(0.5);
  });
}

// ── Smoke test scenario (run with: k6 run --env SCENARIO=smoke) ───
export function smokeTest() {
  const res = http.get(`${BASE_URL}/actuator/health`);
  check(res, { "smoke: health UP": (r) => r.status === 200 });
}
