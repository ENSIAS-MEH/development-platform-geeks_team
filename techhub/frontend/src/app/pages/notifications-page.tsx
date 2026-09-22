import { useEffect, useState } from "react";
import { Bell, Check, Loader2 } from "lucide-react";
import { Button } from "../components/ui/button";
import { Badge } from "../components/ui/badge";
import type { NotificationBackendResponse, NotificationBackendType } from "../types";
import {
  getNotificationsForUser,
  markNotificationAsRead,
  markAllNotificationsAsRead,
} from "../services/notification-api";

// ── Helpers ──────────────────────────────────────────────────────────────────

function getTypeLabel(type: NotificationBackendType): string {
  switch (type) {
    case "WELCOME_EMAIL":       return "Welcome";
    case "PASSWORD_CHANGED_EMAIL": return "Security";
    case "TEAM_INVITED":        return "Team Invite";
    case "TEAM_JOINED":         return "Team";
    default:                    return "Notification";
  }
}

function formatRelativeTime(dateStr: string): string {
  const diff = Date.now() - new Date(dateStr).getTime();
  const minutes = Math.floor(diff / 60_000);
  if (minutes < 1)  return "just now";
  if (minutes < 60) return `${minutes} minute${minutes > 1 ? "s" : ""} ago`;
  const hours = Math.floor(minutes / 60);
  if (hours < 24)   return `${hours} hour${hours > 1 ? "s" : ""} ago`;
  const days = Math.floor(hours / 24);
  return `${days} day${days > 1 ? "s" : ""} ago`;
}

function getCurrentUserId(): string | null {
  try {
    const user = localStorage.getItem("user");
    if (user) return JSON.parse(user)?.id ?? null;
  } catch {
    // ignore
  }
  return null;
}

// ── Component ─────────────────────────────────────────────────────────────────

export function NotificationsPage() {
  const [notifications, setNotifications] = useState<NotificationBackendResponse[]>([]);
  const [loading, setLoading]             = useState(true);
  const [error, setError]                 = useState<string | null>(null);

  const userId = getCurrentUserId();

  useEffect(() => {
    if (!userId) {
      setLoading(false);
      return;
    }
    getNotificationsForUser(userId)
      .then((data) => {
        const sorted = [...data].sort(
          (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        );
        setNotifications(sorted);
      })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [userId]);

  const handleMarkAsRead = async (id: string) => {
    try {
      const updated = await markNotificationAsRead(id);
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? updated : n))
      );
    } catch {
      // silently ignore — not critical
    }
  };

  const handleMarkAllAsRead = async () => {
    if (!userId) return;
    try {
      await markAllNotificationsAsRead(userId);
      setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
    } catch {
      // silently ignore
    }
  };

  const unreadCount = notifications.filter((n) => !n.read).length;

  // ── Render ──────────────────────────────────────────────────────────────────

  return (
    <div className="p-8">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-4xl font-bold text-[#1D2233] mb-2">Notifications</h1>
          <p className="text-lg text-[#717182]">
            {unreadCount > 0
              ? `${unreadCount} unread notification${unreadCount > 1 ? "s" : ""}`
              : "You're all caught up"}
          </p>
        </div>
        {unreadCount > 0 && (
          <Button
            variant="outline"
            className="border-[#BAC7CC]/30"
            onClick={handleMarkAllAsRead}
          >
            <Check className="w-4 h-4 mr-2" />
            Mark All as Read
          </Button>
        )}
      </div>

      {loading && (
        <div className="flex justify-center items-center py-20">
          <Loader2 className="w-8 h-8 animate-spin text-[#56B2BB]" />
        </div>
      )}

      {error && (
        <div className="bg-red-50 border border-red-200 rounded-xl p-6 text-red-700">
          Failed to load notifications: {error}
        </div>
      )}

      {!loading && !error && notifications.length === 0 && (
        <div className="flex flex-col items-center justify-center py-20 text-[#717182]">
          <Bell className="w-12 h-12 mb-4 opacity-30" />
          <p className="text-lg font-medium">No notifications yet</p>
          <p className="text-sm mt-1">We'll notify you when something happens.</p>
        </div>
      )}

      {!loading && !error && notifications.length > 0 && (
        <div className="bg-white rounded-xl border border-[#BAC7CC]/30 overflow-hidden">
          <div className="divide-y divide-[#BAC7CC]/20">
            {notifications.map((notif) => (
              <div
                key={notif.id}
                className={`p-6 flex gap-4 cursor-pointer hover:bg-[#f8fafb] transition-colors ${
                  !notif.read ? "bg-[#56B2BB]/5" : ""
                }`}
                onClick={() => !notif.read && handleMarkAsRead(notif.id)}
              >
                <div
                  className={`w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0 ${
                    !notif.read ? "bg-[#56B2BB]" : "bg-[#F0F4F8]"
                  }`}
                >
                  <Bell
                    className={`w-5 h-5 ${
                      !notif.read ? "text-white" : "text-[#717182]"
                    }`}
                  />
                </div>

                <div className="flex-1 min-w-0">
                  <div className="flex items-start justify-between mb-1">
                    <Badge variant="outline" className="text-xs">
                      {getTypeLabel(notif.type)}
                    </Badge>
                    {!notif.read && (
                      <div className="w-2 h-2 bg-[#56B2BB] rounded-full flex-shrink-0 mt-1" />
                    )}
                  </div>
                  <p className="text-[#1D2233] font-medium mb-0.5">
                    {notif.title ?? getTypeLabel(notif.type)}
                  </p>
                  <p className="text-sm text-[#717182] mb-1 truncate">
                    {notif.message ?? ""}
                  </p>
                  <p className="text-xs text-[#9CA3AF]">
                    {formatRelativeTime(notif.createdAt)}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
