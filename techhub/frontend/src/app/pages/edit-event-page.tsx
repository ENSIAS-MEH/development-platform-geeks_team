import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router";
import { Label } from "../components/ui/label";
import { Input } from "../components/ui/input";
import { Textarea } from "../components/ui/textarea";
import { Button } from "../components/ui/button";
import { Loader2 } from "lucide-react";
import {
  getEvent,
  updateEvent,
  publishEvent,
  deleteEvent,
  formatEventType,
  uiTypeToApi,
  dateInputToIso,
  isoToDateInput,
  EventApiError,
} from "../services/event-api";
import { authService } from "../services/authService";

export function EditEventPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const user = authService.getCurrentUser();

  const [title, setTitle] = useState("");
  const [type, setType] = useState("Hackathon");
  const [location, setLocation] = useState("");
  const [start, setStart] = useState("");
  const [end, setEnd] = useState("");
  const [description, setDescription] = useState("");
  const [capacity, setCapacity] = useState("100");
  const [status, setStatus] = useState<string>("DRAFT");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;
    const load = async () => {
      try {
        setLoading(true);
        const event = await getEvent(id);
        if (user?.id && event.organizerId !== user.id) {
          setError("You can only edit your own events");
          return;
        }
        setTitle(event.title);
        setType(formatEventType(event.type));
        setLocation(event.location || "");
        setStart(isoToDateInput(event.startDate));
        setEnd(isoToDateInput(event.endDate));
        setDescription(event.description || "");
        setCapacity(String(event.maxParticipants ?? 100));
        setStatus(event.status);
      } catch (err) {
        setError(err instanceof EventApiError ? err.message : "Failed to load event");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [id, user?.id]);

  const buildPayload = () => {
    if (!title.trim()) throw new Error("Event title is required");
    if (!start || !end) throw new Error("Start and end dates are required");
    if (end < start) throw new Error("End date must be after start date");
    const maxParticipants = parseInt(capacity, 10);
    if (Number.isNaN(maxParticipants) || maxParticipants < 2) {
      throw new Error("Capacity must be at least 2");
    }
    return {
      title: title.trim(),
      description: description.trim() || undefined,
      type: uiTypeToApi(type),
      startDate: dateInputToIso(start, false),
      endDate: dateInputToIso(end, true),
      location: location.trim() || undefined,
      maxParticipants,
    };
  };

  const handleSave = async () => {
    if (!id) return;
    try {
      setSaving(true);
      setError(null);
      await updateEvent(id, buildPayload());
      navigate("/dashboard/organizer");
    } catch (err) {
      setError(err instanceof EventApiError ? err.message : err instanceof Error ? err.message : "Save failed");
    } finally {
      setSaving(false);
    }
  };

  const handlePublish = async () => {
    if (!id) return;
    try {
      setSaving(true);
      setError(null);
      await updateEvent(id, buildPayload());
      await publishEvent(id);
      navigate("/dashboard/organizer");
    } catch (err) {
      setError(err instanceof EventApiError ? err.message : "Publish failed");
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!id || !confirm("Delete this event permanently?")) return;
    try {
      setSaving(true);
      await deleteEvent(id);
      navigate("/dashboard/organizer");
    } catch (err) {
      setError(err instanceof EventApiError ? err.message : "Delete failed");
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="p-8 flex justify-center">
        <Loader2 className="w-8 h-8 animate-spin text-[#56B2BB]" />
      </div>
    );
  }

  if (error && !title) {
    return (
      <div className="p-8">
        <p className="text-red-500 mb-4">{error}</p>
        <Link to="/dashboard/organizer" className="text-[#56B2BB] hover:underline">
          ← Back to dashboard
        </Link>
      </div>
    );
  }

  return (
    <div className="p-8">
      <div className="max-w-3xl mx-auto">
        <Link to="/dashboard/organizer" className="text-[#56B2BB] hover:underline text-sm mb-4 inline-block">
          ← Back to dashboard
        </Link>
        <h1 className="text-4xl font-bold text-[#1D2233] mb-2">Edit Event</h1>
        <p className="text-lg text-[#717182] mb-8">
          Status: <span className="font-medium">{status}</span>
        </p>

        <div className="bg-white rounded-xl p-8 border border-[#BAC7CC]/30 shadow-sm">
          <form
            className="space-y-6"
            onSubmit={(e) => {
              e.preventDefault();
              handleSave();
            }}
          >
            <div>
              <Label htmlFor="title">Event Title *</Label>
              <Input
                id="title"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                className="mt-1.5 bg-[#F0F4F8]"
                required
              />
            </div>

            <div className="grid md:grid-cols-2 gap-4">
              <div>
                <Label htmlFor="type">Event Type</Label>
                <select
                  id="type"
                  value={type}
                  onChange={(e) => setType(e.target.value)}
                  className="mt-1.5 w-full rounded-md border border-[#BAC7CC]/30 bg-[#F0F4F8] px-3 py-2"
                >
                  <option>Hackathon</option>
                  <option>Workshop</option>
                  <option>Conference</option>
                  <option>Meetup</option>
                  <option>Competition</option>
                </select>
              </div>
              <div>
                <Label htmlFor="location">Location</Label>
                <Input
                  id="location"
                  value={location}
                  onChange={(e) => setLocation(e.target.value)}
                  className="mt-1.5 bg-[#F0F4F8]"
                />
              </div>
            </div>

            <div className="grid md:grid-cols-2 gap-4">
              <div>
                <Label htmlFor="start">Start Date *</Label>
                <Input
                  id="start"
                  type="date"
                  value={start}
                  onChange={(e) => setStart(e.target.value)}
                  className="mt-1.5 bg-[#F0F4F8]"
                  required
                />
              </div>
              <div>
                <Label htmlFor="end">End Date *</Label>
                <Input
                  id="end"
                  type="date"
                  value={end}
                  onChange={(e) => setEnd(e.target.value)}
                  min={start}
                  className="mt-1.5 bg-[#F0F4F8]"
                  required
                />
              </div>
            </div>

            <div>
              <Label htmlFor="description">Description</Label>
              <Textarea
                id="description"
                rows={6}
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                className="mt-1.5 bg-[#F0F4F8]"
              />
            </div>

            <div>
              <Label htmlFor="capacity">Max Capacity *</Label>
              <Input
                id="capacity"
                type="number"
                min={2}
                value={capacity}
                onChange={(e) => setCapacity(e.target.value)}
                className="mt-1.5 bg-[#F0F4F8]"
                required
              />
            </div>

            {error && <p className="text-red-500 text-sm">{error}</p>}

            <div className="flex flex-wrap gap-3 pt-4">
              <Button type="submit" disabled={saving} className="bg-[#56B2BB] hover:bg-[#56B2BB]/90">
                {saving ? "Saving..." : "Save Changes"}
              </Button>
              {status === "DRAFT" && (
                <Button
                  type="button"
                  disabled={saving}
                  variant="outline"
                  onClick={handlePublish}
                >
                  Save & Publish
                </Button>
              )}
              <Button
                type="button"
                variant="destructive"
                disabled={saving}
                onClick={handleDelete}
              >
                Delete
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
