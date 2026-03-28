import { StatCard } from "../components/stat-card";
import { EventCard } from "../components/event-card";
import { ProjectCard } from "../components/project-card";
import { Calendar, FolderGit2, Users, TrendingUp, Bell } from "lucide-react";
import { Badge } from "../components/ui/badge";

export function DashboardPage() {
  const stats = [
    { icon: Calendar, label: "Registered Events", value: "5", trend: "+2 this week", trendUp: true },
    { icon: FolderGit2, label: "Active Projects", value: "3", trend: "1 new contribution", trendUp: true },
    { icon: Users, label: "Team Members", value: "12", trend: "+4 this month", trendUp: true },
    { icon: TrendingUp, label: "Profile Views", value: "89", trend: "+15%", trendUp: true },
  ];

  const upcomingEvents = [
    {
      id: "1",
      title: "AI Innovation Hackathon 2026",
      organizer: "TechCorp",
      date: "March 25-27, 2026",
      location: "Online",
      participants: 250,
      tags: ["AI/ML", "Python"],
      type: "Hackathon"
    },
    {
      id: "2",
      title: "Web3 Workshop Series",
      organizer: "Blockchain Academy",
      date: "April 5, 2026",
      location: "San Francisco, CA",
      participants: 80,
      tags: ["Blockchain", "Solidity"],
      type: "Workshop"
    },
  ];

  const myProjects = [
    {
      id: "1",
      name: "AI Study Buddy",
      description: "An AI-powered learning assistant for students",
      techStack: ["Python", "TensorFlow", "React"],
      status: "Open to contributors",
      teamSize: 5,
    },
  ];

  const activities = [
    { user: "Sarah Chen", action: "joined your project", project: "AI Study Buddy", time: "2 hours ago" },
    { user: "Mike Johnson", action: "commented on", project: "EcoTracker", time: "5 hours ago" },
    { user: "Alex Rivera", action: "registered for", project: "AI Innovation Hackathon", time: "1 day ago" },
    { user: "Emma Wilson", action: "sent you a team invite", project: "Web3 Workshop", time: "2 days ago" },
  ];

  return (
    <div className="p-8">
      <div className="mb-8">
        <h1 className="text-4xl font-bold text-[#1D2233] mb-2">Welcome back, Alex!</h1>
        <p className="text-lg text-[#717182]">Here's what's happening with your projects and events</p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        {stats.map((stat) => (
          <StatCard key={stat.label} {...stat} />
        ))}
      </div>

      <div className="grid lg:grid-cols-3 gap-8">
        {/* Main Content */}
        <div className="lg:col-span-2 space-y-8">
          {/* Upcoming Events */}
          <div>
            <h2 className="text-2xl font-bold text-[#1D2233] mb-4">Upcoming Events</h2>
            <div className="grid gap-6">
              {upcomingEvents.map((event) => (
                <EventCard key={event.id} {...event} />
              ))}
            </div>
          </div>

          {/* My Projects */}
          <div>
            <h2 className="text-2xl font-bold text-[#1D2233] mb-4">My Projects</h2>
            <div className="grid gap-6">
              {myProjects.map((project) => (
                <ProjectCard key={project.id} {...project} />
              ))}
            </div>
          </div>
        </div>

        {/* Sidebar */}
        <div className="space-y-6">
          {/* Notifications */}
          <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30 shadow-sm">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-bold text-[#1D2233]">Notifications</h3>
              <Bell className="w-5 h-5 text-[#56B2BB]" />
            </div>
            <div className="space-y-3">
              <div className="p-3 bg-[#F0F4F8] rounded-lg">
                <p className="text-sm font-medium text-[#1D2233]">New team invite</p>
                <p className="text-xs text-[#717182] mt-1">Emma invited you to Web3 Workshop team</p>
                <p className="text-xs text-[#56B2BB] mt-2">2 hours ago</p>
              </div>
              <div className="p-3 bg-[#F0F4F8] rounded-lg">
                <p className="text-sm font-medium text-[#1D2233]">Event reminder</p>
                <p className="text-xs text-[#717182] mt-1">AI Hackathon starts in 8 days</p>
                <p className="text-xs text-[#56B2BB] mt-2">1 day ago</p>
              </div>
            </div>
          </div>

          {/* Activity Feed */}
          <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30 shadow-sm">
            <h3 className="font-bold text-[#1D2233] mb-4">Recent Activity</h3>
            <div className="space-y-4">
              {activities.map((activity, index) => (
                <div key={index} className="flex gap-3">
                  <div className="w-8 h-8 bg-[#56B2BB]/10 rounded-full flex items-center justify-center flex-shrink-0">
                    <span className="text-xs font-bold text-[#56B2BB]">
                      {activity.user.charAt(0)}
                    </span>
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm text-[#1D2233]">
                      <span className="font-medium">{activity.user}</span>{" "}
                      {activity.action}{" "}
                      <span className="font-medium">{activity.project}</span>
                    </p>
                    <p className="text-xs text-[#717182] mt-0.5">{activity.time}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Suggested Collaborators */}
          <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30 shadow-sm">
            <h3 className="font-bold text-[#1D2233] mb-4">Suggested Collaborators</h3>
            <div className="space-y-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-[#56B2BB]/10 rounded-full flex items-center justify-center">
                  <span className="font-bold text-[#56B2BB]">JD</span>
                </div>
                <div className="flex-1">
                  <p className="text-sm font-medium text-[#1D2233]">Jane Doe</p>
                  <div className="flex gap-1 mt-1">
                    <Badge variant="outline" className="text-xs">React</Badge>
                    <Badge variant="outline" className="text-xs">Python</Badge>
                  </div>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-[#56B2BB]/10 rounded-full flex items-center justify-center">
                  <span className="font-bold text-[#56B2BB]">BS</span>
                </div>
                <div className="flex-1">
                  <p className="text-sm font-medium text-[#1D2233]">Bob Smith</p>
                  <div className="flex gap-1 mt-1">
                    <Badge variant="outline" className="text-xs">Node.js</Badge>
                    <Badge variant="outline" className="text-xs">AWS</Badge>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
