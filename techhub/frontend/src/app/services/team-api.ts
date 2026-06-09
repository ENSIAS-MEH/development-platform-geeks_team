const API_BASE = "/api/teams";

export interface TeamResponse {
  id: string;
  name: string;
  description?: string;
  maxMembers: number;
  currentMembers: number;
  remainingSlots: number;
  status: "OPEN" | "FULL";
  ownerId: string;
  isOwner: boolean;
  createdAt: string;
}

export interface SpringPage<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface CreateTeamPayload {
  name: string;
  description?: string;
  maxMembers: number;
}

export interface UpdateTeamPayload {
  name?: string;
  description?: string;
  maxMembers?: number;
}

export class TeamApiError extends Error {
  constructor(
    public status: number,
    message: string,
    public body?: unknown
  ) {
    super(message);
    this.name = "TeamApiError";
  }
}

function getHeaders(): Record<string, string> {
  const headers: Record<string, string> = { "Content-Type": "application/json" };
  const token = localStorage.getItem("accessToken");
  if (token) headers.Authorization = `Bearer ${token}`;
  try {
    const user = JSON.parse(localStorage.getItem("user") ?? "{}");
    if (user?.id) headers["X-User-Id"] = user.id;
  } catch { /* ignore */ }
  return headers;
}

async function request<T>(url: string, options: RequestInit = {}, requireAuth = false): Promise<T> {
  const headers = getHeaders();
  if (requireAuth && !headers.Authorization) throw new TeamApiError(401, "You must be logged in");

  const response = await fetch(`${API_BASE}${url}`, { ...options, headers });

  if (!response.ok) {
    const body = await response.json().catch(() => ({}));
    const message = (body as { message?: string }).message || response.statusText || "Request failed";
    throw new TeamApiError(response.status, message, body);
  }

  if (response.status === 204) return undefined as T;
  return response.json();
}

export async function createTeam(payload: CreateTeamPayload): Promise<TeamResponse> {
  return request<TeamResponse>("", { method: "POST", body: JSON.stringify(payload) }, true);
}

export async function getTeam(id: string): Promise<TeamResponse> {
  return request<TeamResponse>(`/${id}`);
}

export async function getMyTeams(page = 0, size = 20): Promise<SpringPage<TeamResponse>> {
  return request<SpringPage<TeamResponse>>(`/my-teams?page=${page}&size=${size}`, {}, true);
}

export async function updateTeam(id: string, payload: UpdateTeamPayload): Promise<TeamResponse> {
  return request<TeamResponse>(`/${id}`, { method: "PUT", body: JSON.stringify(payload) }, true);
}

export async function deleteTeam(id: string): Promise<void> {
  await request(`/${id}`, { method: "DELETE" }, true);
}
