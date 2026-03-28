import { useState } from "react";
import { Navbar } from "../components/navbar";
import { EventCard } from "../components/event-card";
import { Input } from "../components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "../components/ui/select";
import { Badge } from "../components/ui/badge";
import { Search, Filter } from "lucide-react";

export function ExploreEventsPage() {
  const [searchQuery, setSearchQuery] = useState("");
  
  const events = [
    {
      id: "1",
      title: "AI Innovation Hackathon 2026",
      organizer: "TechCorp",
      date: "March 25-27, 2026",
      location: "Online",
      participants: 250,
      tags: ["AI/ML", "Python", "TensorFlow"],
      type: "Hackathon"
    },
    {
      id: "2",
      title: "Web3 Workshop Series",
      organizer: "Blockchain Academy",
      date: "April 5, 2026",
      location: "San Francisco, CA",
      participants: 80,
      tags: ["Blockchain", "Solidity", "Web3"],
      type: "Workshop"
    },
    {
      id: "3",
      title: "Mobile Dev Conference",
      organizer: "DevCommunity",
      date: "April 15-16, 2026",
      location: "New York, NY",
      participants: 500,
      tags: ["React Native", "Flutter", "Mobile"],
      type: "Conference"
    },
    {
      id: "4",
      title: "Cloud Computing Meetup",
      organizer: "CloudExperts",
      date: "March 30, 2026",
      location: "Seattle, WA",
      participants: 120,
      tags: ["AWS", "Azure", "GCP"],
      type: "Meetup"
    },
    {
      id: "5",
      title: "Full Stack Bootcamp",
      organizer: "CodeAcademy",
      date: "April 10-12, 2026",
      location: "Online",
      participants: 200,
      tags: ["React", "Node.js", "MongoDB"],
      type: "Workshop"
    },
    {
      id: "6",
      title: "Cybersecurity Summit",
      organizer: "SecureNet",
      date: "April 20, 2026",
      location: "Austin, TX",
      participants: 350,
      tags: ["Security", "Ethical Hacking", "InfoSec"],
      type: "Conference"
    },
  ];

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

        {/* Filters */}
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
            
            <Select defaultValue="all">
              <SelectTrigger className="bg-[#F0F4F8] border-[#BAC7CC]/30">
                <SelectValue placeholder="Event Type" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Types</SelectItem>
                <SelectItem value="hackathon">Hackathon</SelectItem>
                <SelectItem value="workshop">Workshop</SelectItem>
                <SelectItem value="conference">Conference</SelectItem>
                <SelectItem value="meetup">Meetup</SelectItem>
              </SelectContent>
            </Select>
            
            <Select defaultValue="upcoming">
              <SelectTrigger className="bg-[#F0F4F8] border-[#BAC7CC]/30">
                <SelectValue placeholder="Date" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="upcoming">Upcoming</SelectItem>
                <SelectItem value="this-week">This Week</SelectItem>
                <SelectItem value="this-month">This Month</SelectItem>
                <SelectItem value="past">Past Events</SelectItem>
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
            <Badge variant="outline" className="cursor-pointer hover:bg-[#56B2BB]/10">Mobile</Badge>
            <Badge variant="outline" className="cursor-pointer hover:bg-[#56B2BB]/10">Cloud</Badge>
          </div>
        </div>

        {/* Results */}
        <div className="mb-4">
          <p className="text-[#717182]">Showing {events.length} events</p>
        </div>

        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {events.map((event) => (
            <EventCard key={event.id} {...event} />
          ))}
        </div>
      </div>
    </div>
  );
}
