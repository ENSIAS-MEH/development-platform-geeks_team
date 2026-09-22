import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router";
import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { MapPin, Calendar, Github, Linkedin, ExternalLink,
         Mail, Loader2, ArrowLeft } from "lucide-react";
import { userService } from "../services/userService";

type UserProfileResponse = {
  id: string;
  name: string;
  role: string;
  bio?: string;
  location?: string;
  avatarUrl?: string;
  email?: string | null;
  skills: string[];
  joinedAt?: string;
  headline?: string;
  githubUrl?: string;
  linkedinUrl?: string;
  portfolioUrl?: string;
};

export function PublicProfilePage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [user, setUser] = useState<UserProfileResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetch = async () => {
      try {
        setLoading(true);
        const data = await userService.getUserById(id!);
        setUser(data);
      } catch (e: any) {
        setError(
          e.response?.status === 404
            ? "This user doesn't exist"
            : "Failed to load profile"
        );
      } finally {
        setLoading(false);
      }
    };
    if (id) fetch();
  }, [id]);

  if (loading) return (
    <div className="flex items-center justify-center h-64">
      <Loader2 className="w-8 h-8 animate-spin text-[#56B2BB]" />
    </div>
  );

  if (error) return (
    <div className="flex flex-col items-center justify-center h-64 gap-4">
      <p className="text-red-500">{error}</p>
      <Button variant="outline" onClick={() => navigate(-1)}>
        Go Back
      </Button>
    </div>
  );

  if (!user) return null;

  const initials = user.name
    ? user.name.split(" ").map((n) => n[0]).join("").toUpperCase().slice(0, 2)
    : "?";

  return (
    <div className="p-8 max-w-4xl mx-auto">

      {/* Back button */}
      <button
        onClick={() => navigate(-1)}
        className="flex items-center gap-2 text-[#717182] hover:text-[#1D2233] mb-6 transition-colors"
      >
        <ArrowLeft className="w-4 h-4" />
        Back
      </button>

      {/* Profile card */}
      <div className="bg-white rounded-xl p-8 border border-[#BAC7CC]/30 shadow-lg mb-6">
        <div className="flex flex-col md:flex-row gap-6 items-start">

          {/* Avatar */}
          <div className="w-24 h-24 bg-[#56B2BB] rounded-xl flex items-center justify-center text-white text-3xl font-bold flex-shrink-0 overflow-hidden">
            {user.avatarUrl
              ? <img src={user.avatarUrl} alt={user.name}
                     className="w-full h-full object-cover" />
              : initials
            }
          </div>

          <div className="flex-1">
            {/* Name + role */}
            <h1 className="text-2xl font-bold text-[#1D2233] mb-1">
              {user.name}
            </h1>
            <p className="text-[#56B2BB] font-medium mb-1">{user.role}</p>

            {user.headline && (
              <p className="text-[#717182] mb-3">{user.headline}</p>
            )}

            {/* Location + join date */}
            <div className="flex flex-wrap items-center gap-4 text-sm text-[#717182] mb-4">
              {user.location && (
                <span className="flex items-center gap-1">
                  <MapPin className="w-4 h-4" />
                  {user.location}
                </span>
              )}
              {user.joinedAt && (
                <span className="flex items-center gap-1">
                  <Calendar className="w-4 h-4" />
                  Joined {new Date(user.joinedAt).toLocaleDateString("en-US", {
                    month: "long", year: "numeric"
                  })}
                </span>
              )}
            </div>

            {/* Bio */}
            {user.bio && (
              <p className="text-[#1D2233] mb-4">{user.bio}</p>
            )}

            {/* Email — only shown if user enabled showEmail */}
            {user.email && (
              <p className="flex items-center gap-2 text-sm text-[#717182] mb-4">
                <Mail className="w-4 h-4" />
                {user.email}
              </p>
            )}

            {/* Social links */}
            <div className="flex flex-wrap gap-4 mb-4">
              {user.githubUrl && (
                <a href={user.githubUrl} target="_blank" rel="noreferrer"
                   className="flex items-center gap-2 text-sm text-[#56B2BB] hover:underline">
                  <Github className="w-4 h-4" />
                  GitHub
                </a>
              )}
              {user.linkedinUrl && (
                <a href={user.linkedinUrl} target="_blank" rel="noreferrer"
                   className="flex items-center gap-2 text-sm text-[#56B2BB] hover:underline">
                  <Linkedin className="w-4 h-4" />
                  LinkedIn
                </a>
              )}
              {user.portfolioUrl && (
                <a href={user.portfolioUrl} target="_blank" rel="noreferrer"
                   className="flex items-center gap-2 text-sm text-[#56B2BB] hover:underline">
                  <ExternalLink className="w-4 h-4" />
                  Portfolio
                </a>
              )}
            </div>

            {/* Skills */}
            {user.skills && user.skills.length > 0 && (
              <div>
                <p className="text-sm text-[#717182] mb-2">Skills</p>
                <div className="flex flex-wrap gap-2">
                  {user.skills.map((skill) => (
                    <Badge
                      key={skill}
                      className="bg-[#56B2BB]/10 text-[#56B2BB] hover:bg-[#56B2BB]/20"
                    >
                      {skill}
                    </Badge>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>

    </div>
  );
}