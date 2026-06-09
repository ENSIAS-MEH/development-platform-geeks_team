import type { NotificationBackendResponse } from "../types";

const BASE = "/notifications/api/v1/notifications";

async function request<T>(url: string, options: RequestInit = {}): Promise<T> {
  const token = localStorage.getItem("accessToken");
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...(options.headers as Record<string, string> || {}),
  };

  const res = await fetch(url, { ...options, headers });

  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.message || res.statusText);
  }

  if (res.status === 204) return undefined as unknown as T;
  return res.json();
}

export async function getNotificationsForUser(
  userId: string
): Promise<NotificationBackendResponse[]> {
  return request(`${BASE}/user/${userId}`);
}

export async function markNotificationAsRead(
  id: string
): Promise<NotificationBackendResponse> {
  return request(`${BASE}/${id}/read`, { method: "PUT" });
}

export async function markAllNotificationsAsRead(userId: string): Promise<void> {
  return request(`${BASE}/user/${userId}/read-all`, { method: "PUT" });
}

export function countUnread(notifications: NotificationBackendResponse[]): number {
  return notifications.filter((n) => !n.read).length;
}
