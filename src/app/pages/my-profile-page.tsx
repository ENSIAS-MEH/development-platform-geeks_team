import { Button } from "../components/ui/button";
import { Badge } from "../components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "../components/ui/tabs";
import { ProjectCard } from "../components/project-card";
import { Mail, Github, Linkedin, ExternalLink, MapPin, Calendar, Edit, Loader2 } from "lucide-react";
import { useState } from "react";
import { useEffect } from "react";
import { userService } from "../services/userService";
import { Link, useNavigate } from "react-router";


export  function MyProfilePage() {
  const [userInfo, setUserInfo] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        setLoading(true);
        const data = await userService.getMyProfile();
        setUserInfo(data);
      } catch (err: any) {
        setError(err.response?.data?.message || "Failed to load profile");
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, []); // empty array = run once on mount

    // ── Loading state ──────────────────────────────────────────────────────
  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <Loader2 className="w-8 h-8 animate-spin text-[#56B2BB]" />
      </div>
    );
  }

  // ── Error state ────────────────────────────────────────────────────────
  if (error) {
    return (
      <div className="p-8 text-center text-red-500">
        <p>{error}</p>
      </div>
    );
  }

  // ── Guard — should never happen but prevents crash if user is null ─────
  if (!userInfo) return null;

  // ── Get initials for avatar ────────────────────────────────────────────
  const initials = userInfo.name
    ? userInfo.name.split(" ").map((n: string) => n[0]).join("").toUpperCase().slice(0, 2)
    : "?";

  const skills =userInfo.skills;
  
  const projects = [
    {
      id: "1",
      name: "AI Study Buddy",
      description: "An AI-powered learning assistant for students",
      techStack: ["Python", "TensorFlow", "React", "FastAPI"],
      status: "Open to contributors",
      teamSize: 5,
    },
    {
      id: "2",
      name: "EcoTracker",
      description: "Track personal carbon footprint with eco-friendly alternatives",
      techStack: ["React Native", "Node.js", "MongoDB"],
      status: "In progress",
      teamSize: 3,
    },
  ];

  const events = [
    { name: "AI Innovation Hackathon 2026", date: "March 25-27, 2026", role: "Participant" },
    { name: "Web3 Workshop Series", date: "April 5, 2026", role: "Attendee" },
    { name: "Mobile Dev Conference", date: "April 15-16, 2026", role: "Speaker" },
  ];

  return (
    <div className="p-8">
      {/* Header */}
      <div className="bg-gradient-to-r from-[#1D2233] to-[#56B2BB] h-48 rounded-xl mb-8"></div>

      <div className="max-w-5xl mx-auto -mt-32 relative">
        {/* Profile Card */}
        <div className="bg-white rounded-xl p-8 border border-[#BAC7CC]/30 shadow-lg mb-8">
          <div className="flex flex-col md:flex-row gap-6 items-start">
            <div className="w-32 h-32 bg-[#56B2BB] rounded-xl flex items-center justify-center text-white text-5xl font-bold flex-shrink-0">
              {userInfo.avatarUrl
                ? <img src={userInfo.avatarUrl} alt={userInfo.name} className="w-full h-full rounded-xl object-cover" />
                : initials
              }
            </div>
            
            <div className="flex-1">
              <div className="flex items-start justify-between mb-4">
                <div>
                  <h1 className="text-3xl font-bold text-[#1D2233] mb-2">{userInfo.name}</h1>
                  <p className="text-[#717182] mb-3">{userInfo.headline || "No headline"}</p>
                  <div className="flex items-center gap-4 text-sm text-[#717182]">
                    <span className="flex items-center gap-1">
                      <MapPin className="w-4 h-4" />
                      {userInfo.location}
                    </span>
                    {userInfo.joinedAt && (
                      <span className="flex items-center gap-1">
                        <Calendar className="w-4 h-4" />
                        Joined {new Date(userInfo.joinedAt).toLocaleDateString("en-US", {
                          day: "numeric",
                          month: "long",
                          year: "numeric"
                        })}
                      </span> )}
                  </div>
                </div>
                <Link to="/dashboard/profile/edit">
                    <Button  className="bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white"> 
                  <Edit className="w-4 h-4 mr-2" />
                  Edit Profile
                </Button>
                </Link>
                
              </div>

              <p className="text-[#1D2233] mb-4">
                {userInfo.bio}
              </p>

              <div className="flex flex-wrap gap-3 mb-4">
                <a href="#" className="flex items-center gap-2 text-[#56B2BB] hover:underline">
                  <Github className="w-4 h-4" />
                  {userInfo.githubUrl}
                </a>
                <a href="#" className="flex items-center gap-2 text-[#56B2BB] hover:underline">
                  <Linkedin className="w-4 h-4" />
                  {userInfo.linkedinUrl}
                </a>
                <a href="#" className="flex items-center gap-2 text-[#56B2BB] hover:underline">
                  <ExternalLink className="w-4 h-4" />
                  {userInfo.portfolioUrl}
                </a>
              </div>

              <div>
                <p className="text-sm text-[#717182] mb-2">Skills</p>
                <div className="flex flex-wrap gap-2">
                  {skills.map((skill: string) => (
                    <Badge key={skill} className="bg-[#56B2BB]/10 text-[#56B2BB] hover:bg-[#56B2BB]/20">
                      {skill}
                    </Badge>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Tabs */}
        <Tabs defaultValue="projects" className="w-full">
          <TabsList className="mb-6">
            <TabsTrigger value="projects">Projects ({projects.length})</TabsTrigger>
            <TabsTrigger value="events">Events ({events.length})</TabsTrigger>
            <TabsTrigger value="activity">Activity</TabsTrigger>
          </TabsList>

          <TabsContent value="projects">
            <div className="grid md:grid-cols-2 gap-6">
              {projects.map((project) => (
                <ProjectCard key={project.id} {...project} />
              ))}
            </div>
          </TabsContent>

          <TabsContent value="events">
            <div className="bg-white rounded-xl border border-[#BAC7CC]/30 overflow-hidden">
              <div className="divide-y divide-[#BAC7CC]/20">
                {events.map((event, index) => (
                  <div key={index} className="p-6 hover:bg-[#F0F4F8] transition-colors">
                    <div className="flex items-start justify-between">
                      <div>
                        <h3 className="font-bold text-[#1D2233] mb-1">{event.name}</h3>
                        <p className="text-sm text-[#717182] mb-2">{event.date}</p>
                        <Badge className="bg-[#56B2BB]/10 text-[#56B2BB] hover:bg-[#56B2BB]/20">
                          {event.role}
                        </Badge>
                      </div>
                      <Button variant="ghost" size="sm" className="text-[#56B2BB]">
                        View Details
                      </Button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </TabsContent>

          <TabsContent value="activity">
            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <div className="space-y-4">
                <div className="flex gap-4">
                  <div className="w-2 h-2 bg-[#56B2BB] rounded-full mt-2"></div>
                  <div className="flex-1">
                    <p className="text-sm text-[#1D2233]">Joined <span className="font-medium">AI Study Buddy</span> project</p>
                    <p className="text-xs text-[#717182]">2 days ago</p>
                  </div>
                </div>
                <div className="flex gap-4">
                  <div className="w-2 h-2 bg-[#56B2BB] rounded-full mt-2"></div>
                  <div className="flex-1">
                    <p className="text-sm text-[#1D2233]">Registered for <span className="font-medium">Web3 Workshop Series</span></p>
                    <p className="text-xs text-[#717182]">5 days ago</p>
                  </div>
                </div>
                <div className="flex gap-4">
                  <div className="w-2 h-2 bg-[#56B2BB] rounded-full mt-2"></div>
                  <div className="flex-1">
                    <p className="text-sm text-[#1D2233]">Updated profile skills</p>
                    <p className="text-xs text-[#717182]">1 week ago</p>
                  </div>
                </div>
              </div>
            </div>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
}
