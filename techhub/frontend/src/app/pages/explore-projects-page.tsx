import { useEffect, useState } from "react";
import { Navbar } from "../components/navbar";
import { ProjectCard } from "../components/project-card";
import { Input } from "../components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "../components/ui/select";
import { Badge } from "../components/ui/badge";
import { Search, Filter, Loader2 } from "lucide-react";
import {
  searchProjects,
  projectToCardProps,
  uiStatusToProjectStatusApi,
  uiTypeToProjectTypeApi,
  ProjectApiError,
} from "../services/project-api";

export function ExploreProjectsPage() {
  const [searchQuery, setSearchQuery] = useState("");
  const [statusFilter, setStatusFilter] = useState("all");
  const [typeFilter, setTypeFilter] = useState("all");
  const [projects, setProjects] = useState<ReturnType<typeof projectToCardProps>[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const load = async () => {
      try {
        setLoading(true);
        setError(null);
        const page = await searchProjects({
          keyword: searchQuery.trim() || undefined,
          status: statusFilter === "all" ? undefined : uiStatusToProjectStatusApi(statusFilter),
          type: typeFilter === "all" ? undefined : uiTypeToProjectTypeApi(typeFilter),
          size: 30,
        });
        setProjects(page.content.map(projectToCardProps));
      } catch (err) {
        const message =
          err instanceof ProjectApiError
            ? err.message
            : "Could not load projects. Is project-service running on port 8083?";
        setError(message);
      } finally {
        setLoading(false);
      }
    };

    const timer = setTimeout(load, 300);
    return () => clearTimeout(timer);
  }, [searchQuery, statusFilter, typeFilter]);

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
            
            <Select value={statusFilter} onValueChange={setStatusFilter}>
              <SelectTrigger className="bg-[#F0F4F8] border-[#BAC7CC]/30">
                <SelectValue placeholder="Status" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Projects</SelectItem>
                <SelectItem value="Open to contributors">Open to Contributors</SelectItem>
                <SelectItem value="In progress">In Progress</SelectItem>
                <SelectItem value="Completed">Completed</SelectItem>
              </SelectContent>
            </Select>
            
            <Select value={typeFilter} onValueChange={setTypeFilter}>
              <SelectTrigger className="bg-[#F0F4F8] border-[#BAC7CC]/30">
                <SelectValue placeholder="Project Type" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Types</SelectItem>
                <SelectItem value="Startup Idea">Startup Idea</SelectItem>
                <SelectItem value="Open Source">Open Source</SelectItem>
                <SelectItem value="Student Project">Student Project</SelectItem>
                <SelectItem value="Hackathon Project">Hackathon Project</SelectItem>
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

        {loading && (
          <div className="flex justify-center py-16">
            <Loader2 className="w-8 h-8 animate-spin text-[#56B2BB]" />
          </div>
        )}

        {error && !loading && (
          <p className="text-center text-red-500 py-8">{error}</p>
        )}

        {!loading && !error && (
          <>
            <div className="mb-4">
              <p className="text-[#717182]">Showing {projects.length} projects</p>
            </div>
            {projects.length === 0 ? (
              <p className="text-center text-[#717182] py-12">No projects found.</p>
            ) : (
              <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
                {projects.map((project) => (
                  <ProjectCard key={project.id} {...project} />
                ))}
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}
