// TypeScript type definitions for TechHub platform

// Event Types
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

// Community Types
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
