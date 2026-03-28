import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { Users } from "lucide-react";

export function CommunityGroupsPage() {
  const groups = [
    { name: "AI Builders", members: 1240, description: "Community for AI/ML enthusiasts and developers", tags: ["AI", "ML", "Python"] },
    { name: "Web3 Casablanca", members: 580, description: "Blockchain developers in Morocco", tags: ["Blockchain", "Web3"] },
    { name: "Open Source Africa", members: 2100, description: "Contributing to open source from Africa", tags: ["Open Source", "Community"] },
  ];

  return (
    <div className="p-8">
      <h1 className="text-4xl font-bold text-[#1D2233] mb-2">Community Groups</h1>
      <p className="text-lg text-[#717182] mb-8">Join thematic groups and connect with like-minded developers</p>

      <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
        {groups.map((group) => (
          <div key={group.name} className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30 shadow-sm">
            <h3 className="font-bold text-[#1D2233] mb-2">{group.name}</h3>
            <div className="flex items-center gap-2 text-sm text-[#717182] mb-3">
              <Users className="w-4 h-4" />
              <span>{group.members.toLocaleString()} members</span>
            </div>
            <p className="text-sm text-[#717182] mb-4">{group.description}</p>
            <div className="flex flex-wrap gap-2 mb-4">
              {group.tags.map((tag) => (
                <Badge key={tag} variant="outline" className="text-xs">{tag}</Badge>
              ))}
            </div>
            <Button className="w-full bg-[#56B2BB] hover:bg-[#56B2BB]/90">Join Group</Button>
          </div>
        ))}
      </div>
    </div>
  );
}
