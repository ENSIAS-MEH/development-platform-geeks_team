import { Button } from "../components/ui/button";
import { Badge } from "../components/ui/badge";
import { Input } from "../components/ui/input";
import { Search, Download } from "lucide-react";

export function ManageParticipantsPage() {
  const participants = [
    { name: "Sarah Chen", email: "sarah@example.com", skills: ["Python", "ML"], team: "Team A", date: "March 10, 2026" },
    { name: "Mike Johnson", email: "mike@example.com", skills: ["React", "Node"], team: "Team B", date: "March 11, 2026" },
    { name: "Emma Wilson", email: "emma@example.com", skills: ["UI/UX", "Figma"], team: "Unassigned", date: "March 12, 2026" },
  ];

  return (
    <div className="p-8">
      <h1 className="text-4xl font-bold text-[#1D2233] mb-8">Manage Participants</h1>

      <div className="bg-white rounded-xl border border-[#BAC7CC]/30 overflow-hidden">
        <div className="p-6 border-b border-[#BAC7CC]/20">
          <div className="flex items-center justify-between">
            <div className="relative flex-1 max-w-md">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-[#717182]" />
              <Input placeholder="Search participants..." className="pl-10 bg-[#F0F4F8]" />
            </div>
            <Button variant="outline">
              <Download className="w-4 h-4 mr-2" />
              Export CSV
            </Button>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-[#F0F4F8]">
              <tr>
                <th className="text-left p-4 text-sm font-medium text-[#717182]">Name</th>
                <th className="text-left p-4 text-sm font-medium text-[#717182]">Email</th>
                <th className="text-left p-4 text-sm font-medium text-[#717182]">Skills</th>
                <th className="text-left p-4 text-sm font-medium text-[#717182]">Team</th>
                <th className="text-left p-4 text-sm font-medium text-[#717182]">Registered</th>
                <th className="text-left p-4 text-sm font-medium text-[#717182]">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#BAC7CC]/20">
              {participants.map((participant) => (
                <tr key={participant.email} className="hover:bg-[#F0F4F8]">
                  <td className="p-4 font-medium text-[#1D2233]">{participant.name}</td>
                  <td className="p-4 text-[#717182]">{participant.email}</td>
                  <td className="p-4">
                    <div className="flex gap-1">
                      {participant.skills.map((skill) => (
                        <Badge key={skill} variant="outline" className="text-xs">{skill}</Badge>
                      ))}
                    </div>
                  </td>
                  <td className="p-4">{participant.team}</td>
                  <td className="p-4 text-[#717182]">{participant.date}</td>
                  <td className="p-4">
                    <Button variant="ghost" size="sm" className="text-[#56B2BB]">View</Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
