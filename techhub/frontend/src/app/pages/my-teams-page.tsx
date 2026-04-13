import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { Users, MessageSquare } from "lucide-react";

export function MyTeamsPage() {
  const teams = [
    { name: "AI Study Buddy Team", members: 5, type: "Project", activity: "Active" },
    { name: "Hackathon Squad", members: 4, type: "Event", activity: "Active" },
    { name: "Web3 Workshop Group", members: 3, type: "Event", activity: "Upcoming" },
  ];

  return (
    <div className="p-8">
      <h1 className="text-4xl font-bold text-[#1D2233] mb-2">My Teams</h1>
      <p className="text-lg text-[#717182] mb-8">Manage your project and event teams</p>

      <div className="grid md:grid-cols-2 gap-6">
        {teams.map((team) => (
          <div key={team.name} className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30 shadow-sm">
            <div className="flex items-start justify-between mb-4">
              <div>
                <h3 className="font-bold text-[#1D2233] mb-1">{team.name}</h3>
                <div className="flex items-center gap-2 text-sm text-[#717182]">
                  <Users className="w-4 h-4" />
                  <span>{team.members} members</span>
                </div>
              </div>
              <Badge className={team.activity === "Active" ? "bg-green-100 text-green-700" : "bg-yellow-100 text-yellow-700"}>
                {team.activity}
              </Badge>
            </div>
            <div className="flex gap-2">
              <Button className="flex-1 bg-[#56B2BB] hover:bg-[#56B2BB]/90">
                <MessageSquare className="w-4 h-4 mr-2" />
                Message Team
              </Button>
              <Button variant="outline">View</Button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
