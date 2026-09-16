import { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router";
import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Label } from "../components/ui/label";
import { Textarea } from "../components/ui/textarea";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "../components/ui/dialog";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../components/ui/select";
import { Users, Search, Plus, Globe, Lock, Loader2 } from "lucide-react";
import { COMMUNITY_TOPICS } from "../config/constants";
import { getGroups, searchGroups, createGroup, joinGroup, ApiError } from "../services/community-api";
import type { GroupResponse, GroupRequest, Topic } from "../types";

export function CommunityGroupsPage() {
  const navigate = useNavigate();

  // ─── State ──────────────────────────────────────────────────────────
  const [groups, setGroups] = useState<GroupResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedTopic, setSelectedTopic] = useState<string>("ALL");
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [creating, setCreating] = useState(false);
  const [joiningId, setJoiningId] = useState<string | null>(null);
  const [joinedIds, setJoinedIds] = useState<Set<string>>(new Set());

  // ─── Create Group Form ──────────────────────────────────────────────
  const [newGroup, setNewGroup] = useState<GroupRequest>({
    name: "",
    description: "",
    topic: "WEB",
    isPublic: true,
  });

  // ─── Fetch Groups ──────────────────────────────────────────────────
  const fetchGroups = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      let result;
      if (searchQuery.trim()) {
        result = await searchGroups(searchQuery.trim());
      } else {
        const topic = selectedTopic !== "ALL" ? selectedTopic : undefined;
        result = await getGroups(topic);
      }
      setGroups(result.content);
    } catch (err) {
      const msg = err instanceof ApiError ? err.message : "Failed to load groups";
      setError(msg);
    } finally {
      setLoading(false);
    }
  }, [searchQuery, selectedTopic]);

  useEffect(() => {
    fetchGroups();
  }, [fetchGroups]);

  // ─── Handlers ──────────────────────────────────────────────────────
  const handleCreateGroup = async () => {
    if (!newGroup.name.trim()) return;
    setCreating(true);
    try {
      await createGroup(newGroup);
      setCreateDialogOpen(false);
      setNewGroup({ name: "", description: "", topic: "WEB", isPublic: true });
      fetchGroups();
    } catch (err) {
      const msg = err instanceof ApiError ? err.message : "Failed to create group";
      alert(msg);
    } finally {
      setCreating(false);
    }
  };

  const handleJoinGroup = async (groupId: string) => {
    setJoiningId(groupId);
    try {
      await joinGroup(groupId);
      setJoinedIds((prev) => new Set(prev).add(groupId));
      fetchGroups(); // Refresh member count
    } catch (err) {
      const msg = err instanceof ApiError ? err.message : "Failed to join group";
      alert(msg);
    } finally {
      setJoiningId(null);
    }
  };

  const getTopicLabel = (topic: string) => {
    return COMMUNITY_TOPICS.find((t) => t.value === topic)?.label || topic;
  };

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

  // ─── Render ────────────────────────────────────────────────────────
  return (
    <div className="p-8">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between mb-8">
        <div>
          <h1 className="text-4xl font-bold text-[#1D2233] mb-2">Community Groups</h1>
          <p className="text-lg text-[#717182]">
            Join thematic groups and connect with like-minded developers
          </p>
        </div>

        {/* Create Group Button + Dialog */}
        <Dialog open={createDialogOpen} onOpenChange={setCreateDialogOpen}>
          <DialogTrigger asChild>
            <Button className="mt-4 md:mt-0 bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white gap-2">
              <Plus className="w-4 h-4" />
              Create Group
            </Button>
          </DialogTrigger>
          <DialogContent className="sm:max-w-[500px]">
            <DialogHeader>
              <DialogTitle>Create a New Group</DialogTitle>
              <DialogDescription>
                Start a community group for any tech topic.
              </DialogDescription>
            </DialogHeader>
            <div className="space-y-4 pt-4">
              <div>
                <Label htmlFor="group-name">Group Name *</Label>
                <Input
                  id="group-name"
                  placeholder="e.g., React Developers Morocco"
                  value={newGroup.name}
                  onChange={(e) => setNewGroup({ ...newGroup, name: e.target.value })}
                  maxLength={120}
                />
              </div>
              <div>
                <Label htmlFor="group-desc">Description</Label>
                <Textarea
                  id="group-desc"
                  placeholder="What is this group about?"
                  value={newGroup.description}
                  onChange={(e) => setNewGroup({ ...newGroup, description: e.target.value })}
                  rows={3}
                />
              </div>
              <div>
                <Label>Topic *</Label>
                <Select
                  value={newGroup.topic}
                  onValueChange={(val) => setNewGroup({ ...newGroup, topic: val as Topic })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select topic" />
                  </SelectTrigger>
                  <SelectContent>
                    {COMMUNITY_TOPICS.map((t) => (
                      <SelectItem key={t.value} value={t.value}>
                        {t.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div className="flex items-center gap-3">
                <Label>Visibility:</Label>
                <Button
                  type="button"
                  variant={newGroup.isPublic ? "default" : "outline"}
                  size="sm"
                  onClick={() => setNewGroup({ ...newGroup, isPublic: true })}
                  className={newGroup.isPublic ? "bg-[#56B2BB] hover:bg-[#56B2BB]/90" : ""}
                >
                  <Globe className="w-3 h-3 mr-1" /> Public
                </Button>
                <Button
                  type="button"
                  variant={!newGroup.isPublic ? "default" : "outline"}
                  size="sm"
                  onClick={() => setNewGroup({ ...newGroup, isPublic: false })}
                  className={!newGroup.isPublic ? "bg-[#1D2233] hover:bg-[#1D2233]/90" : ""}
                >
                  <Lock className="w-3 h-3 mr-1" /> Private
                </Button>
              </div>
              <Button
                onClick={handleCreateGroup}
                disabled={creating || !newGroup.name.trim()}
                className="w-full bg-[#56B2BB] hover:bg-[#56B2BB]/90"
              >
                {creating ? (
                  <>
                    <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                    Creating...
                  </>
                ) : (
                  "Create Group"
                )}
              </Button>
            </div>
          </DialogContent>
        </Dialog>
      </div>

      {/* Filters */}
      <div className="flex flex-col md:flex-row gap-4 mb-8">
        {/* Search */}
        <div className="relative flex-1 max-w-md">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[#717182]" />
          <Input
            id="group-search"
            placeholder="Search groups by name..."
            className="pl-10"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>

        {/* Topic Filter */}
        <div className="flex flex-wrap gap-2">
          <Button
            variant={selectedTopic === "ALL" ? "default" : "outline"}
            size="sm"
            onClick={() => { setSelectedTopic("ALL"); setSearchQuery(""); }}
            className={selectedTopic === "ALL" ? "bg-[#1D2233] hover:bg-[#1D2233]/90 text-white" : ""}
          >
            All Topics
          </Button>
          {COMMUNITY_TOPICS.map((topic) => (
            <Button
              key={topic.value}
              variant={selectedTopic === topic.value ? "default" : "outline"}
              size="sm"
              onClick={() => { setSelectedTopic(topic.value); setSearchQuery(""); }}
              className={
                selectedTopic === topic.value
                  ? "bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white"
                  : ""
              }
            >
              {topic.label}
            </Button>
          ))}
        </div>
      </div>

      {/* Loading State */}
      {loading && (
        <div className="flex items-center justify-center py-20">
          <Loader2 className="w-8 h-8 animate-spin text-[#56B2BB]" />
          <span className="ml-3 text-[#717182]">Loading groups...</span>
        </div>
      )}

      {/* Error State */}
      {error && (
        <div className="text-center py-20">
          <p className="text-red-500 mb-4">{error}</p>
          <Button onClick={fetchGroups} variant="outline">
            Retry
          </Button>
        </div>
      )}

      {/* Empty State */}
      {!loading && !error && groups.length === 0 && (
        <div className="text-center py-20">
          <Users className="w-16 h-16 text-[#BAC7CC] mx-auto mb-4" />
          <h3 className="text-xl font-semibold text-[#1D2233] mb-2">No groups found</h3>
          <p className="text-[#717182] mb-4">
            {searchQuery
              ? `No groups match "${searchQuery}"`
              : "Be the first to create a community group!"}
          </p>
          <Button
            onClick={() => setCreateDialogOpen(true)}
            className="bg-[#56B2BB] hover:bg-[#56B2BB]/90"
          >
            Create Group
          </Button>
        </div>
      )}

      {/* Groups Grid */}
      {!loading && !error && groups.length > 0 && (
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {groups.map((group) => (
            <div
              key={group.id}
              className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30 shadow-sm hover:shadow-md transition-shadow cursor-pointer"
              onClick={() => navigate(`/dashboard/communities/${group.id}`)}
            >
              <div className="flex items-start justify-between mb-3">
                <h3 className="font-bold text-[#1D2233] text-lg">{group.name}</h3>
                {group.isPublic ? (
                  <Globe className="w-4 h-4 text-[#56B2BB] flex-shrink-0" />
                ) : (
                  <Lock className="w-4 h-4 text-[#717182] flex-shrink-0" />
                )}
              </div>

              <div className="flex items-center gap-2 text-sm text-[#717182] mb-3">
                <Users className="w-4 h-4" />
                <span>{group.memberCount.toLocaleString()} members</span>
              </div>

              <p className="text-sm text-[#717182] mb-4 line-clamp-2">
                {group.description || "No description provided."}
              </p>

              <div className="flex flex-wrap gap-2 mb-4">
                <Badge className={`text-xs ${getTopicColor(group.topic)}`}>
                  {getTopicLabel(group.topic)}
                </Badge>
              </div>

              <Button
                className="w-full bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white"
                disabled={joiningId === group.id || joinedIds.has(group.id)}
                onClick={(e) => {
                  e.stopPropagation();
                  handleJoinGroup(group.id);
                }}
              >
                {joiningId === group.id ? (
                  <Loader2 className="w-4 h-4 animate-spin" />
                ) : joinedIds.has(group.id) ? (
                  "Joined ✓"
                ) : (
                  "Join Group"
                )}
              </Button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
