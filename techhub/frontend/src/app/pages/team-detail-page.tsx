import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { Users, Crown, Edit, Trash2, ArrowLeft, CalendarDays } from "lucide-react";
import { getTeam, deleteTeam, TeamResponse, TeamApiError } from "../services/team-api";

export function TeamDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [team, setTeam] = useState<TeamResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [deleting, setDeleting] = useState(false);
  const [confirmDelete, setConfirmDelete] = useState(false);

  useEffect(() => {
    if (!id) return;
    getTeam(id)
      .then(setTeam)
      .catch((err) => {
        if (err instanceof TeamApiError) setError(err.message);
        else setError("Failed to load team.");
      })
      .finally(() => setLoading(false));
  }, [id]);

  const handleDelete = async () => {
    if (!id) return;
    setDeleting(true);
    try {
      await deleteTeam(id);
      navigate("/dashboard/teams");
    } catch (err) {
      if (err instanceof TeamApiError) setError(err.message);
      else setError("Failed to delete team.");
      setDeleting(false);
      setConfirmDelete(false);
    }
  };

  if (loading) {
    return (
      <div className="p-8 max-w-3xl mx-auto animate-pulse">
        <div className="h-8 bg-[#F0F4F8] rounded w-1/3 mb-4" />
        <div className="bg-white rounded-xl p-8 border border-[#BAC7CC]/30">
          <div className="h-6 bg-[#F0F4F8] rounded w-2/3 mb-3" />
          <div className="h-4 bg-[#F0F4F8] rounded w-full mb-2" />
          <div className="h-4 bg-[#F0F4F8] rounded w-3/4" />
        </div>
      </div>
    );
  }

  if (error || !team) {
    return (
      <div className="p-8 max-w-3xl mx-auto">
        <Button variant="ghost" onClick={() => navigate("/dashboard/teams")} className="mb-6 text-[#717182]">
          <ArrowLeft className="w-4 h-4 mr-2" /> Back to My Teams
        </Button>
        <div className="bg-red-50 border border-red-200 rounded-xl p-6 text-red-600">
          {error ?? "Team not found."}
        </div>
      </div>
    );
  }

  const filledPct = Math.round((team.currentMembers / team.maxMembers) * 100);
  const createdDate = new Date(team.createdAt).toLocaleDateString("en-US", {
    year: "numeric", month: "long", day: "numeric",
  });

  return (
    <div className="p-8 max-w-3xl mx-auto">
      <Button variant="ghost" onClick={() => navigate("/dashboard/teams")} className="mb-6 text-[#717182] -ml-2">
        <ArrowLeft className="w-4 h-4 mr-2" /> Back to My Teams
      </Button>

      <div className="bg-white rounded-xl border border-[#BAC7CC]/30 shadow-sm overflow-hidden">
        {/* Header */}
        <div className="p-8 border-b border-[#BAC7CC]/20">
          <div className="flex items-start justify-between gap-4">
            <div className="flex-1 min-w-0">
              <div className="flex items-center gap-3 mb-2">
                <h1 className="text-3xl font-bold text-[#1D2233] truncate">{team.name}</h1>
                {team.isOwner && (
                  <span className="flex items-center gap-1 text-xs font-medium text-amber-600 bg-amber-50 border border-amber-200 rounded-full px-2 py-0.5 shrink-0">
                    <Crown className="w-3 h-3" /> Owner
                  </span>
                )}
              </div>
              <div className="flex items-center gap-2 text-sm text-[#717182]">
                <CalendarDays className="w-4 h-4" />
                <span>Created {createdDate}</span>
              </div>
            </div>
            <Badge
              className={
                team.status === "OPEN"
                  ? "bg-green-100 text-green-700 border-green-200 shrink-0"
                  : "bg-red-100 text-red-700 border-red-200 shrink-0"
              }
            >
              {team.status === "OPEN" ? "Open" : "Full"}
            </Badge>
          </div>

          {team.isOwner && (
            <div className="flex gap-2 mt-6">
              <Button
                variant="outline"
                onClick={() => navigate(`/dashboard/teams/${team.id}/edit`)}
                className="border-[#BAC7CC]/40"
              >
                <Edit className="w-4 h-4 mr-2" /> Edit Team
              </Button>
              {!confirmDelete ? (
                <Button
                  variant="outline"
                  onClick={() => setConfirmDelete(true)}
                  className="border-red-200 text-red-600 hover:bg-red-50"
                >
                  <Trash2 className="w-4 h-4 mr-2" /> Delete
                </Button>
              ) : (
                <div className="flex items-center gap-2 bg-red-50 border border-red-200 rounded-lg px-3 py-1.5">
                  <span className="text-sm text-red-700 font-medium">Delete this team?</span>
                  <Button
                    size="sm"
                    onClick={handleDelete}
                    disabled={deleting}
                    className="bg-red-600 hover:bg-red-700 text-white h-7 px-3 text-xs"
                  >
                    {deleting ? "Deleting..." : "Yes, delete"}
                  </Button>
                  <Button
                    size="sm"
                    variant="ghost"
                    onClick={() => setConfirmDelete(false)}
                    className="h-7 px-3 text-xs"
                  >
                    Cancel
                  </Button>
                </div>
              )}
            </div>
          )}
        </div>

        {/* Description */}
        {team.description && (
          <div className="px-8 py-6 border-b border-[#BAC7CC]/20">
            <h2 className="text-sm font-semibold text-[#1D2233] uppercase tracking-wide mb-3">About</h2>
            <p className="text-[#717182] leading-relaxed whitespace-pre-wrap">{team.description}</p>
          </div>
        )}

        {/* Members */}
        <div className="px-8 py-6">
          <h2 className="text-sm font-semibold text-[#1D2233] uppercase tracking-wide mb-4">Members</h2>
          <div className="flex items-center justify-between text-sm text-[#717182] mb-2">
            <span className="flex items-center gap-2">
              <Users className="w-4 h-4" />
              {team.currentMembers} of {team.maxMembers} members joined
            </span>
            <span className="font-medium text-[#56B2BB]">
              {team.remainingSlots} slot{team.remainingSlots !== 1 ? "s" : ""} available
            </span>
          </div>
          <div className="w-full bg-[#F0F4F8] rounded-full h-2">
            <div
              className="bg-[#56B2BB] h-2 rounded-full transition-all"
              style={{ width: `${filledPct}%` }}
            />
          </div>
          <p className="text-xs text-[#BAC7CC] mt-2">{filledPct}% full</p>
        </div>
      </div>
    </div>
  );
}
