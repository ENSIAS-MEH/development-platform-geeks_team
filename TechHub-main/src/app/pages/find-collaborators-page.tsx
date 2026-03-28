import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Search } from "lucide-react";

export function FindCollaboratorsPage() {
  const profiles = [
    { name: "Sarah Chen", role: "ML Engineer", skills: ["Python", "TensorFlow", "PyTorch"], location: "San Francisco" },
    { name: "Mike Johnson", role: "Full Stack Developer", skills: ["React", "Node.js", "AWS"], location: "New York" },
    { name: "Emma Wilson", role: "UI/UX Designer", skills: ["Figma", "React", "Design Systems"], location: "London" },
  ];

  return (
    <div className="p-8">
      <h1 className="text-4xl font-bold text-[#1D2233] mb-2">Find Collaborators</h1>
      <p className="text-lg text-[#717182] mb-8">Connect with developers who have the skills you need</p>

      <div className="bg-white rounded-xl p-6 mb-6 border border-[#BAC7CC]/30">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-[#717182]" />
          <Input placeholder="Search by skills, role, or location..." className="pl-10 bg-[#F0F4F8]" />
        </div>
      </div>

      <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
        {profiles.map((profile) => (
          <div key={profile.name} className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30 shadow-sm">
            <div className="w-16 h-16 bg-[#56B2BB]/10 rounded-full flex items-center justify-center mb-4 mx-auto">
              <span className="text-2xl font-bold text-[#56B2BB]">{profile.name.charAt(0)}</span>
            </div>
            <h3 className="font-bold text-[#1D2233] text-center">{profile.name}</h3>
            <p className="text-sm text-[#717182] text-center mb-3">{profile.role}</p>
            <p className="text-xs text-[#717182] text-center mb-4">{profile.location}</p>
            <div className="flex flex-wrap gap-2 justify-center mb-4">
              {profile.skills.map((skill) => (
                <Badge key={skill} variant="outline" className="text-xs">{skill}</Badge>
              ))}
            </div>
            <Button className="w-full bg-[#56B2BB] hover:bg-[#56B2BB]/90">View Profile</Button>
          </div>
        ))}
      </div>
    </div>
  );
}
