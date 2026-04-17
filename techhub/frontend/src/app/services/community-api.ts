/**
 * Community Service API Client
 * 
 * Centralized service layer for all calls to the Community Service backend.
 * The backend runs on port 8085 and all endpoints are prefixed with /api.
 * 
 * Uses the Vite proxy (configured in vite.config.ts) so requests go to
 * /api/... which gets proxied to http://localhost:8085/api/...
 */

// ─── Types ──────────────────────────────────────────────────────────────
import type {
  GroupResponse,
  GroupRequest,
  PostResponse,
  PostRequest,
  CommentResponse,
  CommentRequest,
  GroupMemberResponse,
  SpringPage,
} from "../types";

// ─── Config ─────────────────────────────────────────────────────────────

/** 
 * Base URL for API requests. 
 * In development, the Vite proxy forwards /api → http://localhost:8085/api
 * so we just use a relative path.
 */
const API_BASE = "/api";

/**
 * Mock user ID – used as X-User-Id header since there's no auth service yet.
 * This matches one of the seed users in the database migration.
 */
const MOCK_USER_ID = "00000000-0000-0000-0000-000000000001";

// ─── Helpers ────────────────────────────────────────────────────────────

async function request<T>(
  url: string,
  options: RequestInit = {}
): Promise<T> {
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    "X-User-Id": MOCK_USER_ID,
    ...(options.headers as Record<string, string> || {}),
  };

  const response = await fetch(`${API_BASE}${url}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({}));
    throw new ApiError(
      response.status,
      errorBody.message || response.statusText,
      errorBody
    );
  }

  // Some endpoints return 204 No Content
  if (response.status === 204) {
    return undefined as unknown as T;
  }

  return response.json();
}

export class ApiError extends Error {
  constructor(
    public status: number,
    message: string,
    public body?: any
  ) {
    super(message);
    this.name = "ApiError";
  }
}

// ═══════════════════════════════════════════════════════════════════════
// GROUPS
// ═══════════════════════════════════════════════════════════════════════

/** List public groups, optionally filtered by topic */
export async function getGroups(
  topic?: string,
  page = 0,
  size = 20
): Promise<SpringPage<GroupResponse>> {
  const params = new URLSearchParams({ page: String(page), size: String(size) });
  if (topic) params.set("topic", topic);
  return request(`/groups?${params}`);
}

/** Search groups by keyword */
export async function searchGroups(
  keyword: string,
  page = 0,
  size = 20
): Promise<SpringPage<GroupResponse>> {
  const params = new URLSearchParams({
    keyword,
    page: String(page),
    size: String(size),
  });
  return request(`/groups/search?${params}`);
}

/** Get a single group by ID */
export async function getGroupById(groupId: string): Promise<GroupResponse> {
  return request(`/groups/${groupId}`);
}

/** Create a new group */
export async function createGroup(data: GroupRequest): Promise<GroupResponse> {
  return request("/groups", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

/** Update a group */
export async function updateGroup(
  groupId: string,
  data: GroupRequest
): Promise<GroupResponse> {
  return request(`/groups/${groupId}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

/** Delete a group (owner only) */
export async function deleteGroup(groupId: string): Promise<void> {
  return request(`/groups/${groupId}`, { method: "DELETE" });
}

// ─── Membership ─────────────────────────────────────────────────────────

/** Join a group */
export async function joinGroup(groupId: string): Promise<GroupMemberResponse> {
  return request(`/groups/${groupId}/join`, { method: "POST" });
}

/** Leave a group */
export async function leaveGroup(groupId: string): Promise<void> {
  return request(`/groups/${groupId}/leave`, { method: "DELETE" });
}

/** List members of a group */
export async function getGroupMembers(
  groupId: string,
  page = 0,
  size = 20
): Promise<SpringPage<GroupMemberResponse>> {
  const params = new URLSearchParams({ page: String(page), size: String(size) });
  return request(`/groups/${groupId}/members?${params}`);
}

/** Remove a member (owner/moderator only) */
export async function removeMember(
  groupId: string,
  targetUserId: string
): Promise<void> {
  return request(`/groups/${groupId}/members/${targetUserId}`, {
    method: "DELETE",
  });
}

// ═══════════════════════════════════════════════════════════════════════
// POSTS
// ═══════════════════════════════════════════════════════════════════════

/** List posts in a group */
export async function getGroupPosts(
  groupId: string,
  page = 0,
  size = 20,
  sortByPopularity = false
): Promise<SpringPage<PostResponse>> {
  const params = new URLSearchParams({
    page: String(page),
    size: String(size),
    sortByPopularity: String(sortByPopularity),
  });
  return request(`/groups/${groupId}/posts?${params}`);
}

/** Get a single post */
export async function getPost(
  groupId: string,
  postId: string
): Promise<PostResponse> {
  return request(`/groups/${groupId}/posts/${postId}`);
}

/** Create a new post in a group */
export async function createPost(
  groupId: string,
  data: PostRequest
): Promise<PostResponse> {
  return request(`/groups/${groupId}/posts`, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

/** Update a post (author only) */
export async function updatePost(
  groupId: string,
  postId: string,
  data: PostRequest
): Promise<PostResponse> {
  return request(`/groups/${groupId}/posts/${postId}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

/** Delete a post */
export async function deletePost(
  groupId: string,
  postId: string
): Promise<void> {
  return request(`/groups/${groupId}/posts/${postId}`, { method: "DELETE" });
}

/** Upvote a post (atomic increment) */
export async function upvotePost(
  groupId: string,
  postId: string
): Promise<void> {
  return request(`/groups/${groupId}/posts/${postId}/upvote`, {
    method: "POST",
  });
}

/** Toggle pin on a post (moderator/owner only) */
export async function togglePinPost(
  groupId: string,
  postId: string
): Promise<PostResponse> {
  return request(`/groups/${groupId}/posts/${postId}/pin`, { method: "PUT" });
}

/** Get popular posts across all groups (cached) */
export async function getPopularPosts(
  page = 0,
  size = 20
): Promise<SpringPage<PostResponse>> {
  const params = new URLSearchParams({ page: String(page), size: String(size) });
  return request(`/posts/popular?${params}`);
}

// ═══════════════════════════════════════════════════════════════════════
// COMMENTS
// ═══════════════════════════════════════════════════════════════════════

/** List comments for a post (with nested replies) */
export async function getComments(
  groupId: string,
  postId: string,
  page = 0,
  size = 20
): Promise<SpringPage<CommentResponse>> {
  const params = new URLSearchParams({ page: String(page), size: String(size) });
  return request(`/groups/${groupId}/posts/${postId}/comments?${params}`);
}

/** Create a comment on a post */
export async function createComment(
  groupId: string,
  postId: string,
  data: CommentRequest
): Promise<CommentResponse> {
  return request(`/groups/${groupId}/posts/${postId}/comments`, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

/** Upvote a comment */
export async function upvoteComment(
  groupId: string,
  postId: string,
  commentId: string
): Promise<void> {
  return request(
    `/groups/${groupId}/posts/${postId}/comments/${commentId}/upvote`,
    { method: "POST" }
  );
}

/** Delete a comment (author only) */
export async function deleteComment(
  groupId: string,
  postId: string,
  commentId: string
): Promise<void> {
  return request(
    `/groups/${groupId}/posts/${postId}/comments/${commentId}`,
    { method: "DELETE" }
  );
}
