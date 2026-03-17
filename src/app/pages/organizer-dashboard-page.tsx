import { StatCard } from "../components/stat-card";
import { Button } from "../components/ui/button";
import { Badge } from "../components/ui/badge";
import { Link } from "react-router";
import { Calendar, Users, TrendingUp, Award } from "lucide-react";

export function OrganizerDashboardPage() {
  const stats = [
    { icon: Calendar, label: "Active Events", value: "3", trend: "+1 this month", trendUp: true },
    { icon: Users, label: "Total Registrations", value: "847", trend: "+12%", trendUp: true },
    { icon: TrendingUp, label: "Avg. Attendance", value: "85%", trend: "+3%", trendUp: true },
    { icon: Award, label: "Events Completed", value: "12" },
  ];

  const events = [
    { name: "AI Innovation Hackathon", status: "Live", registrations: 250, date: "March 25-27" },
    { name: "Web3 Workshop", status: "Draft", registrations: 0, date: "April 5" },
    { name: "Mobile Dev Conference", status: "Ended", registrations: 500, date: "Feb 15-16" },
  ];

  return (
    <div className="p-8">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-4xl font-bold text-[#1D2233] mb-2">Organizer Dashboard</h1>
          <p className="text-lg text-[#717182]">Manage your events and participants</p>
        </div>
        <Link to="/dashboard/organizer/events/create">
          <Button className="bg-[#56B2BB] hover:bg-[#56B2BB]/90">
            + Create Event
          </Button>
        </Link>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        {stats.map((stat) => (
          <StatCard key={stat.label} {...stat} />
        ))}
      </div>

      <div className="bg-white rounded-xl border border-[#BAC7CC]/30 overflow-hidden">
        <div className="p-6 border-b border-[#BAC7CC]/20">
          <h2 className="text-2xl font-bold text-[#1D2233]">My Events</h2>
        </div>
        <div className="divide-y divide-[#BAC7CC]/20">
          {events.map((event) => (
            <div key={event.name} className="p-6 hover:bg-[#F0F4F8] transition-colors">
              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <div className="flex items-center gap-3 mb-2">
                    <h3 className="font-bold text-[#1D2233]">{event.name}</h3>
                    <Badge 
                      className={
                        event.status === "Live" ? "bg-green-100 text-green-700" :
                        event.status === "Draft" ? "bg-yellow-100 text-yellow-700" :
                        "bg-gray-100 text-gray-700"
                      }
                    >
                      {event.status}
                    </Badge>
                  </div>
                  <div className="flex gap-6 text-sm text-[#717182]">
                    <span>{event.date}</span>
                    <span>{event.registrations} registrations</span>
                  </div>
                </div>
                <div className="flex gap-2">
                  <Button variant="outline" size="sm">Edit</Button>
                  <Button variant="outline" size="sm">Analytics</Button>
                  <Button variant="outline" size="sm">Participants</Button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
