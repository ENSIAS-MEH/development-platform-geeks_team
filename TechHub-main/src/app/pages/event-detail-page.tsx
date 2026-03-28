import { Link } from "react-router";
import { Button } from "../components/ui/button";
import { Badge } from "../components/ui/badge";
import { Calendar, MapPin, Users, Clock } from "lucide-react";

export function EventDetailPage() {
  return (
    <div className="min-h-screen bg-[#F0F4F8]">
      <div className="bg-[#1D2233] text-white py-8 px-6">
        <div className="max-w-5xl mx-auto">
          <Link to="/explore/events" className="text-[#56B2BB] hover:underline mb-4 inline-block">
            ← Back to Events
          </Link>
          <h1 className="text-4xl font-bold mb-4">AI Innovation Hackathon 2026</h1>
          <div className="flex gap-3 items-center">
            <Badge className="bg-[#56B2BB] text-white">Hackathon</Badge>
            <div className="flex items-center gap-2 text-sm">
              <Users className="w-4 h-4" />
              <span>250 participants registered</span>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-5xl mx-auto px-6 py-8">
        <div className="grid lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2 space-y-6">
            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <h2 className="text-2xl font-bold text-[#1D2233] mb-4">About This Event</h2>
              <p className="text-[#717182]">
                Join us for an exciting 48-hour hackathon focused on AI innovation! Build amazing projects, 
                learn from industry experts, and compete for prizes. Whether you're a beginner or an expert, 
                this event has something for everyone.
              </p>
            </div>

            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <h3 className="font-bold text-[#1D2233] mb-4">Schedule</h3>
              <div className="space-y-4">
                <div className="flex gap-3">
                  <Clock className="w-5 h-5 text-[#56B2BB] mt-0.5" />
                  <div>
                    <p className="font-medium">Day 1 - March 25</p>
                    <p className="text-sm text-[#717182]">9:00 AM - Opening Ceremony</p>
                    <p className="text-sm text-[#717182]">10:00 AM - Hacking Begins</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="space-y-6">
            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <Button className="w-full bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white">
                Register Now
              </Button>
            </div>

            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <h3 className="font-bold text-[#1D2233] mb-4">Event Details</h3>
              <div className="space-y-3 text-sm">
                <div className="flex gap-2">
                  <Calendar className="w-5 h-5 text-[#56B2BB]" />
                  <div>
                    <p className="font-medium">Date</p>
                    <p className="text-[#717182]">March 25-27, 2026</p>
                  </div>
                </div>
                <div className="flex gap-2">
                  <MapPin className="w-5 h-5 text-[#56B2BB]" />
                  <div>
                    <p className="font-medium">Location</p>
                    <p className="text-[#717182]">Online</p>
                  </div>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <h3 className="font-bold text-[#1D2233] mb-4">Tags</h3>
              <div className="flex flex-wrap gap-2">
                <Badge variant="outline">AI/ML</Badge>
                <Badge variant="outline">Python</Badge>
                <Badge variant="outline">TensorFlow</Badge>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
