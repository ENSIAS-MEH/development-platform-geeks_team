import { Button } from "../components/ui/button";
import { Badge } from "../components/ui/badge";
import { Users, Shuffle } from "lucide-react";

export function TeamFormationPage() {
  return (
    <div className="p-8">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-4xl font-bold text-[#1D2233] mb-2">Team Formation</h1>
          <p className="text-lg text-[#717182]">Assign participants to teams</p>
        </div>
        <Button className="bg-[#56B2BB] hover:bg-[#56B2BB]/90">
          <Shuffle className="w-4 h-4 mr-2" />
          Auto-Balance Teams
        </Button>
      </div>

      <div className="grid md:grid-cols-3 gap-6">
        {["Team A", "Team B", "Team C"].map((teamName) => (
          <div key={teamName} className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-bold text-[#1D2233]">{teamName}</h3>
              <div className="flex items-center gap-2 text-sm text-[#717182]">
                <Users className="w-4 h-4" />
                <span>4 members</span>
              </div>
            </div>
            <div className="space-y-3">
              <div className="p-3 bg-[#F0F4F8] rounded-lg">
                <p className="font-medium text-[#1D2233] text-sm">Sarah Chen</p>
                <div className="flex gap-1 mt-1">
                  <Badge variant="outline" className="text-xs">Python</Badge>
                  <Badge variant="outline" className="text-xs">ML</Badge>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
