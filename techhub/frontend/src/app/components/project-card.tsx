import { Users, ExternalLink } from "lucide-react";
import { Badge } from "./ui/badge";
import { Button } from "./ui/button";
import { Link } from "react-router";

interface ProjectCardProps {
  id: string;
  name: string;
  description: string;
  techStack: string[];
  status: string;
  teamSize: number;
  skillsNeeded?: string[];
}

export function ProjectCard({ id, name, description, techStack, status, teamSize, skillsNeeded }: ProjectCardProps) {
  return (
    <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30 shadow-sm hover:shadow-lg transition-all hover:-translate-y-1">
      <div className="flex items-start justify-between mb-3">
        <h3 className="text-xl font-bold text-[#1D2233]">{name}</h3>
        <Badge 
          className={status === "Open to contributors" 
            ? "bg-green-100 text-green-700 hover:bg-green-200" 
            : "bg-yellow-100 text-yellow-700 hover:bg-yellow-200"
          }
        >
          {status}
        </Badge>
      </div>
      
      <p className="text-sm text-[#717182] mb-4 line-clamp-2">{description}</p>
      
      <div className="mb-4">
        <p className="text-xs text-[#717182] mb-2">Tech Stack</p>
        <div className="flex flex-wrap gap-2">
          {techStack.map((tech) => (
            <Badge key={tech} className="bg-[#56B2BB]/10 text-[#56B2BB] hover:bg-[#56B2BB]/20">
              {tech}
            </Badge>
          ))}
        </div>
      </div>
      
      {skillsNeeded && skillsNeeded.length > 0 && (
        <div className="mb-4">
          <p className="text-xs text-[#717182] mb-2">Skills Needed</p>
          <div className="flex flex-wrap gap-2">
            {skillsNeeded.map((skill) => (
              <Badge key={skill} variant="outline" className="text-xs">
                {skill}
              </Badge>
            ))}
          </div>
        </div>
      )}
      
      <div className="flex items-center justify-between pt-4 border-t border-[#BAC7CC]/20">
        <div className="flex items-center gap-2 text-sm text-[#717182]">
          <Users className="w-4 h-4" />
          <span>{teamSize} members</span>
        </div>
        <Link to={`/projects/${id}`}>
          <Button variant="ghost" size="sm" className="text-[#56B2BB] hover:text-[#56B2BB]/90">
            View Project
            <ExternalLink className="w-4 h-4 ml-2" />
          </Button>
        </Link>
      </div>
    </div>
  );
}
