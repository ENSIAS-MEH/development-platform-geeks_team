// TypeScript type definitions for TechHub platform

// Event Types

export type UserSummaryDTO = {
  id: number;
  name: string;
  userType: string;
  bio: string;
  location: string;
  skills: string[];
  email?: string | null;
};

export interface Event {
  id: string;
  title: string;
  organizer: string;
  date: string;
  location: string;
  participants: number;
  tags: string[];
  type: 'Hackathon' | 'Workshop' | 'Conference' | 'Meetup';
  description?: string;
  imageUrl?: string;
  capacity?: number;
  registrationDeadline?: string;
}

// Project Types
export interface Project {
  id: string;
  name: string;
  description: string;
  techStack: string[];
  status: 'Open to contributors' | 'In progress' | 'Completed' | 'On hold';
  teamSize: number;
  skillsNeeded?: string[];
  githubUrl?: string;
  imageUrl?: string;
  createdAt?: string;
  updatedAt?: string;
}

// User Types
export interface User {
  id: string;
  name: string;
  email?: string;
  role: 'Student' | 'Developer' | 'Organizer' | 'Admin';
  skills: string[];
  location?: string;
  bio?: string;
  avatarUrl?: string;
  githubUrl?: string;
  linkedinUrl?: string;
  portfolioUrl?: string;
  joinedAt?: string;
}

// Team Types
export interface Team {
  id: string;
  name: string;
  members: User[];
  projectId?: string;
  eventId?: string;
  createdAt: string;
}

// Community Types (legacy – used by other pages)
export interface Community {
  id: string;
  name: string;
  description: string;
  members: number;
  tags: string[];
  activity: 'Very Active' | 'Active' | 'Moderate' | 'Low';
  imageUrl?: string;
  createdAt?: string;
}

// ═══════════════════════════════════════════════════════════════════════
// Community Service Backend Types (match Spring Boot DTOs exactly)
// ═══════════════════════════════════════════════════════════════════════

// ── Enums ───────────────────────────────────────────────────────────────
export type Topic = 'WEB' | 'MOBILE' | 'AI_ML' | 'DEVOPS' | 'SECURITY' | 'GAME_DEV' | 'DATA' | 'OTHER';
export type PostType = 'DISCUSSION' | 'ANNOUNCEMENT' | 'RESOURCE';
export type MemberRole = 'OWNER' | 'MODERATOR' | 'MEMBER';

// ── Response DTOs ───────────────────────────────────────────────────────
export interface GroupResponse {
  id: string;
  name: string;
  description: string;
  topic: Topic;
  isPublic: boolean;
  ownerId: string;
  memberCount: number;
  createdAt: string;
}

export interface PostResponse {
  id: string;
  groupId: string;
  authorId: string;
  title: string;
  content: string;
  type: PostType;
  upvotes: number;
  commentCount: number;
  isPinned: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CommentResponse {
  id: string;
  postId: string;
  authorId: string;
  content: string;
  parentCommentId: string | null;
  upvotes: number;
  createdAt: string;
  replies: CommentResponse[] | null;
}

export interface GroupMemberResponse {
  id: string;
  groupId: string;
  userId: string;
  role: MemberRole;
  joinedAt: string;
}

// ── Request DTOs ────────────────────────────────────────────────────────
export interface GroupRequest {
  name: string;
  description?: string;
  topic: Topic;
  isPublic?: boolean;
}

export interface PostRequest {
  title: string;
  content: string;
  type: PostType;
}

export interface CommentRequest {
  content: string;
  parentCommentId?: string;
}

// ── Spring Boot Paginated Response ──────────────────────────────────────
export interface SpringPage<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;        // current page (0-indexed)
  first: boolean;
  last: boolean;
  empty: boolean;
}

// Notification Types
export interface Notification {
  id: string;
  type: 'Team Invite' | 'Event Reminder' | 'Project Update' | 'Comment' | 'System';
  message: string;
  time: string;
  unread: boolean;
  actionUrl?: string;
}

// Message Types
export interface Message {
  id: string;
  senderId: string;
  senderName: string;
  content: string;
  timestamp: string;
  conversationId: string;
}

export interface Conversation {
  id: string;
  name: string;
  lastMessage: string;
  time: string;
  unread: number;
  participants: User[];
}

// Analytics Types
export interface Stat {
  icon: any; // LucideIcon type
  label: string;
  value: string | number;
  trend?: string;
  trendUp?: boolean;
}

export interface ChartData {
  [key: string]: string | number;
}

// Form Types
export interface ProjectFormData {
  title: string;
  description: string;
  techStack: string[];
  skillsNeeded: string[];
  teamSize: number;
  status: Project['status'];
  githubUrl?: string;
}

export interface EventFormData {
  title: string;
  description: string;
  type: Event['type'];
  date: string;
  location: string;
  capacity: number;
  registrationDeadline: string;
  tags: string[];
}

// API Response Types
export interface ApiResponse<T> {
  data: T;
  message?: string;
  error?: string;
}

export interface PaginatedResponse<T> {
  data: T[];
  page: number;
  totalPages: number;
  totalItems: number;
}

// Filter Types
export interface EventFilters {
  type?: Event['type'];
  date?: string;
  location?: string;
  tags?: string[];
  searchQuery?: string;
}

export interface ProjectFilters {
  status?: Project['status'];
  techStack?: string[];
  skillsNeeded?: string[];
  searchQuery?: string;
}

// Auth Types
export interface LoginCredentials {
  email: string;
  password: string;
}

export interface RegisterData extends LoginCredentials {
  name: string;
  role: User['role'];
}

// Settings Types
export interface UserSettings {
  notifications: {
    email: boolean;
    push: boolean;
    eventReminders: boolean;
    projectUpdates: boolean;
    messages: boolean;
  };
  privacy: {
    profileVisibility: 'public' | 'private';
    showEmail: boolean;
    showLocation: boolean;
  };
  preferences: {
    theme: 'light' | 'dark' | 'system';
    language: string;
  };
}
