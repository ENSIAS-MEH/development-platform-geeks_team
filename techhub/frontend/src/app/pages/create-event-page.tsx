import { useState } from "react";
import { useNavigate } from "react-router";
import { Label } from "../components/ui/label";
import { Input } from "../components/ui/input";
import { Textarea } from "../components/ui/textarea";
import { Button } from "../components/ui/button";
import {
  createEvent,
  publishEvent,
  uiTypeToApi,
  dateInputToIso,
  EventApiError,
} from "../services/event-api";

function minFutureDate(): string {
  const d = new Date();
  d.setDate(d.getDate() + 1);
  return d.toISOString().split("T")[0];
}

export function CreateEventPage() {
  const navigate = useNavigate();
  const minDate = minFutureDate();
  const [title, setTitle] = useState("");
  const [type, setType] = useState("Hackathon");
  const [location, setLocation] = useState("");
  const [start, setStart] = useState("");
  const [end, setEnd] = useState("");
  const [description, setDescription] = useState("");
  const [capacity, setCapacity] = useState("100");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

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

  const handleCreate = async (publish: boolean) => {
    try {
      setLoading(true);
      setError(null);
      const payload = buildPayload();
      const created = await createEvent(payload);
      if (publish) {
        await publishEvent(created.id);
      }
      navigate("/dashboard/organizer");
    } catch (err) {
      if (err instanceof EventApiError) {
        setError(err.message);
      } else if (err instanceof Error) {
        setError(err.message);
      } else {
        setError("Failed to save event");
      }
    } finally {
      setLoading(false);
    }
  };

  const onSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    handleCreate(true);
  };

  return (
    <div className="p-8">
      <div className="max-w-3xl mx-auto">
        <h1 className="text-4xl font-bold text-[#1D2233] mb-2">Create New Event</h1>
        <p className="text-lg text-[#717182] mb-8">Organize a hackathon, workshop, or conference</p>

        <div className="bg-white rounded-xl p-8 border border-[#BAC7CC]/30 shadow-sm">
          <form className="space-y-6" onSubmit={onSubmit}>
            <div>
              <Label htmlFor="title">Event Title *</Label>
              <Input
                id="title"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="e.g., AI Innovation Hackathon 2026"
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
                  placeholder="Online or City, Country"
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
                  min={minDate}
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
                  min={start || minDate}
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
                placeholder="Describe your event..."
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
                placeholder="100"
                className="mt-1.5 bg-[#F0F4F8]"
                required
              />
            </div>

            {error && <p className="text-red-500 text-sm">{error}</p>}

            <div className="flex gap-4 pt-4">
              <Button
                type="submit"
                disabled={loading}
                className="bg-[#56B2BB] hover:bg-[#56B2BB]/90 flex-1"
              >
                {loading ? "Saving..." : "Publish Event"}
              </Button>
              <Button
                type="button"
                variant="outline"
                disabled={loading}
                onClick={() => handleCreate(false)}
              >
                Save as Draft
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
