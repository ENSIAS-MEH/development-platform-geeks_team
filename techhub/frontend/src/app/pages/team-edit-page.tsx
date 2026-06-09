import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Label } from "../components/ui/label";
import { Textarea } from "../components/ui/textarea";
import { Users, ArrowLeft } from "lucide-react";
import { getTeam, updateTeam, TeamApiError } from "../services/team-api";

export function TeamEditPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [maxMembers, setMaxMembers] = useState(5);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [saveError, setSaveError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!id) return;
    getTeam(id)
      .then((team) => {
        if (!team.isOwner) {
          navigate(`/dashboard/teams/${id}`, { replace: true });
          return;
        }
        setName(team.name);
        setDescription(team.description ?? "");
        setMaxMembers(team.maxMembers);
      })
      .catch((err) => {
        if (err instanceof TeamApiError) setLoadError(err.message);
        else setLoadError("Failed to load team.");
      })
      .finally(() => setLoading(false));
  }, [id, navigate]);

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (name.trim().length < 3) {
      setSaveError("Team name must be at least 3 characters.");
      return;
    }
    if (!id) return;
    setSaving(true);
    setSaveError(null);
    try {
      await updateTeam(id, {
        name: name.trim(),
        description: description.trim() || undefined,
        maxMembers,
      });
      navigate(`/dashboard/teams/${id}`);
    } catch (err) {
      if (err instanceof TeamApiError) setSaveError(err.message);
      else setSaveError("Failed to save changes. Please try again.");
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="p-8 max-w-2xl mx-auto animate-pulse">
        <div className="h-8 bg-[#F0F4F8] rounded w-1/3 mb-8" />
        <div className="bg-white rounded-xl p-8 border border-[#BAC7CC]/30 space-y-6">
          <div className="h-10 bg-[#F0F4F8] rounded" />
          <div className="h-28 bg-[#F0F4F8] rounded" />
          <div className="h-10 bg-[#F0F4F8] rounded w-1/3" />
        </div>
      </div>
    );
  }

  if (loadError) {
    return (
      <div className="p-8 max-w-2xl mx-auto">
        <Button variant="ghost" onClick={() => navigate(-1)} className="mb-6 text-[#717182]">
          <ArrowLeft className="w-4 h-4 mr-2" /> Back
        </Button>
        <div className="bg-red-50 border border-red-200 rounded-xl p-6 text-red-600">{loadError}</div>
      </div>
    );
  }

  return (
    <div className="p-8">
      <div className="max-w-2xl mx-auto">
        <Button variant="ghost" onClick={() => navigate(`/dashboard/teams/${id}`)} className="mb-6 text-[#717182] -ml-2">
          <ArrowLeft className="w-4 h-4 mr-2" /> Back to Team
        </Button>

        <h1 className="text-4xl font-bold text-[#1D2233] mb-2">Edit Team</h1>
        <p className="text-lg text-[#717182] mb-8">Update your team details</p>

        <div className="bg-white rounded-xl p-8 border border-[#BAC7CC]/30 shadow-sm">
          <form className="space-y-6" onSubmit={onSubmit}>

            <div>
              <Label htmlFor="name">Team Name *</Label>
              <Input
                id="name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="e.g., AI Hackathon Squad"
                className="mt-1.5 bg-[#F0F4F8] border-[#BAC7CC]/30"
                minLength={3}
                maxLength={120}
                required
              />
              <p className="text-xs text-[#717182] mt-1">{name.length}/120</p>
            </div>

            <div>
              <Label htmlFor="description">Description</Label>
              <Textarea
                id="description"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="What is this team working on?"
                rows={5}
                className="mt-1.5 bg-[#F0F4F8] border-[#BAC7CC]/30"
                maxLength={1000}
              />
              <p className="text-xs text-[#717182] mt-1">{description.length}/1000</p>
            </div>

            <div>
              <Label htmlFor="maxMembers">Maximum Members *</Label>
              <div className="mt-1.5 flex items-center gap-3">
                <Users className="w-5 h-5 text-[#56B2BB]" />
                <Input
                  id="maxMembers"
                  type="number"
                  value={maxMembers}
                  onChange={(e) => setMaxMembers(Number(e.target.value))}
                  min={2}
                  max={100}
                  className="w-32 bg-[#F0F4F8] border-[#BAC7CC]/30"
                  required
                />
                <span className="text-sm text-[#717182]">members (2–100)</span>
              </div>
            </div>

            {saveError && (
              <div className="bg-red-50 border border-red-200 rounded-lg p-3">
                <p className="text-red-600 text-sm">{saveError}</p>
              </div>
            )}

            <div className="flex gap-4 pt-2">
              <Button
                type="submit"
                disabled={saving}
                className="bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white flex-1"
              >
                {saving ? "Saving..." : "Save Changes"}
              </Button>
              <Button
                type="button"
                variant="outline"
                className="border-[#BAC7CC]/30"
                onClick={() => navigate(`/dashboard/teams/${id}`)}
              >
                Cancel
              </Button>
            </div>

          </form>
        </div>
      </div>
    </div>
  );
}
