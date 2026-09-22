import { StatCard } from "../components/stat-card";
import { Users, TrendingUp, MapPin, Award } from "lucide-react";
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar } from "recharts";

export function EventAnalyticsPage() {
  const stats = [
    { icon: Users, label: "Total Registrations", value: "250" },
    { icon: TrendingUp, label: "Profile Views", value: "1,240" },
    { icon: MapPin, label: "Countries", value: "15" },
    { icon: Award, label: "Engagement Rate", value: "78%" },
  ];

  const registrationData = [
    { date: "Week 1", registrations: 30 },
    { date: "Week 2", registrations: 60 },
    { date: "Week 3", registrations: 120 },
    { date: "Week 4", registrations: 250 },
  ];

  const skillsData = [
    { skill: "Python", count: 80 },
    { skill: "React", count: 65 },
    { skill: "ML", count: 55 },
    { skill: "Node.js", count: 45 },
    { skill: "Design", count: 35 },
  ];

  return (
    <div className="p-8">
      <h1 className="text-4xl font-bold text-[#1D2233] mb-8">Event Analytics</h1>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        {stats.map((stat) => (
          <StatCard key={stat.label} {...stat} />
        ))}
      </div>

      <div className="grid lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
          <h3 className="font-bold text-[#1D2233] mb-6">Registrations Over Time</h3>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={registrationData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="date" />
              <YAxis />
              <Tooltip />
              <Line type="monotone" dataKey="registrations" stroke="#56B2BB" strokeWidth={2} />
            </LineChart>
          </ResponsiveContainer>
        </div>

        <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
          <h3 className="font-bold text-[#1D2233] mb-6">Top Skills</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={skillsData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="skill" />
              <YAxis />
              <Tooltip />
              <Bar dataKey="count" fill="#56B2BB" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
}
