import { useState } from "react";
import { useNavigate } from "react-router";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Label } from "../components/ui/label";
import { Textarea } from "../components/ui/textarea";
import { Users } from "lucide-react";
import { createTeam, TeamApiError } from "../services/team-api";

export function TeamCreatePage() {
  const navigate = useNavigate();
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [maxMembers, setMaxMembers] = useState(5);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (name.trim().length < 3) {
      setError("Team name must be at least 3 characters.");
      return;
    }
    try {
      setLoading(true);
      setError(null);
      const team = await createTeam({
        name: name.trim(),
        description: description.trim() || undefined,
        maxMembers,
      });
      navigate(`/dashboard/teams/${team.id}`);
    } catch (err) {
      if (err instanceof TeamApiError) {
        setError(err.message);
      } else {
        setError("Failed to create team. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-8">
      <div className="max-w-2xl mx-auto">
        <h1 className="text-4xl font-bold text-[#1D2233] mb-2">Create Team</h1>
        <p className="text-lg text-[#717182] mb-8">Form a new team for your project or event</p>

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
                placeholder="What is this team working on? What skills are you looking for?"
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

            {error && (
              <div className="bg-red-50 border border-red-200 rounded-lg p-3">
                <p className="text-red-600 text-sm">{error}</p>
              </div>
            )}

            <div className="flex gap-4 pt-2">
              <Button
                type="submit"
                disabled={loading}
                className="bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white flex-1"
              >
                {loading ? "Creating..." : "Create Team"}
              </Button>
              <Button
                type="button"
                variant="outline"
                className="border-[#BAC7CC]/30"
                onClick={() => navigate("/dashboard/teams")}
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
