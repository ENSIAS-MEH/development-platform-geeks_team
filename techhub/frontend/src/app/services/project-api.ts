import type { SpringPage } from "../types";

const API_BASE = "/api/projects";

export type ProjectTypeApi =
  | "STARTUP_IDEA"
  | "OPEN_SOURCE"
  | "STUDENT_PROJECT"
  | "HACKATHON_PROJECT";

export type ProjectStatusApi = "OPEN" | "IN_PROGRESS" | "COMPLETED";
export type ProjectMemberRoleApi = "OWNER" | "MEMBER";

export interface ProjectResponseDto {
  id: string;
  title: string;
  description?: string;
  type: ProjectTypeApi;
  technologies: string[];
  skillsNeeded: string[];
  status: ProjectStatusApi;
  githubUrl?: string;
  ownerId: string;
  memberCount: number;
  userIsMember: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateProjectPayload {
  title: string;
  description?: string;
  type: ProjectTypeApi;
  technologies?: string[];
  skillsNeeded?: string[];
  githubUrl?: string;
}

export interface UpdateProjectPayload extends Partial<CreateProjectPayload> {
  status?: ProjectStatusApi;
}

export interface ProjectMemberResponseDto {
  id: string;
  projectId: string;
  userId: string;
  role: ProjectMemberRoleApi;
  joinedAt: string;
}

export interface ProjectCommentResponseDto {
  id: string;
  projectId: string;
  userId: string;
  content: string;
  createdAt: string;
}

export class ProjectApiError extends Error {
  constructor(
    public status: number,
    message: string,
    public body?: unknown
  ) {
    super(message);
    this.name = "ProjectApiError";
  }
}

function getOptionalUserId(): string | undefined {
  try {
    const userStr = localStorage.getItem("user");
    if (userStr) {
      const user = JSON.parse(userStr);
      if (user?.id) return user.id;
    }
  } catch {
    // Ignore malformed storage data.
  }
  return undefined;
}

async function request<T>(
  url: string,
  options: RequestInit = {},
  requireAuth = false
): Promise<T> {
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options.headers as Record<string, string>),
  };

  const token = localStorage.getItem("accessToken");
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const userId = getOptionalUserId();
  if (userId) {
    headers["X-User-Id"] = userId;
  } else if (requireAuth) {
    throw new ProjectApiError(401, "You must be logged in");
  }

  const response = await fetch(`${API_BASE}${url}`, { ...options, headers });

  if (!response.ok) {
    const body = await response.json().catch(() => ({}));
    const message =
      (body as { message?: string }).message ||
      response.statusText ||
      "Request failed";
    throw new ProjectApiError(response.status, message, body);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json();
}

export function formatProjectType(type: ProjectTypeApi): string {
  const map: Record<ProjectTypeApi, string> = {
    STARTUP_IDEA: "Startup Idea",
    OPEN_SOURCE: "Open Source",
    STUDENT_PROJECT: "Student Project",
    HACKATHON_PROJECT: "Hackathon Project",
  };
  return map[type] ?? type;
}

export function formatProjectStatus(status: ProjectStatusApi): string {
  const map: Record<ProjectStatusApi, string> = {
    OPEN: "Open to contributors",
    IN_PROGRESS: "In progress",
    COMPLETED: "Completed",
  };
  return map[status] ?? status;
}

export function uiTypeToProjectTypeApi(type: string): ProjectTypeApi {
  const map: Record<string, ProjectTypeApi> = {
    "Startup Idea": "STARTUP_IDEA",
    "Open Source": "OPEN_SOURCE",
    "Student Project": "STUDENT_PROJECT",
    "Hackathon Project": "HACKATHON_PROJECT",
  };
  return map[type] ?? "OPEN_SOURCE";
}

export function uiStatusToProjectStatusApi(status: string): ProjectStatusApi {
  const map: Record<string, ProjectStatusApi> = {
    "Open to contributors": "OPEN",
    "In progress": "IN_PROGRESS",
    Completed: "COMPLETED",
  };
  return map[status] ?? "OPEN";
}

export function projectToCardProps(project: ProjectResponseDto) {
  return {
    id: project.id,
    name: project.title,
    description: project.description || "No description provided.",
    techStack: project.technologies ? [...project.technologies] : [],
    status: formatProjectStatus(project.status),
    teamSize: project.memberCount,
    skillsNeeded: project.skillsNeeded ? [...project.skillsNeeded] : [],
  };
}

export async function searchProjects(params?: {
  type?: ProjectTypeApi;
  status?: ProjectStatusApi;
  keyword?: string;
  page?: number;
  size?: number;
}): Promise<SpringPage<ProjectResponseDto>> {
  const q = new URLSearchParams();
  if (params?.type) q.set("type", params.type);
  if (params?.status) q.set("status", params.status);
  if (params?.keyword) q.set("keyword", params.keyword);
  q.set("page", String(params?.page ?? 0));
  q.set("size", String(params?.size ?? 20));
  return request<SpringPage<ProjectResponseDto>>(`?${q.toString()}`);
}

export async function getProject(id: string): Promise<ProjectResponseDto> {
  return request<ProjectResponseDto>(`/${id}`);
}

export async function createProject(
  payload: CreateProjectPayload
): Promise<ProjectResponseDto> {
  return request<ProjectResponseDto>(
    "",
    { method: "POST", body: JSON.stringify(payload) },
    true
  );
}

export async function updateProject(
  id: string,
  payload: UpdateProjectPayload
): Promise<ProjectResponseDto> {
  return request<ProjectResponseDto>(
    `/${id}`,
    { method: "PUT", body: JSON.stringify(payload) },
    true
  );
}

export async function joinProject(id: string): Promise<ProjectMemberResponseDto> {
  return request<ProjectMemberResponseDto>(`/${id}/join`, { method: "POST" }, true);
}

export async function leaveProject(id: string): Promise<void> {
  await request(`/${id}/leave`, { method: "DELETE" }, true);
}

export async function getProjectMembers(
  id: string
): Promise<ProjectMemberResponseDto[]> {
  return request<ProjectMemberResponseDto[]>(`/${id}/members`);
}

export async function getProjectComments(
  id: string,
  page = 0,
  size = 20
): Promise<SpringPage<ProjectCommentResponseDto>> {
  return request<SpringPage<ProjectCommentResponseDto>>(
    `/${id}/comments?page=${page}&size=${size}`
  );
}

export async function addProjectComment(
  id: string,
  content: string
): Promise<ProjectCommentResponseDto> {
  return request<ProjectCommentResponseDto>(
    `/${id}/comments`,
    { method: "POST", body: JSON.stringify({ content }) },
    true
  );
}
