import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Search, MapPin, Loader2 } from "lucide-react";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { userService } from "../services/userService";

// Type matches your actual UserResponse DTO from backend
type UserResponse = {
  id: string;
  name: string;
  role: string;        // "Developer", "Student", "Organizer", "Company"
  bio: string;
  location: string;
  skills: string[];
  avatarUrl?: string;
  email?: string | null;
  joinedAt?: string;
};

export function FindCollaboratorsPage() {
  const navigate = useNavigate();
  const [profiles, setProfiles] = useState<UserResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchSkill, setSearchSkill] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [error, setError] = useState<string | null>(null);

  // ── Load all users on mount ────────────────────────────────────────────
  useEffect(() => {
    fetchUsers();
  }, [page]);  // re-fetch when page changes

  // ── Fetch without skill filter — shows everyone ────────────────────────
  const fetchUsers = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await userService.getAllUsers(page, 9);  // 9 for 3x3 grid
      setProfiles(data.content || []);
      setTotalPages(data.totalPages);
    } catch (e: any) {
      setError("Failed to load collaborators");
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  // ── Search by skill name ───────────────────────────────────────────────
  const handleSearch = async () => {
    if (!searchSkill.trim()) {
      // Empty search — go back to showing everyone
      fetchUsers();
      return;
    }
    try {
      setLoading(true);
      setError(null);
      const data = await userService.searchUsers(searchSkill.trim(), undefined, 0, 9);
      setProfiles(data.content || []);
      setTotalPages(data.totalPages);
      setPage(0);
    } catch (e: any) {
      setError("Search failed");
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  // ── Search on Enter key ────────────────────────────────────────────────
  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") handleSearch();
  };

  return (
    <div className="p-8">
      <h1 className="text-4xl font-bold text-[#1D2233] mb-2">
        Find Collaborators
      </h1>
      <p className="text-lg text-[#717182] mb-8">
        Connect with developers who have the skills you need
      </p>

      {/* ── Search bar ── */}
      <div className="bg-white rounded-xl p-6 mb-6 border border-[#BAC7CC]/30">
        <div className="flex gap-3">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-[#717182]" />
            <Input
              placeholder="Search by skill e.g. React, Python, Docker..."
              className="pl-10 bg-[#F0F4F8]"
              value={searchSkill}
              onChange={(e) => setSearchSkill(e.target.value)}
              onKeyDown={handleKeyDown}
            />
          </div>
          <Button
            onClick={handleSearch}
            className="bg-[#56B2BB] hover:bg-[#56B2BB]/90"
          >
            Search
          </Button>
          {searchSkill && (
            <Button
              variant="outline"
              onClick={() => {
                setSearchSkill("");
                fetchUsers();
              }}
            >
              Clear
            </Button>
          )}
        </div>
      </div>

      {/* ── Loading state ── */}
      {loading && (
        <div className="flex items-center justify-center h-48">
          <Loader2 className="w-8 h-8 animate-spin text-[#56B2BB]" />
        </div>
      )}

      {/* ── Error state ── */}
      {error && !loading && (
        <div className="text-center py-12 text-red-500">
          <p>{error}</p>
        </div>
      )}

      {/* ── Empty state ── */}
      {!loading && !error && profiles.length === 0 && (
        <div className="text-center py-12">
          <p className="text-[#717182]">
            {searchSkill
              ? `No collaborators found with skill "${searchSkill}"`
              : "No collaborators found"
            }
          </p>
        </div>
      )}

      {/* ── Profiles grid ── */}
      {!loading && profiles.length > 0 && (
        <>
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {profiles.map((profile) => (
              <div
                key={profile.id}   // id not name — names can be duplicates
                className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30 shadow-sm hover:shadow-md transition-shadow"
              >
                {/* Avatar */}
                <div className="w-16 h-16 bg-[#56B2BB]/10 rounded-full flex items-center justify-center mb-4 mx-auto overflow-hidden">
                  {profile.avatarUrl
                    ? <img
                        src={profile.avatarUrl}
                        alt={profile.name}
                        className="w-full h-full object-cover"
                      />
                    : <span className="text-2xl font-bold text-[#56B2BB]">
                        {profile.name?.charAt(0)?.toUpperCase() || "?"}
                      </span>
                  }
                </div>

                {/* Name */}
                <h3 className="font-bold text-[#1D2233] text-center mb-1">
                  {profile.name}
                </h3>

                {/* Role — was showing name twice before */}
                <p className="text-sm text-[#56B2BB] text-center mb-2">
                  {profile.role}
                </p>

                {/* Location */}
                {profile.location && (
                  <p className="text-xs text-[#717182] text-center mb-3 flex items-center justify-center gap-1">
                    <MapPin className="w-3 h-3" />
                    {profile.location}
                  </p>
                )}

                {/* Bio — truncated */}
                {profile.bio && (
                  <p className="text-xs text-[#717182] text-center mb-3 line-clamp-2">
                    {profile.bio}
                  </p>
                )}

                {/* Skills */}
                {profile.skills && profile.skills.length > 0 && (
                  <div className="flex flex-wrap gap-1 justify-center mb-4">
                    {profile.skills.slice(0, 4).map((skill) => (  // max 4 to avoid overflow
                      <Badge
                        key={skill}
                        variant="outline"
                        className="text-xs"
                      >
                        {skill}
                      </Badge>
                    ))}
                    {profile.skills.length > 4 && (
                      <Badge variant="outline" className="text-xs text-[#717182]">
                        +{profile.skills.length - 4} more
                      </Badge>
                    )}
                  </div>
                )}

                {/* View Profile button — navigates to public profile page */}
                <Button
                  className="w-full bg-[#56B2BB] hover:bg-[#56B2BB]/90"
                  onClick={() => navigate(`/users/${profile.id}`)}
                >
                  View Profile
                </Button>
              </div>
            ))}
          </div>

          {/* ── Pagination ── */}
          {totalPages > 1 && (
            <div className="flex items-center justify-center gap-3 mt-8">
              <Button
                variant="outline"
                disabled={page === 0}
                onClick={() => setPage(p => p - 1)}
              >
                Previous
              </Button>
              <span className="text-sm text-[#717182]">
                Page {page + 1} of {totalPages}
              </span>
              <Button
                variant="outline"
                disabled={page >= totalPages - 1}
                onClick={() => setPage(p => p + 1)}
              >
                Next
              </Button>
            </div>
          )}
        </>
      )}
    </div>
  );
}