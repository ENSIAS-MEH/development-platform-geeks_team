import { useState } from "react";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Label } from "../components/ui/label";
import { Textarea } from "../components/ui/textarea";
import { Badge } from "../components/ui/badge";
import { X } from "lucide-react";

export function CreateProjectPage() {
  const [techStack, setTechStack] = useState<string[]>(["React", "Node.js"]);
  const [skillsNeeded, setSkillsNeeded] = useState<string[]>(["Frontend", "Backend"]);
  const [newTech, setNewTech] = useState("");
  const [newSkill, setNewSkill] = useState("");

  const addTech = () => {
    if (newTech && !techStack.includes(newTech)) {
      setTechStack([...techStack, newTech]);
      setNewTech("");
    }
  };

  const addSkill = () => {
    if (newSkill && !skillsNeeded.includes(newSkill)) {
      setSkillsNeeded([...skillsNeeded, newSkill]);
      setNewSkill("");
    }
  };

  return (
    <div className="p-8">
      <div className="max-w-3xl mx-auto">
        <h1 className="text-4xl font-bold text-[#1D2233] mb-2">Create New Project</h1>
        <p className="text-lg text-[#717182] mb-8">Share your idea and find collaborators</p>

        <div className="bg-white rounded-xl p-8 border border-[#BAC7CC]/30 shadow-sm">
          <form className="space-y-6">
            <div>
              <Label htmlFor="title">Project Title *</Label>
              <Input
                id="title"
                placeholder="e.g., AI-Powered Study Assistant"
                className="mt-1.5 bg-[#F0F4F8] border-[#BAC7CC]/30"
              />
            </div>

            <div>
              <Label htmlFor="description">Description *</Label>
              <Textarea
                id="description"
                placeholder="Describe your project, its goals, and what makes it unique..."
                rows={6}
                className="mt-1.5 bg-[#F0F4F8] border-[#BAC7CC]/30"
              />
            </div>

            <div>
              <Label>Tech Stack *</Label>
              <div className="mt-1.5 flex flex-wrap gap-2 mb-3">
                {techStack.map((tech) => (
                  <Badge key={tech} className="bg-[#56B2BB]/10 text-[#56B2BB] hover:bg-[#56B2BB]/20">
                    {tech}
                    <button
                      type="button"
                      onClick={() => setTechStack(techStack.filter(t => t !== tech))}
                      className="ml-2"
                    >
                      <X className="w-3 h-3" />
                    </button>
                  </Badge>
                ))}
              </div>
              <div className="flex gap-2">
                <Input
                  value={newTech}
                  onChange={(e) => setNewTech(e.target.value)}
                  placeholder="Add technology"
                  className="bg-[#F0F4F8] border-[#BAC7CC]/30"
                  onKeyPress={(e) => e.key === 'Enter' && (e.preventDefault(), addTech())}
                />
                <Button type="button" onClick={addTech} className="bg-[#56B2BB] hover:bg-[#56B2BB]/90">
                  Add
                </Button>
              </div>
            </div>

            <div>
              <Label>Skills Needed</Label>
              <div className="mt-1.5 flex flex-wrap gap-2 mb-3">
                {skillsNeeded.map((skill) => (
                  <Badge key={skill} variant="outline">
                    {skill}
                    <button
                      type="button"
                      onClick={() => setSkillsNeeded(skillsNeeded.filter(s => s !== skill))}
                      className="ml-2"
                    >
                      <X className="w-3 h-3" />
                    </button>
                  </Badge>
                ))}
              </div>
              <div className="flex gap-2">
                <Input
                  value={newSkill}
                  onChange={(e) => setNewSkill(e.target.value)}
                  placeholder="Add skill"
                  className="bg-[#F0F4F8] border-[#BAC7CC]/30"
                  onKeyPress={(e) => e.key === 'Enter' && (e.preventDefault(), addSkill())}
                />
                <Button type="button" onClick={addSkill} variant="outline">
                  Add
                </Button>
              </div>
            </div>

            <div className="grid md:grid-cols-2 gap-4">
              <div>
                <Label htmlFor="status">Project Status</Label>
                <select
                  id="status"
                  className="mt-1.5 w-full rounded-md border border-[#BAC7CC]/30 bg-[#F0F4F8] px-3 py-2"
                >
                  <option>Planning</option>
                  <option>In Progress</option>
                  <option>Open to Contributors</option>
                  <option>Beta Testing</option>
                </select>
              </div>

              <div>
                <Label htmlFor="teamSize">Team Size Sought</Label>
                <Input
                  id="teamSize"
                  type="number"
                  placeholder="5"
                  className="mt-1.5 bg-[#F0F4F8] border-[#BAC7CC]/30"
                />
              </div>
            </div>

            <div>
              <Label htmlFor="github">GitHub Repository (optional)</Label>
              <Input
                id="github"
                placeholder="https://github.com/username/repo"
                className="mt-1.5 bg-[#F0F4F8] border-[#BAC7CC]/30"
              />
            </div>

            <div className="flex gap-4 pt-4">
              <Button type="submit" className="bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white flex-1">
                Publish Project
              </Button>
              <Button type="button" variant="outline" className="border-[#BAC7CC]/30">
                Save as Draft
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
