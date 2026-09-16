import { useEffect, useState } from "react";
import { useParams, Link, useNavigate } from "react-router";
import { Button } from "../components/ui/button";
import { Badge } from "../components/ui/badge";
import { Github, Users, MessageSquare, Loader2 } from "lucide-react";
import {
  addProjectComment,
  formatProjectStatus,
  formatProjectType,
  getProject,
  getProjectComments,
  getProjectMembers,
  joinProject,
  leaveProject,
  ProjectApiError,
  type ProjectCommentResponseDto,
  type ProjectMemberResponseDto,
  type ProjectResponseDto,
} from "../services/project-api";
import { authService } from "../services/authService";
import { userService } from "../services/userService";
import { Textarea } from "../components/ui/textarea";

export function ProjectDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [project, setProject] = useState<ProjectResponseDto | null>(null);
  const [members, setMembers] = useState<ProjectMemberResponseDto[]>([]);
  const [comments, setComments] = useState<ProjectCommentResponseDto[]>([]);
  const [commentInput, setCommentInput] = useState("");
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [commentLoading, setCommentLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);
  const [usernames, setUsernames] = useState<Record<string, string>>({});

  useEffect(() => {
    const fetchUsernames = async () => {
      const uniqueUserIds = Array.from(
        new Set([
          ...members.map((m) => m.userId),
          ...comments.map((c) => c.userId),
        ])
      ).filter((uid) => uid && !usernames[uid]);

      if (uniqueUserIds.length === 0) return;

      const newNames = { ...usernames };
      await Promise.all(
        uniqueUserIds.map(async (uid) => {
          try {
            const profile = await userService.getUserById(uid);
            newNames[uid] = profile.name || "Unknown User";
          } catch (err) {
            newNames[uid] = uid; // fallback to uuid if request fails
          }
        })
      );
      setUsernames(newNames);
    };

    fetchUsernames();
  }, [members, comments]);

  const loadProject = async () => {
    if (!id) return;
    try {
      setLoading(true);
      setError(null);
      const [projectData, membersData, commentsData] = await Promise.all([
        getProject(id),
        getProjectMembers(id),
        getProjectComments(id, 0, 20),
      ]);
      setProject(projectData);
      setMembers(membersData);
      setComments(commentsData.content);
    } catch (err) {
      setError(
        err instanceof ProjectApiError ? err.message : "Failed to load project"
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProject();
  }, [id]);

  const handleJoinOrLeave = async () => {
    if (!id || !project) return;
    if (!authService.isAuthenticated()) {
      navigate("/auth/login");
      return;
    }
    try {
      setActionLoading(true);
      setActionError(null);
      if (project.userIsMember) {
        await leaveProject(id);
      } else {
        await joinProject(id);
      }
      await loadProject();
    } catch (err) {
      setActionError(
        err instanceof ProjectApiError ? err.message : "Could not update membership"
      );
    } finally {
      setActionLoading(false);
    }
  };

  const handleComment = async () => {
    if (!id || !commentInput.trim()) return;
    if (!authService.isAuthenticated()) {
      navigate("/auth/login");
      return;
    }
    try {
      setCommentLoading(true);
      setActionError(null);
      await addProjectComment(id, commentInput.trim());
      setCommentInput("");
      const latestComments = await getProjectComments(id, 0, 20);
      setComments(latestComments.content);
    } catch (err) {
      setActionError(
        err instanceof ProjectApiError ? err.message : "Could not post comment"
      );
    } finally {
      setCommentLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#F0F4F8] flex items-center justify-center">
        <Loader2 className="w-8 h-8 animate-spin text-[#56B2BB]" />
      </div>
    );
  }

  if (error || !project) {
    return (
      <div className="min-h-screen bg-[#F0F4F8] flex flex-col items-center justify-center gap-4">
        <p className="text-red-500">{error || "Project not found"}</p>
        <Link to="/explore/projects" className="text-[#56B2BB] hover:underline">
          ← Back to Projects
        </Link>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#F0F4F8]">
      <div className="bg-[#1D2233] text-white py-8 px-6">
        <div className="max-w-5xl mx-auto">
          <Link to="/explore/projects" className="text-[#56B2BB] hover:underline mb-4 inline-block">
            ← Back to Projects
          </Link>
          <h1 className="text-4xl font-bold mb-4">{project.title}</h1>
          <div className="flex gap-3 items-center">
            <Badge className="bg-green-500 text-white hover:bg-green-600">
              {formatProjectStatus(project.status)}
            </Badge>
            <div className="flex items-center gap-2 text-sm">
              <Users className="w-4 h-4" />
              <span>{project.memberCount} team members</span>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-5xl mx-auto px-6 py-8">
        <div className="grid lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2 space-y-6">
            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <h2 className="text-2xl font-bold text-[#1D2233] mb-4">About This Project</h2>
              <p className="text-[#717182]">{project.description || "No description provided."}</p>
            </div>

            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <h3 className="font-bold text-[#1D2233] mb-4">Tech Stack</h3>
              <div className="flex flex-wrap gap-2">
                {project.technologies?.length ? (
                  project.technologies.map((tech) => (
                    <Badge key={tech} className="bg-[#56B2BB]/10 text-[#56B2BB]">
                      {tech}
                    </Badge>
                  ))
                ) : (
                  <p className="text-[#717182] text-sm">No technologies listed.</p>
                )}
              </div>
            </div>

            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <h3 className="font-bold text-[#1D2233] mb-4">Comments & Discussion</h3>
              <div className="space-y-4 mb-4">
                {comments.length === 0 ? (
                  <p className="text-sm text-[#717182]">No comments yet.</p>
                ) : (
                  comments.map((comment) => (
                    <div key={comment.id} className="flex gap-3">
                      <div className="w-10 h-10 bg-[#56B2BB]/10 rounded-full flex items-center justify-center">
                        <span className="font-bold text-[#56B2BB]">
                          {usernames[comment.userId] ? usernames[comment.userId].charAt(0).toUpperCase() : "U"}
                        </span>
                      </div>
                      <div className="flex-1">
                        <p className="font-medium text-[#1D2233]">{usernames[comment.userId] || comment.userId}</p>
                        <p className="text-sm text-[#717182] mt-1">{comment.content}</p>
                        <p className="text-xs text-[#56B2BB] mt-2">
                          {new Date(comment.createdAt).toLocaleString()}
                        </p>
                      </div>
                    </div>
                  ))
                )}
              </div>
              <div className="space-y-2">
                <Textarea
                  value={commentInput}
                  onChange={(e) => setCommentInput(e.target.value)}
                  placeholder="Write a comment..."
                  className="bg-[#F0F4F8]"
                />
                <Button
                  type="button"
                  onClick={handleComment}
                  disabled={commentLoading || !commentInput.trim()}
                  className="bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white"
                >
                  {commentLoading ? "Posting..." : "Post Comment"}
                </Button>
                {actionError && <p className="text-red-500 text-sm">{actionError}</p>}
              </div>
            </div>
          </div>

          <div className="space-y-6">
            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <Button
                className="w-full bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white mb-3"
                onClick={handleJoinOrLeave}
                disabled={actionLoading}
              >
                {actionLoading
                  ? "Please wait..."
                  : project.userIsMember
                    ? "Leave Project"
                    : "Join Project"}
              </Button>
              <Button variant="outline" className="w-full border-[#BAC7CC]/30" disabled>
                <MessageSquare className="w-4 h-4 mr-2" />
                Contact Team
              </Button>
            </div>

            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <h3 className="font-bold text-[#1D2233] mb-4">Project Info</h3>
              <div className="space-y-2 text-sm">
                <p><span className="font-medium">Type:</span> {formatProjectType(project.type)}</p>
                <p><span className="font-medium">Status:</span> {formatProjectStatus(project.status)}</p>
                <p><span className="font-medium">Created:</span> {new Date(project.createdAt).toLocaleDateString()}</p>
              </div>
            </div>

            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <h3 className="font-bold text-[#1D2233] mb-4">Skills Needed</h3>
              <div className="flex flex-wrap gap-2">
                {project.skillsNeeded?.length ? (
                  project.skillsNeeded.map((skill) => (
                    <Badge key={skill} variant="outline">
                      {skill}
                    </Badge>
                  ))
                ) : (
                  <p className="text-sm text-[#717182]">No skills listed.</p>
                )}
              </div>
            </div>

            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <h3 className="font-bold text-[#1D2233] mb-4">Team Members</h3>
              <div className="space-y-3">
                {members.length === 0 ? (
                  <p className="text-sm text-[#717182]">No members yet.</p>
                ) : (
                  members.map((member) => (
                    <div key={member.id} className="flex items-center gap-3">
                      <div className="w-10 h-10 bg-[#56B2BB]/10 rounded-full flex items-center justify-center">
                        <span className="font-bold text-[#56B2BB]">
                          {usernames[member.userId] ? usernames[member.userId].charAt(0).toUpperCase() : "U"}
                        </span>
                      </div>
                      <div>
                        <p className="font-medium text-[#1D2233]">{usernames[member.userId] || member.userId}</p>
                        <p className="text-xs text-[#717182]">
                          {member.role === "OWNER" ? "Project Lead" : "Member"}
                        </p>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>

            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              {project.githubUrl ? (
                <a href={project.githubUrl} target="_blank" rel="noreferrer" className="flex items-center gap-2 text-[#56B2BB] hover:underline">
                  <Github className="w-5 h-5" />
                  View on GitHub
                </a>
              ) : (
                <p className="text-sm text-[#717182]">No GitHub repository linked.</p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
