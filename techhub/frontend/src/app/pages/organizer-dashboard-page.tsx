import { useCallback, useEffect, useState } from "react";
import { StatCard } from "../components/stat-card";
import { Button } from "../components/ui/button";
import { Badge } from "../components/ui/badge";
import { Input } from "../components/ui/input";
import { Link } from "react-router";
import { Calendar, Users, TrendingUp, Award, Loader2, Search } from "lucide-react";
import {
  searchEvents,
  publishEvent,
  formatEventDateRange,
  EventApiError,
  type EventResponseDto,
  type EventStatusApi,
} from "../services/event-api";
import { authService } from "../services/authService";

function statusLabel(status: string): string {
  switch (status) {
    case "PUBLISHED":
      return "Live";
    case "DRAFT":
      return "Draft";
    default:
      return status;
  }
}

function statusClass(status: string): string {
  if (status === "PUBLISHED") return "bg-green-100 text-green-700";
  if (status === "DRAFT") return "bg-yellow-100 text-yellow-700";
  return "bg-gray-100 text-gray-700";
}

export function OrganizerDashboardPage() {
  const user = authService.getCurrentUser();
  const [events, setEvents] = useState<EventResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [publishingId, setPublishingId] = useState<string | null>(null);
  const [keyword, setKeyword] = useState("");
  const [statusFilter, setStatusFilter] = useState<"all" | EventStatusApi>("all");

  const loadEvents = useCallback(async () => {
    if (!user?.id) {
      setError("Please log in to manage your events");
      setLoading(false);
      return;
    }
    try {
      setLoading(true);
      setError(null);
      const page = await searchEvents({
        organizerId: user.id,
        status: statusFilter === "all" ? undefined : statusFilter,
        keyword: keyword.trim() || undefined,
        size: 100,
      });
      setEvents(page.content);
    } catch (err) {
      setError(
        err instanceof EventApiError
          ? err.message
          : "Failed to load events. Restart event-service after code updates."
      );
    } finally {
      setLoading(false);
    }
  }, [user?.id, statusFilter, keyword]);

  useEffect(() => {
    const timer = setTimeout(loadEvents, 300);
    return () => clearTimeout(timer);
  }, [loadEvents]);

  const handlePublish = async (eventId: string) => {
    try {
      setPublishingId(eventId);
      await publishEvent(eventId);
      await loadEvents();
    } catch (err) {
      alert(err instanceof EventApiError ? err.message : "Publish failed");
    } finally {
      setPublishingId(null);
    }
  };

  const totalRegistrations = events.reduce((sum, e) => sum + e.participantCount, 0);
  const activeCount = events.filter((e) => e.status === "PUBLISHED").length;

  const stats = [
    {
      icon: Calendar,
      label: "Active Events",
      value: String(activeCount),
      trend: `${events.length} total`,
      trendUp: true,
    },
    {
      icon: Users,
      label: "Total Registrations",
      value: String(totalRegistrations),
    },
    {
      icon: TrendingUp,
      label: "Draft Events",
      value: String(events.filter((e) => e.status === "DRAFT").length),
    },
    {
      icon: Award,
      label: "Published",
      value: String(activeCount),
    },
  ];

  return (
    <div className="p-8">
      <div className="flex items-center justify-between mb-8 flex-wrap gap-4">
        <div>
          <h1 className="text-4xl font-bold text-[#1D2233] mb-2">Organizer Dashboard</h1>
          <p className="text-lg text-[#717182]">Manage your events and participants</p>
        </div>
        <Link to="/dashboard/organizer/events/create">
          <Button className="bg-[#56B2BB] hover:bg-[#56B2BB]/90">+ Create Event</Button>
        </Link>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        {stats.map((stat) => (
          <StatCard key={stat.label} {...stat} />
        ))}
      </div>

      <div className="bg-white rounded-xl border border-[#BAC7CC]/30 overflow-hidden">
        <div className="p-6 border-b border-[#BAC7CC]/20 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <h2 className="text-2xl font-bold text-[#1D2233]">My Events</h2>
          <div className="flex flex-wrap gap-3">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[#717182]" />
              <Input
                placeholder="Search by title..."
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                className="pl-9 w-48 bg-[#F0F4F8]"
              />
            </div>
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value as typeof statusFilter)}
              className="rounded-md border border-[#BAC7CC]/30 bg-[#F0F4F8] px-3 py-2 text-sm"
            >
              <option value="all">All statuses</option>
              <option value="PUBLISHED">Published</option>
              <option value="DRAFT">Draft</option>
            </select>
          </div>
        </div>

        {loading && (
          <div className="flex justify-center py-12">
            <Loader2 className="w-8 h-8 animate-spin text-[#56B2BB]" />
          </div>
        )}

        {error && !loading && (
          <p className="p-6 text-red-500">{error}</p>
        )}

        {!loading && !error && events.length === 0 && (
          <p className="p-6 text-[#717182]">
            No events match your filters.{" "}
            <Link to="/dashboard/organizer/events/create" className="text-[#56B2BB] hover:underline">
              Create one
            </Link>
          </p>
        )}

        <div className="divide-y divide-[#BAC7CC]/20">
          {events.map((event) => (
            <div key={event.id} className="p-6 hover:bg-[#F0F4F8] transition-colors">
              <div className="flex items-center justify-between flex-wrap gap-4">
                <div className="flex-1 min-w-[200px]">
                  <div className="flex items-center gap-3 mb-2 flex-wrap">
                    <h3 className="font-bold text-[#1D2233]">{event.title}</h3>
                    <Badge className={statusClass(event.status)}>
                      {statusLabel(event.status)}
                    </Badge>
                  </div>
                  <div className="flex gap-6 text-sm text-[#717182] flex-wrap">
                    <span>{formatEventDateRange(event.startDate, event.endDate)}</span>
                    <span>{event.participantCount} registrations</span>
                  </div>
                </div>
                <div className="flex gap-2 flex-wrap">
                  {event.status === "DRAFT" && (
                    <Button
                      size="sm"
                      className="bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white"
                      disabled={publishingId === event.id}
                      onClick={() => handlePublish(event.id)}
                    >
                      {publishingId === event.id ? "Publishing..." : "Publish"}
                    </Button>
                  )}
                  <Link to={`/dashboard/organizer/events/${event.id}/edit`}>
                    <Button variant="outline" size="sm">
                      Edit
                    </Button>
                  </Link>
                  <Link to={`/events/${event.id}`}>
                    <Button variant="outline" size="sm">
                      View
                    </Button>
                  </Link>
                  <Link to={`/dashboard/organizer/events/${event.id}/participants`}>
                    <Button variant="outline" size="sm">
                      Participants
                    </Button>
                  </Link>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
