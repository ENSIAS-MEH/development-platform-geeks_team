import { useState } from "react";
import { Navbar } from "../components/navbar";
import { ProjectCard } from "../components/project-card";
import { Input } from "../components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "../components/ui/select";
import { Badge } from "../components/ui/badge";
import { Search, Filter } from "lucide-react";

export function ExploreProjectsPage() {
  const [searchQuery, setSearchQuery] = useState("");
  
  const projects = [
    {
      id: "1",
      name: "AI Study Buddy",
      description: "An AI-powered learning assistant that helps students prepare for exams with personalized quizzes and study plans.",
      techStack: ["Python", "TensorFlow", "React", "FastAPI"],
      status: "Open to contributors",
      teamSize: 5,
      skillsNeeded: ["Machine Learning", "Frontend Development", "UX Design"]
    },
    {
      id: "2",
      name: "EcoTracker",
      description: "Mobile app to track personal carbon footprint and suggest eco-friendly alternatives for daily activities.",
      techStack: ["React Native", "Node.js", "MongoDB", "Firebase"],
      status: "In progress",
      teamSize: 3,
      skillsNeeded: ["Mobile Development", "Backend API"]
    },
    {
      id: "3",
      name: "OpenSource Dev Tools",
      description: "Collection of developer productivity tools including code snippets manager, API testing, and documentation generator.",
      techStack: ["TypeScript", "Electron", "React", "PostgreSQL"],
      status: "Open to contributors",
      teamSize: 8,
      skillsNeeded: ["Desktop Development", "Database Design"]
    },
    {
      id: "4",
      name: "Community Health Platform",
      description: "Healthcare management system for rural communities with telemedicine capabilities and health records.",
      techStack: ["Vue.js", "Django", "WebRTC", "MySQL"],
      status: "Open to contributors",
      teamSize: 6,
      skillsNeeded: ["Backend Development", "WebRTC", "Security"]
    },
    {
      id: "5",
      name: "Decentralized Chat",
      description: "Privacy-focused messaging app built on blockchain with end-to-end encryption and no central servers.",
      techStack: ["Solidity", "Web3.js", "React", "IPFS"],
      status: "In progress",
      teamSize: 4,
      skillsNeeded: ["Blockchain", "Smart Contracts"]
    },
    {
      id: "6",
      name: "EdTech Learning Platform",
      description: "Interactive learning platform for K-12 students with gamification, progress tracking, and teacher dashboards.",
      techStack: ["Next.js", "Supabase", "Tailwind", "Vercel"],
      status: "Open to contributors",
      teamSize: 7,
      skillsNeeded: ["Full Stack", "UI/UX", "Content Creation"]
    },
  ];

  return (
    <div className="min-h-screen bg-[#F0F4F8]">
      <Navbar />
      
      <div className="max-w-7xl mx-auto px-6 py-12">
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-[#1D2233] mb-2">Explore Projects</h1>
          <p className="text-lg text-[#717182]">
            Find open-source projects to contribute to or get inspired for your next idea
          </p>
        </div>

        {/* Filters */}
        <div className="bg-white rounded-xl p-6 mb-8 border border-[#BAC7CC]/30 shadow-sm">
          <div className="grid md:grid-cols-4 gap-4">
            <div className="md:col-span-2">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-[#717182]" />
                <Input
                  type="text"
                  placeholder="Search projects..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10 bg-[#F0F4F8] border-[#BAC7CC]/30"
                />
              </div>
            </div>
            
            <Select defaultValue="all">
              <SelectTrigger className="bg-[#F0F4F8] border-[#BAC7CC]/30">
                <SelectValue placeholder="Status" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Projects</SelectItem>
                <SelectItem value="open">Open to Contributors</SelectItem>
                <SelectItem value="in-progress">In Progress</SelectItem>
              </SelectContent>
            </Select>
            
            <Select defaultValue="all">
              <SelectTrigger className="bg-[#F0F4F8] border-[#BAC7CC]/30">
                <SelectValue placeholder="Tech Stack" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Technologies</SelectItem>
                <SelectItem value="react">React</SelectItem>
                <SelectItem value="python">Python</SelectItem>
                <SelectItem value="node">Node.js</SelectItem>
                <SelectItem value="blockchain">Blockchain</SelectItem>
              </SelectContent>
            </Select>
          </div>
          
          <div className="mt-4 flex flex-wrap gap-2">
            <span className="text-sm text-[#717182] flex items-center gap-2">
              <Filter className="w-4 h-4" />
              Skills needed:
            </span>
            <Badge variant="outline" className="cursor-pointer hover:bg-[#56B2BB]/10">Frontend</Badge>
            <Badge variant="outline" className="cursor-pointer hover:bg-[#56B2BB]/10">Backend</Badge>
            <Badge variant="outline" className="cursor-pointer hover:bg-[#56B2BB]/10">Machine Learning</Badge>
            <Badge variant="outline" className="cursor-pointer hover:bg-[#56B2BB]/10">UI/UX Design</Badge>
            <Badge variant="outline" className="cursor-pointer hover:bg-[#56B2BB]/10">DevOps</Badge>
          </div>
        </div>

        {/* Results */}
        <div className="mb-4">
          <p className="text-[#717182]">Showing {projects.length} projects</p>
        </div>

        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {projects.map((project) => (
            <ProjectCard key={project.id} {...project} />
          ))}
        </div>
      </div>
    </div>
  );
}
