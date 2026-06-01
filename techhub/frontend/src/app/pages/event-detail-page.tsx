import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router";
import { Button } from "../components/ui/button";
import { Badge } from "../components/ui/badge";
import { Calendar, MapPin, Users, Loader2 } from "lucide-react";
import {
  getEvent,
  registerForEvent,
  cancelRegistration,
  formatEventType,
  formatEventDateRange,
  EventApiError,
  type EventResponseDto,
} from "../services/event-api";
import { authService } from "../services/authService";

export function EventDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const currentUser = authService.getCurrentUser();
  const [event, setEvent] = useState<EventResponseDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);

  const loadEvent = async () => {
    if (!id) return;
    try {
      setLoading(true);
      setError(null);
      const data = await getEvent(id);
      setEvent(data);
    } catch (err) {
      setError(
        err instanceof EventApiError ? err.message : "Failed to load event"
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadEvent();
  }, [id]);

  const handleRegister = async () => {
    if (!id) return;
    if (!authService.isAuthenticated()) {
      navigate("/auth/login");
      return;
    }
    try {
      setActionLoading(true);
      setActionError(null);
      if (event?.userRegistered) {
        await cancelRegistration(id);
        setEvent((prev) =>
          prev
            ? {
                ...prev,
                userRegistered: false,
                participantCount: Math.max(0, prev.participantCount - 1),
              }
            : prev
        );
      } else {
        await registerForEvent(id);
        setEvent((prev) =>
          prev
            ? {
                ...prev,
                userRegistered: true,
                participantCount: prev.participantCount + 1,
              }
            : prev
        );
      }
      await loadEvent();
    } catch (err) {
      setActionError(
        err instanceof EventApiError ? err.message : "Registration failed"
      );
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#F0F4F8] flex items-center justify-center">
        <Loader2 className="w-8 h-8 animate-spin text-[#56B2BB]" />
      </div>
    );
  }

  if (error || !event) {
    return (
      <div className="min-h-screen bg-[#F0F4F8] flex flex-col items-center justify-center gap-4">
        <p className="text-red-500">{error || "Event not found"}</p>
        <Link to="/explore/events" className="text-[#56B2BB] hover:underline">
          ← Back to Events
        </Link>
      </div>
    );
  }

  const dateLabel = formatEventDateRange(event.startDate, event.endDate);
  const canRegister = event.status === "PUBLISHED";
  const isOrganizer =
    currentUser?.id && event.organizerId === currentUser.id;

  return (
    <div className="min-h-screen bg-[#F0F4F8]">
      <div className="bg-[#1D2233] text-white py-8 px-6">
        <div className="max-w-5xl mx-auto">
          <Link to="/explore/events" className="text-[#56B2BB] hover:underline mb-4 inline-block">
            ← Back to Events
          </Link>
          <h1 className="text-4xl font-bold mb-4">{event.title}</h1>
          <div className="flex gap-3 items-center flex-wrap">
            <Badge className="bg-[#56B2BB] text-white">{formatEventType(event.type)}</Badge>
            <Badge variant="outline" className="text-white border-white/30">
              {event.status}
            </Badge>
            <div className="flex items-center gap-2 text-sm">
              <Users className="w-4 h-4" />
              <span>{event.participantCount} participants registered</span>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-5xl mx-auto px-6 py-8">
        <div className="grid lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2 space-y-6">
            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <h2 className="text-2xl font-bold text-[#1D2233] mb-4">About This Event</h2>
              <p className="text-[#717182] whitespace-pre-wrap">
                {event.description || "No description provided."}
              </p>
            </div>
          </div>

          <div className="space-y-6">
            {isOrganizer && id && (
              <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
                <Link to={`/dashboard/organizer/events/${id}/edit`}>
                  <Button variant="outline" className="w-full border-[#56B2BB] text-[#56B2BB]">
                    Edit Event
                  </Button>
                </Link>
              </div>
            )}
            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <Button
                className="w-full bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white"
                disabled={!canRegister || actionLoading}
                onClick={handleRegister}
              >
                {actionLoading
                  ? "Please wait..."
                  : event.userRegistered
                    ? "Cancel Registration"
                    : "Register Now"}
              </Button>
              {!canRegister && (
                <p className="text-xs text-[#717182] mt-2 text-center">
                  Registration is only open for published events.
                </p>
              )}
              {actionError && (
                <p className="text-red-500 text-sm mt-2 text-center">{actionError}</p>
              )}
            </div>

            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <h3 className="font-bold text-[#1D2233] mb-4">Event Details</h3>
              <div className="space-y-3 text-sm">
                <div className="flex gap-2">
                  <Calendar className="w-5 h-5 text-[#56B2BB]" />
                  <div>
                    <p className="font-medium">Date</p>
                    <p className="text-[#717182]">{dateLabel}</p>
                  </div>
                </div>
                <div className="flex gap-2">
                  <MapPin className="w-5 h-5 text-[#56B2BB]" />
                  <div>
                    <p className="font-medium">Location</p>
                    <p className="text-[#717182]">{event.location || "TBD"}</p>
                  </div>
                </div>
                {event.maxParticipants != null && (
                  <div className="flex gap-2">
                    <Users className="w-5 h-5 text-[#56B2BB]" />
                    <div>
                      <p className="font-medium">Capacity</p>
                      <p className="text-[#717182]">
                        {event.participantCount} / {event.maxParticipants}
                      </p>
                    </div>
                  </div>
                )}
              </div>
            </div>

            {event.tags && event.tags.length > 0 && (
              <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
                <h3 className="font-bold text-[#1D2233] mb-4">Tags</h3>
                <div className="flex flex-wrap gap-2">
                  {event.tags.map((tag) => (
                    <Badge key={tag} variant="outline">
                      {tag}
                    </Badge>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
