import { useEffect, useState } from "react";
import { Navbar } from "../components/navbar";
import { EventCard } from "../components/event-card";
import { Input } from "../components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "../components/ui/select";
import { Badge } from "../components/ui/badge";
import { Search, Filter, Loader2 } from "lucide-react";
import {
  searchEvents,
  eventToCardProps,
  uiTypeToApi,
  EventApiError,
  type EventTypeApi,
} from "../services/event-api";

export function ExploreEventsPage() {
  const [searchQuery, setSearchQuery] = useState("");
  const [typeFilter, setTypeFilter] = useState("all");
  const [statusFilter, setStatusFilter] = useState<"PUBLISHED" | "all">("PUBLISHED");
  const [events, setEvents] = useState<ReturnType<typeof eventToCardProps>[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const load = async () => {
      try {
        setLoading(true);
        setError(null);
        const type =
          typeFilter === "all" ? undefined : (uiTypeToApi(typeFilter) as EventTypeApi);
        const page = await searchEvents({
          status: statusFilter === "all" ? undefined : statusFilter,
          keyword: searchQuery.trim() || undefined,
          type,
          size: 50,
        });
        setEvents(page.content.map(eventToCardProps));
      } catch (err) {
        const message =
          err instanceof EventApiError
            ? err.message
            : "Could not load events. Is event-service running on port 8082?";
        setError(message);
      } finally {
        setLoading(false);
      }
    };

    const timer = setTimeout(load, 300);
    return () => clearTimeout(timer);
  }, [searchQuery, typeFilter, statusFilter]);

  return (
    <div className="min-h-screen bg-[#F0F4F8]">
      <Navbar />

      <div className="max-w-7xl mx-auto px-6 py-12">
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-[#1D2233] mb-2">Explore Events</h1>
          <p className="text-lg text-[#717182]">
            Discover hackathons, workshops, conferences, and meetups
          </p>
        </div>

        <div className="bg-white rounded-xl p-6 mb-8 border border-[#BAC7CC]/30 shadow-sm">
          <div className="grid md:grid-cols-4 gap-4">
            <div className="md:col-span-2">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-[#717182]" />
                <Input
                  type="text"
                  placeholder="Search events..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10 bg-[#F0F4F8] border-[#BAC7CC]/30"
                />
              </div>
            </div>

            <Select value={typeFilter} onValueChange={setTypeFilter}>
              <SelectTrigger className="bg-[#F0F4F8] border-[#BAC7CC]/30">
                <SelectValue placeholder="Event Type" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Types</SelectItem>
                <SelectItem value="Hackathon">Hackathon</SelectItem>
                <SelectItem value="Workshop">Workshop</SelectItem>
                <SelectItem value="Conference">Conference</SelectItem>
                <SelectItem value="Meetup">Meetup</SelectItem>
              </SelectContent>
            </Select>

            <Select
              value={statusFilter}
              onValueChange={(v) => setStatusFilter(v as "PUBLISHED" | "all")}
            >
              <SelectTrigger className="bg-[#F0F4F8] border-[#BAC7CC]/30">
                <SelectValue placeholder="Status" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="PUBLISHED">Published only</SelectItem>
                <SelectItem value="all">All statuses</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div className="mt-4 flex flex-wrap gap-2">
            <span className="text-sm text-[#717182] flex items-center gap-2">
              <Filter className="w-4 h-4" />
              Popular tags:
            </span>
            <Badge variant="outline" className="cursor-pointer hover:bg-[#56B2BB]/10">AI/ML</Badge>
            <Badge variant="outline" className="cursor-pointer hover:bg-[#56B2BB]/10">Web Development</Badge>
            <Badge variant="outline" className="cursor-pointer hover:bg-[#56B2BB]/10">Blockchain</Badge>
          </div>
        </div>

        {loading && (
          <div className="flex justify-center py-16">
            <Loader2 className="w-8 h-8 animate-spin text-[#56B2BB]" />
          </div>
        )}

        {error && !loading && (
          <p className="text-center text-red-500 py-8">{error}</p>
        )}

        {!loading && !error && (
          <>
            <div className="mb-4">
              <p className="text-[#717182]">Showing {events.length} events</p>
            </div>

            {events.length === 0 ? (
              <p className="text-center text-[#717182] py-12">No published events yet.</p>
            ) : (
              <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
                {events.map((event) => (
                  <EventCard key={event.id} {...event} />
                ))}
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}
