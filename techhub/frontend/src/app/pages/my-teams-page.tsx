import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { Users, Plus, ArrowRight, Crown } from "lucide-react";
import { getMyTeams, TeamResponse, TeamApiError } from "../services/team-api";

export function MyTeamsPage() {
  const navigate = useNavigate();
  const [teams, setTeams] = useState<TeamResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    getMyTeams()
      .then((page) => setTeams(page.content))
      .catch((err) => {
        if (err instanceof TeamApiError) setError(err.message);
        else setError("Failed to load teams.");
      })
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="p-8">
      <div className="flex items-start justify-between mb-8">
        <div>
          <h1 className="text-4xl font-bold text-[#1D2233] mb-2">My Teams</h1>
          <p className="text-lg text-[#717182]">Teams you own or belong to</p>
        </div>
        <Button
          onClick={() => navigate("/dashboard/teams/create")}
          className="bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white"
        >
          <Plus className="w-4 h-4 mr-2" />
          Create Team
        </Button>
      </div>

      {loading && (
        <div className="grid md:grid-cols-2 gap-6">
          {[1, 2, 3].map((n) => (
            <div key={n} className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30 animate-pulse">
              <div className="h-5 bg-[#F0F4F8] rounded w-2/3 mb-3" />
              <div className="h-4 bg-[#F0F4F8] rounded w-full mb-2" />
              <div className="h-4 bg-[#F0F4F8] rounded w-1/2 mb-6" />
              <div className="h-9 bg-[#F0F4F8] rounded" />
            </div>
          ))}
        </div>
      )}

      {!loading && error && (
        <div className="bg-red-50 border border-red-200 rounded-xl p-6 text-red-600">
          {error}
        </div>
      )}

      {!loading && !error && teams.length === 0 && (
        <div className="bg-white rounded-xl p-12 border border-[#BAC7CC]/30 shadow-sm text-center">
          <div className="w-16 h-16 bg-[#56B2BB]/10 rounded-full flex items-center justify-center mx-auto mb-4">
            <Users className="w-8 h-8 text-[#56B2BB]" />
          </div>
          <h3 className="text-xl font-bold text-[#1D2233] mb-2">No teams yet</h3>
          <p className="text-[#717182] mb-6">Create your first team or wait to be invited to one.</p>
          <Button
            onClick={() => navigate("/dashboard/teams/create")}
            className="bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white"
          >
            <Plus className="w-4 h-4 mr-2" />
            Create a Team
          </Button>
        </div>
      )}

      {!loading && !error && teams.length > 0 && (
        <div className="grid md:grid-cols-2 gap-6">
          {teams.map((team) => (
            <TeamCard key={team.id} team={team} onView={() => navigate(`/dashboard/teams/${team.id}`)} />
          ))}
        </div>
      )}
    </div>
  );
}

function TeamCard({ team, onView }: { team: TeamResponse; onView: () => void }) {
  const filledPct = Math.round((team.currentMembers / team.maxMembers) * 100);

  return (
    <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30 shadow-sm flex flex-col gap-4">
      <div className="flex items-start justify-between">
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 mb-1">
            <h3 className="font-bold text-[#1D2233] truncate">{team.name}</h3>
            {team.isOwner && (
              <Crown className="w-4 h-4 text-amber-500 shrink-0" title="You own this team" />
            )}
          </div>
          {team.description && (
            <p className="text-sm text-[#717182] line-clamp-2">{team.description}</p>
          )}
        </div>
        <Badge
          className={
            team.status === "OPEN"
              ? "bg-green-100 text-green-700 border-green-200 shrink-0 ml-3"
              : "bg-red-100 text-red-700 border-red-200 shrink-0 ml-3"
          }
        >
          {team.status === "OPEN" ? "Open" : "Full"}
        </Badge>
      </div>

      <div>
        <div className="flex items-center justify-between text-sm text-[#717182] mb-1.5">
          <span className="flex items-center gap-1.5">
            <Users className="w-4 h-4" />
            {team.currentMembers} / {team.maxMembers} members
          </span>
          <span>{team.remainingSlots} slot{team.remainingSlots !== 1 ? "s" : ""} left</span>
        </div>
        <div className="w-full bg-[#F0F4F8] rounded-full h-1.5">
          <div
            className="bg-[#56B2BB] h-1.5 rounded-full transition-all"
            style={{ width: `${filledPct}%` }}
          />
        </div>
      </div>

      <Button
        onClick={onView}
        className="w-full bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white mt-auto"
      >
        View Team
        <ArrowRight className="w-4 h-4 ml-2" />
      </Button>
    </div>
  );
}
