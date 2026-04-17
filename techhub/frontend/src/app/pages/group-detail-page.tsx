import { useState, useEffect, useCallback } from "react";
import { useParams, useNavigate } from "react-router";
import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Label } from "../components/ui/label";
import { Textarea } from "../components/ui/textarea";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../components/ui/select";
import {
  ArrowLeft,
  Users,
  MessageSquare,
  ThumbsUp,
  Pin,
  Send,
  Loader2,
  ChevronDown,
  ChevronUp,
  Plus,
  Trash2,
  Globe,
  Lock,
} from "lucide-react";
import { COMMUNITY_TOPICS, POST_TYPES } from "../config/constants";
import {
  getGroupById,
  getGroupPosts,
  getGroupMembers,
  createPost,
  upvotePost,
  getComments,
  createComment,
  upvoteComment,
  leaveGroup,
  deletePost,
  deleteComment,
  ApiError,
} from "../services/community-api";
import type {
  GroupResponse,
  PostResponse,
  CommentResponse,
  GroupMemberResponse,
  PostRequest,
  PostType,
} from "../types";

export function GroupDetailPage() {
  const { groupId } = useParams<{ groupId: string }>();
  const navigate = useNavigate();

  // ─── Group State ──────────────────────────────────────────────────
  const [group, setGroup] = useState<GroupResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // ─── Tab State ────────────────────────────────────────────────────
  const [activeTab, setActiveTab] = useState<"posts" | "members">("posts");

  // ─── Posts State ──────────────────────────────────────────────────
  const [posts, setPosts] = useState<PostResponse[]>([]);
  const [postsLoading, setPostsLoading] = useState(false);
  const [showCreatePost, setShowCreatePost] = useState(false);
  const [newPost, setNewPost] = useState<PostRequest>({
    title: "",
    content: "",
    type: "DISCUSSION",
  });
  const [creatingPost, setCreatingPost] = useState(false);

  // ─── Members State ────────────────────────────────────────────────
  const [members, setMembers] = useState<GroupMemberResponse[]>([]);
  const [membersLoading, setMembersLoading] = useState(false);

  // ─── Comments State ───────────────────────────────────────────────
  const [expandedComments, setExpandedComments] = useState<Set<string>>(new Set());
  const [commentsByPost, setCommentsByPost] = useState<Record<string, CommentResponse[]>>({});
  const [commentInputs, setCommentInputs] = useState<Record<string, string>>({});
  const [replyInputs, setReplyInputs] = useState<Record<string, string>>({});
  const [replyToComment, setReplyToComment] = useState<string | null>(null);

  // ─── Fetch Group ──────────────────────────────────────────────────
  const fetchGroup = useCallback(async () => {
    if (!groupId) return;
    setLoading(true);
    try {
      const data = await getGroupById(groupId);
      setGroup(data);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Failed to load group");
    } finally {
      setLoading(false);
    }
  }, [groupId]);

  const fetchPosts = useCallback(async () => {
    if (!groupId) return;
    setPostsLoading(true);
    try {
      const data = await getGroupPosts(groupId);
      setPosts(data.content);
    } catch (err) {
      console.error("Failed to load posts:", err);
    } finally {
      setPostsLoading(false);
    }
  }, [groupId]);

  const fetchMembers = useCallback(async () => {
    if (!groupId) return;
    setMembersLoading(true);
    try {
      const data = await getGroupMembers(groupId);
      setMembers(data.content);
    } catch (err) {
      console.error("Failed to load members:", err);
    } finally {
      setMembersLoading(false);
    }
  }, [groupId]);

  useEffect(() => {
    fetchGroup();
    fetchPosts();
  }, [fetchGroup, fetchPosts]);

  useEffect(() => {
    if (activeTab === "members") {
      fetchMembers();
    }
  }, [activeTab, fetchMembers]);

  // ─── Post Handlers ────────────────────────────────────────────────
  const handleCreatePost = async () => {
    if (!groupId || !newPost.title.trim() || !newPost.content.trim()) return;
    setCreatingPost(true);
    try {
      await createPost(groupId, newPost);
      setNewPost({ title: "", content: "", type: "DISCUSSION" });
      setShowCreatePost(false);
      fetchPosts();
    } catch (err) {
      alert(err instanceof ApiError ? err.message : "Failed to create post");
    } finally {
      setCreatingPost(false);
    }
  };

  const handleUpvotePost = async (postId: string) => {
    if (!groupId) return;
    try {
      await upvotePost(groupId, postId);
      setPosts((prev) =>
        prev.map((p) =>
          p.id === postId ? { ...p, upvotes: p.upvotes + 1 } : p
        )
      );
    } catch (err) {
      console.error("Failed to upvote:", err);
    }
  };

  const handleDeletePost = async (postId: string) => {
    if (!groupId || !confirm("Delete this post?")) return;
    try {
      await deletePost(groupId, postId);
      setPosts((prev) => prev.filter((p) => p.id !== postId));
    } catch (err) {
      alert(err instanceof ApiError ? err.message : "Failed to delete post");
    }
  };

  // ─── Comment Handlers ────────────────────────────────────────────
  const toggleComments = async (postId: string) => {
    const newExpanded = new Set(expandedComments);
    if (newExpanded.has(postId)) {
      newExpanded.delete(postId);
    } else {
      newExpanded.add(postId);
      if (!commentsByPost[postId]) {
        try {
          const data = await getComments(groupId!, postId);
          setCommentsByPost((prev) => ({ ...prev, [postId]: data.content }));
        } catch (err) {
          console.error("Failed to load comments:", err);
        }
      }
    }
    setExpandedComments(newExpanded);
  };

  const handleCreateComment = async (postId: string) => {
    const content = commentInputs[postId]?.trim();
    if (!groupId || !content) return;
    try {
      const comment = await createComment(groupId, postId, { content });
      setCommentsByPost((prev) => ({
        ...prev,
        [postId]: [...(prev[postId] || []), comment],
      }));
      setCommentInputs((prev) => ({ ...prev, [postId]: "" }));
      // Update comment count
      setPosts((prev) =>
        prev.map((p) =>
          p.id === postId ? { ...p, commentCount: p.commentCount + 1 } : p
        )
      );
    } catch (err) {
      alert(err instanceof ApiError ? err.message : "Failed to add comment");
    }
  };

  const handleReplyComment = async (postId: string, parentCommentId: string) => {
    const content = replyInputs[parentCommentId]?.trim();
    if (!groupId || !content) return;
    try {
      const reply = await createComment(groupId, postId, {
        content,
        parentCommentId,
      });
      setCommentsByPost((prev) => ({
        ...prev,
        [postId]: (prev[postId] || []).map((c) =>
          c.id === parentCommentId
            ? { ...c, replies: [...(c.replies || []), reply] }
            : c
        ),
      }));
      setReplyInputs((prev) => ({ ...prev, [parentCommentId]: "" }));
      setReplyToComment(null);
      setPosts((prev) =>
        prev.map((p) =>
          p.id === postId ? { ...p, commentCount: p.commentCount + 1 } : p
        )
      );
    } catch (err) {
      alert(err instanceof ApiError ? err.message : "Failed to reply");
    }
  };

  const handleUpvoteComment = async (postId: string, commentId: string) => {
    if (!groupId) return;
    try {
      await upvoteComment(groupId, postId, commentId);
      setCommentsByPost((prev) => ({
        ...prev,
        [postId]: (prev[postId] || []).map((c) =>
          c.id === commentId
            ? { ...c, upvotes: c.upvotes + 1 }
            : {
                ...c,
                replies: (c.replies || []).map((r) =>
                  r.id === commentId ? { ...r, upvotes: r.upvotes + 1 } : r
                ),
              }
        ),
      }));
    } catch (err) {
      console.error("Failed to upvote comment:", err);
    }
  };

  const handleDeleteComment = async (postId: string, commentId: string) => {
    if (!groupId || !confirm("Delete this comment?")) return;
    try {
      await deleteComment(groupId, postId, commentId);
      setCommentsByPost((prev) => ({
        ...prev,
        [postId]: (prev[postId] || []).filter((c) => c.id !== commentId),
      }));
      setPosts((prev) =>
        prev.map((p) =>
          p.id === postId ? { ...p, commentCount: Math.max(0, p.commentCount - 1) } : p
        )
      );
    } catch (err) {
      alert(err instanceof ApiError ? err.message : "Failed to delete comment");
    }
  };

  // ─── Leave Group ──────────────────────────────────────────────────
  const handleLeaveGroup = async () => {
    if (!groupId || !confirm("Are you sure you want to leave this group?")) return;
    try {
      await leaveGroup(groupId);
      navigate("/dashboard/communities");
    } catch (err) {
      alert(err instanceof ApiError ? err.message : "Failed to leave group");
    }
  };

  // ─── Helpers ──────────────────────────────────────────────────────
  const getTopicLabel = (topic: string) =>
    COMMUNITY_TOPICS.find((t) => t.value === topic)?.label || topic;

  const getTopicColor = (topic: string) => {
    const colorMap: Record<string, string> = {
      WEB: "bg-blue-100 text-blue-700",
      MOBILE: "bg-purple-100 text-purple-700",
      AI_ML: "bg-orange-100 text-orange-700",
      DEVOPS: "bg-green-100 text-green-700",
      SECURITY: "bg-red-100 text-red-700",
      GAME_DEV: "bg-pink-100 text-pink-700",
      DATA: "bg-cyan-100 text-cyan-700",
      OTHER: "bg-gray-100 text-gray-700",
    };
    return colorMap[topic] || "bg-gray-100 text-gray-700";
  };

  const getPostTypeColor = (type: string) => {
    const colorMap: Record<string, string> = {
      DISCUSSION: "bg-blue-100 text-blue-700",
      ANNOUNCEMENT: "bg-amber-100 text-amber-700",
      RESOURCE: "bg-emerald-100 text-emerald-700",
    };
    return colorMap[type] || "bg-gray-100 text-gray-700";
  };

  const getPostTypeLabel = (type: string) =>
    POST_TYPES.find((t) => t.value === type)?.label || type;

  const formatDate = (dateStr: string) => {
    try {
      return new Date(dateStr).toLocaleDateString("en-US", {
        year: "numeric",
        month: "short",
        day: "numeric",
      });
    } catch {
      return dateStr;
    }
  };

  const getRoleColor = (role: string) => {
    const colorMap: Record<string, string> = {
      OWNER: "bg-amber-100 text-amber-700",
      MODERATOR: "bg-blue-100 text-blue-700",
      MEMBER: "bg-gray-100 text-gray-700",
    };
    return colorMap[role] || "bg-gray-100 text-gray-700";
  };

  // ─── Loading / Error ──────────────────────────────────────────────
  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <Loader2 className="w-8 h-8 animate-spin text-[#56B2BB]" />
        <span className="ml-3 text-[#717182]">Loading group...</span>
      </div>
    );
  }

  if (error || !group) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh]">
        <p className="text-red-500 mb-4">{error || "Group not found"}</p>
        <Button variant="outline" onClick={() => navigate("/dashboard/communities")}>
          ← Back to Communities
        </Button>
      </div>
    );
  }

  // ─── Main Render ──────────────────────────────────────────────────
  return (
    <div className="p-8 max-w-4xl mx-auto">
      {/* Back Button */}
      <Button
        variant="ghost"
        className="mb-6 text-[#717182] hover:text-[#1D2233]"
        onClick={() => navigate("/dashboard/communities")}
      >
        <ArrowLeft className="w-4 h-4 mr-2" />
        Back to Communities
      </Button>

      {/* Group Header */}
      <div className="bg-white rounded-xl p-8 border border-[#BAC7CC]/30 shadow-sm mb-6">
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <div className="flex items-center gap-3 mb-2">
              <h1 className="text-3xl font-bold text-[#1D2233]">{group.name}</h1>
              {group.isPublic ? (
                <Globe className="w-5 h-5 text-[#56B2BB]" />
              ) : (
                <Lock className="w-5 h-5 text-[#717182]" />
              )}
            </div>
            <p className="text-[#717182] mb-4">{group.description || "No description."}</p>
            <div className="flex items-center gap-4">
              <Badge className={getTopicColor(group.topic)}>{getTopicLabel(group.topic)}</Badge>
              <div className="flex items-center gap-1 text-sm text-[#717182]">
                <Users className="w-4 h-4" />
                <span>{group.memberCount} members</span>
              </div>
              <span className="text-sm text-[#717182]">Created {formatDate(group.createdAt)}</span>
            </div>
          </div>
          <Button
            variant="outline"
            size="sm"
            className="text-red-500 border-red-200 hover:bg-red-50"
            onClick={handleLeaveGroup}
          >
            Leave Group
          </Button>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-1 mb-6 bg-[#F0F4F8] rounded-lg p-1">
        <button
          className={`flex-1 py-2 px-4 rounded-md text-sm font-medium transition-colors ${
            activeTab === "posts"
              ? "bg-white text-[#1D2233] shadow-sm"
              : "text-[#717182] hover:text-[#1D2233]"
          }`}
          onClick={() => setActiveTab("posts")}
        >
          Posts
        </button>
        <button
          className={`flex-1 py-2 px-4 rounded-md text-sm font-medium transition-colors ${
            activeTab === "members"
              ? "bg-white text-[#1D2233] shadow-sm"
              : "text-[#717182] hover:text-[#1D2233]"
          }`}
          onClick={() => setActiveTab("members")}
        >
          Members ({group.memberCount})
        </button>
      </div>

      {/* ═══════════════════════ POSTS TAB ═══════════════════════════════ */}
      {activeTab === "posts" && (
        <div className="space-y-4">
          {/* Create Post Toggle */}
          <Button
            onClick={() => setShowCreatePost(!showCreatePost)}
            className="bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white gap-2"
          >
            <Plus className="w-4 h-4" />
            New Post
          </Button>

          {/* Create Post Form */}
          {showCreatePost && (
            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30 shadow-sm">
              <h3 className="font-semibold text-[#1D2233] mb-4">Create a Post</h3>
              <div className="space-y-4">
                <div>
                  <Label htmlFor="post-title">Title *</Label>
                  <Input
                    id="post-title"
                    placeholder="Post title..."
                    value={newPost.title}
                    onChange={(e) => setNewPost({ ...newPost, title: e.target.value })}
                    maxLength={200}
                  />
                </div>
                <div>
                  <Label htmlFor="post-content">Content *</Label>
                  <Textarea
                    id="post-content"
                    placeholder="Write your post content..."
                    value={newPost.content}
                    onChange={(e) => setNewPost({ ...newPost, content: e.target.value })}
                    rows={4}
                  />
                </div>
                <div>
                  <Label>Post Type</Label>
                  <Select
                    value={newPost.type}
                    onValueChange={(val) => setNewPost({ ...newPost, type: val as PostType })}
                  >
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      {POST_TYPES.map((t) => (
                        <SelectItem key={t.value} value={t.value}>
                          {t.label}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
                <div className="flex gap-2">
                  <Button
                    onClick={handleCreatePost}
                    disabled={creatingPost || !newPost.title.trim() || !newPost.content.trim()}
                    className="bg-[#56B2BB] hover:bg-[#56B2BB]/90"
                  >
                    {creatingPost ? (
                      <Loader2 className="w-4 h-4 animate-spin" />
                    ) : (
                      "Publish"
                    )}
                  </Button>
                  <Button variant="outline" onClick={() => setShowCreatePost(false)}>
                    Cancel
                  </Button>
                </div>
              </div>
            </div>
          )}

          {/* Posts Loading */}
          {postsLoading && (
            <div className="flex items-center justify-center py-12">
              <Loader2 className="w-6 h-6 animate-spin text-[#56B2BB]" />
            </div>
          )}

          {/* Posts List */}
          {!postsLoading && posts.length === 0 && (
            <div className="text-center py-12 bg-white rounded-xl border border-[#BAC7CC]/30">
              <MessageSquare className="w-12 h-12 text-[#BAC7CC] mx-auto mb-3" />
              <p className="text-[#717182]">No posts yet. Be the first to post!</p>
            </div>
          )}

          {posts.map((post) => (
            <div
              key={post.id}
              className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30 shadow-sm"
            >
              {/* Post Header */}
              <div className="flex items-start justify-between mb-3">
                <div className="flex items-center gap-2">
                  <Badge className={getPostTypeColor(post.type)}>
                    {getPostTypeLabel(post.type)}
                  </Badge>
                  {post.isPinned && (
                    <Badge className="bg-amber-100 text-amber-700">
                      <Pin className="w-3 h-3 mr-1" /> Pinned
                    </Badge>
                  )}
                </div>
                <Button
                  variant="ghost"
                  size="sm"
                  className="text-red-400 hover:text-red-600"
                  onClick={() => handleDeletePost(post.id)}
                >
                  <Trash2 className="w-4 h-4" />
                </Button>
              </div>

              {/* Post Content */}
              <h3 className="text-lg font-semibold text-[#1D2233] mb-2">{post.title}</h3>
              <p className="text-[#717182] mb-4 whitespace-pre-wrap">{post.content}</p>

              {/* Post Meta */}
              <div className="flex items-center gap-4 text-sm text-[#717182] mb-3">
                <span>{formatDate(post.createdAt)}</span>
                {post.updatedAt !== post.createdAt && (
                  <span className="italic">edited {formatDate(post.updatedAt)}</span>
                )}
              </div>

              {/* Post Actions */}
              <div className="flex items-center gap-3 pt-3 border-t border-[#BAC7CC]/20">
                <Button
                  variant="ghost"
                  size="sm"
                  className="text-[#717182] hover:text-[#56B2BB] gap-1"
                  onClick={() => handleUpvotePost(post.id)}
                >
                  <ThumbsUp className="w-4 h-4" />
                  {post.upvotes}
                </Button>
                <Button
                  variant="ghost"
                  size="sm"
                  className="text-[#717182] hover:text-[#56B2BB] gap-1"
                  onClick={() => toggleComments(post.id)}
                >
                  <MessageSquare className="w-4 h-4" />
                  {post.commentCount}
                  {expandedComments.has(post.id) ? (
                    <ChevronUp className="w-3 h-3" />
                  ) : (
                    <ChevronDown className="w-3 h-3" />
                  )}
                </Button>
              </div>

              {/* Comments Section */}
              {expandedComments.has(post.id) && (
                <div className="mt-4 pt-4 border-t border-[#BAC7CC]/20 space-y-3">
                  {/* Existing Comments */}
                  {(commentsByPost[post.id] || []).map((comment) => (
                    <div key={comment.id} className="pl-4 border-l-2 border-[#BAC7CC]/30">
                      <div className="bg-[#F0F4F8] rounded-lg p-3">
                        <p className="text-sm text-[#1D2233]">{comment.content}</p>
                        <div className="flex items-center gap-3 mt-2 text-xs text-[#717182]">
                          <span>{formatDate(comment.createdAt)}</span>
                          <button
                            className="flex items-center gap-1 hover:text-[#56B2BB]"
                            onClick={() => handleUpvoteComment(post.id, comment.id)}
                          >
                            <ThumbsUp className="w-3 h-3" /> {comment.upvotes}
                          </button>
                          <button
                            className="hover:text-[#56B2BB]"
                            onClick={() =>
                              setReplyToComment(
                                replyToComment === comment.id ? null : comment.id
                              )
                            }
                          >
                            Reply
                          </button>
                          <button
                            className="hover:text-red-500"
                            onClick={() => handleDeleteComment(post.id, comment.id)}
                          >
                            <Trash2 className="w-3 h-3" />
                          </button>
                        </div>
                      </div>

                      {/* Reply Input */}
                      {replyToComment === comment.id && (
                        <div className="flex gap-2 mt-2 ml-4">
                          <Input
                            placeholder="Write a reply..."
                            value={replyInputs[comment.id] || ""}
                            onChange={(e) =>
                              setReplyInputs((prev) => ({
                                ...prev,
                                [comment.id]: e.target.value,
                              }))
                            }
                            onKeyDown={(e) => {
                              if (e.key === "Enter")
                                handleReplyComment(post.id, comment.id);
                            }}
                            className="text-sm"
                          />
                          <Button
                            size="sm"
                            className="bg-[#56B2BB] hover:bg-[#56B2BB]/90"
                            onClick={() => handleReplyComment(post.id, comment.id)}
                          >
                            <Send className="w-3 h-3" />
                          </Button>
                        </div>
                      )}

                      {/* Nested Replies */}
                      {comment.replies &&
                        comment.replies.map((reply) => (
                          <div key={reply.id} className="ml-6 mt-2">
                            <div className="bg-white rounded-lg p-3 border border-[#BAC7CC]/20">
                              <p className="text-sm text-[#1D2233]">{reply.content}</p>
                              <div className="flex items-center gap-3 mt-2 text-xs text-[#717182]">
                                <span>{formatDate(reply.createdAt)}</span>
                                <button
                                  className="flex items-center gap-1 hover:text-[#56B2BB]"
                                  onClick={() =>
                                    handleUpvoteComment(post.id, reply.id)
                                  }
                                >
                                  <ThumbsUp className="w-3 h-3" /> {reply.upvotes}
                                </button>
                              </div>
                            </div>
                          </div>
                        ))}
                    </div>
                  ))}

                  {/* New Comment Input */}
                  <div className="flex gap-2 pt-2">
                    <Input
                      placeholder="Write a comment..."
                      value={commentInputs[post.id] || ""}
                      onChange={(e) =>
                        setCommentInputs((prev) => ({
                          ...prev,
                          [post.id]: e.target.value,
                        }))
                      }
                      onKeyDown={(e) => {
                        if (e.key === "Enter") handleCreateComment(post.id);
                      }}
                    />
                    <Button
                      className="bg-[#56B2BB] hover:bg-[#56B2BB]/90"
                      onClick={() => handleCreateComment(post.id)}
                      disabled={!commentInputs[post.id]?.trim()}
                    >
                      <Send className="w-4 h-4" />
                    </Button>
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {/* ═══════════════════════ MEMBERS TAB ══════════════════════════════ */}
      {activeTab === "members" && (
        <div className="bg-white rounded-xl border border-[#BAC7CC]/30 shadow-sm">
          {membersLoading ? (
            <div className="flex items-center justify-center py-12">
              <Loader2 className="w-6 h-6 animate-spin text-[#56B2BB]" />
            </div>
          ) : members.length === 0 ? (
            <div className="text-center py-12">
              <Users className="w-12 h-12 text-[#BAC7CC] mx-auto mb-3" />
              <p className="text-[#717182]">No members found.</p>
            </div>
          ) : (
            <div className="divide-y divide-[#BAC7CC]/20">
              {members.map((member) => (
                <div
                  key={member.id}
                  className="flex items-center justify-between px-6 py-4"
                >
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-full bg-[#56B2BB] flex items-center justify-center text-white font-semibold text-sm">
                      {member.userId.substring(member.userId.length - 2).toUpperCase()}
                    </div>
                    <div>
                      <p className="text-sm font-medium text-[#1D2233]">
                        User {member.userId.substring(0, 8)}...
                      </p>
                      <p className="text-xs text-[#717182]">
                        Joined {formatDate(member.joinedAt)}
                      </p>
                    </div>
                  </div>
                  <Badge className={getRoleColor(member.role)}>{member.role}</Badge>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
