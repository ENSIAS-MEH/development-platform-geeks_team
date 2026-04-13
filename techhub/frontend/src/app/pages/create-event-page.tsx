import { Label } from "../components/ui/label";
import { Input } from "../components/ui/input";
import { Textarea } from "../components/ui/textarea";
import { Button } from "../components/ui/button";

export function CreateEventPage() {
  return (
    <div className="p-8">
      <div className="max-w-3xl mx-auto">
        <h1 className="text-4xl font-bold text-[#1D2233] mb-2">Create New Event</h1>
        <p className="text-lg text-[#717182] mb-8">Organize a hackathon, workshop, or conference</p>

        <div className="bg-white rounded-xl p-8 border border-[#BAC7CC]/30 shadow-sm">
          <form className="space-y-6">
            <div>
              <Label htmlFor="title">Event Title *</Label>
              <Input id="title" placeholder="e.g., AI Innovation Hackathon 2026" className="mt-1.5 bg-[#F0F4F8]" />
            </div>

            <div className="grid md:grid-cols-2 gap-4">
              <div>
                <Label htmlFor="type">Event Type</Label>
                <select id="type" className="mt-1.5 w-full rounded-md border border-[#BAC7CC]/30 bg-[#F0F4F8] px-3 py-2">
                  <option>Hackathon</option>
                  <option>Workshop</option>
                  <option>Conference</option>
                  <option>Meetup</option>
                </select>
              </div>
              <div>
                <Label htmlFor="location">Location</Label>
                <Input id="location" placeholder="Online or City, Country" className="mt-1.5 bg-[#F0F4F8]" />
              </div>
            </div>

            <div className="grid md:grid-cols-2 gap-4">
              <div>
                <Label htmlFor="start">Start Date</Label>
                <Input id="start" type="date" className="mt-1.5 bg-[#F0F4F8]" />
              </div>
              <div>
                <Label htmlFor="end">End Date</Label>
                <Input id="end" type="date" className="mt-1.5 bg-[#F0F4F8]" />
              </div>
            </div>

            <div>
              <Label htmlFor="description">Description *</Label>
              <Textarea id="description" rows={6} placeholder="Describe your event..." className="mt-1.5 bg-[#F0F4F8]" />
            </div>

            <div className="grid md:grid-cols-2 gap-4">
              <div>
                <Label htmlFor="capacity">Max Capacity</Label>
                <Input id="capacity" type="number" placeholder="500" className="mt-1.5 bg-[#F0F4F8]" />
              </div>
              <div>
                <Label htmlFor="deadline">Registration Deadline</Label>
                <Input id="deadline" type="date" className="mt-1.5 bg-[#F0F4F8]" />
              </div>
            </div>

            <div className="flex gap-4 pt-4">
              <Button type="submit" className="bg-[#56B2BB] hover:bg-[#56B2BB]/90 flex-1">Publish Event</Button>
              <Button type="button" variant="outline">Save as Draft</Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
