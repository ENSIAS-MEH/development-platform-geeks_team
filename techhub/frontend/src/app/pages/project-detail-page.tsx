import { useParams, Link } from "react-router";
import { Button } from "../components/ui/button";
import { Badge } from "../components/ui/badge";
import { Github, Users, Calendar, MessageSquare } from "lucide-react";

export function ProjectDetailPage() {
  const { id } = useParams();

  return (
    <div className="min-h-screen bg-[#F0F4F8]">
      <div className="bg-[#1D2233] text-white py-8 px-6">
        <div className="max-w-5xl mx-auto">
          <Link to="/explore/projects" className="text-[#56B2BB] hover:underline mb-4 inline-block">
            ← Back to Projects
          </Link>
          <h1 className="text-4xl font-bold mb-4">AI Study Buddy</h1>
          <div className="flex gap-3 items-center">
            <Badge className="bg-green-500 text-white hover:bg-green-600">Open to contributors</Badge>
            <div className="flex items-center gap-2 text-sm">
              <Users className="w-4 h-4" />
              <span>5 team members</span>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-5xl mx-auto px-6 py-8">
        <div className="grid lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2 space-y-6">
            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <h2 className="text-2xl font-bold text-[#1D2233] mb-4">About This Project</h2>
              <p className="text-[#717182] mb-4">
                An AI-powered learning assistant that helps students prepare for exams with personalized quizzes, 
                study plans, and progress tracking. The platform uses machine learning to adapt to each student's 
                learning style and identify areas for improvement.
              </p>
              <p className="text-[#717182]">
                We're building this to make studying more efficient and less stressful for students worldwide. 
                Currently in beta with over 100 active users providing valuable feedback.
              </p>
            </div>

            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <h3 className="font-bold text-[#1D2233] mb-4">Tech Stack</h3>
              <div className="flex flex-wrap gap-2">
                <Badge className="bg-[#56B2BB]/10 text-[#56B2BB]">Python</Badge>
                <Badge className="bg-[#56B2BB]/10 text-[#56B2BB]">TensorFlow</Badge>
                <Badge className="bg-[#56B2BB]/10 text-[#56B2BB]">React</Badge>
                <Badge className="bg-[#56B2BB]/10 text-[#56B2BB]">FastAPI</Badge>
              </div>
            </div>

            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <h3 className="font-bold text-[#1D2233] mb-4">Comments & Discussion</h3>
              <div className="space-y-4">
                <div className="flex gap-3">
                  <div className="w-10 h-10 bg-[#56B2BB]/10 rounded-full flex items-center justify-center">
                    <span className="font-bold text-[#56B2BB]">SC</span>
                  </div>
                  <div className="flex-1">
                    <p className="font-medium text-[#1D2233]">Sarah Chen</p>
                    <p className="text-sm text-[#717182] mt-1">This looks amazing! I'd love to contribute to the ML models.</p>
                    <p className="text-xs text-[#56B2BB] mt-2">2 hours ago</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="space-y-6">
            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <Button className="w-full bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white mb-3">
                Join Project
              </Button>
              <Button variant="outline" className="w-full border-[#BAC7CC]/30">
                <MessageSquare className="w-4 h-4 mr-2" />
                Contact Team
              </Button>
            </div>

            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <h3 className="font-bold text-[#1D2233] mb-4">Skills Needed</h3>
              <div className="flex flex-wrap gap-2">
                <Badge variant="outline">Machine Learning</Badge>
                <Badge variant="outline">Frontend Development</Badge>
                <Badge variant="outline">UX Design</Badge>
              </div>
            </div>

            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <h3 className="font-bold text-[#1D2233] mb-4">Team Members</h3>
              <div className="space-y-3">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-[#56B2BB]/10 rounded-full flex items-center justify-center">
                    <span className="font-bold text-[#56B2BB]">AD</span>
                  </div>
                  <div>
                    <p className="font-medium text-[#1D2233]">Alex Davis</p>
                    <p className="text-xs text-[#717182]">Project Lead</p>
                  </div>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <a href="#" className="flex items-center gap-2 text-[#56B2BB] hover:underline">
                <Github className="w-5 h-5" />
                View on GitHub
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
