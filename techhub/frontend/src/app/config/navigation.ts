// Navigation configuration for TechHub platform

export const publicRoutes = [
  { path: '/', label: 'Home' },
  { path: '/explore/events', label: 'Events' },
  { path: '/explore/projects', label: 'Projects' },
  { path: '/auth/login', label: 'Login' },
] as const;

export const dashboardRoutes = [
  { path: '/dashboard', label: 'Dashboard', icon: 'LayoutDashboard' },
  { path: '/dashboard/profile', label: 'My Profile', icon: 'User' },
  { path: '/dashboard/projects/create', label: 'Create Project', icon: 'FolderGit2' },
  { path: '/dashboard/collaborators', label: 'Find Collaborators', icon: 'Users' },
  { path: '/dashboard/teams', label: 'My Teams', icon: 'UsersRound' },
  { path: '/dashboard/communities', label: 'Communities', icon: 'Globe' },
  { path: '/dashboard/notifications', label: 'Notifications', icon: 'Bell' },
  { path: '/dashboard/messages', label: 'Messages', icon: 'MessageSquare' },
  { path: '/dashboard/settings', label: 'Settings', icon: 'Settings' },
] as const;

export const organizerRoutes = [
  { path: '/dashboard/organizer', label: 'Organizer Dashboard', icon: 'Calendar' },
  { path: '/dashboard/organizer/events/create', label: 'Create Event', icon: 'Plus' },
] as const;

export const adminRoutes = [
  { path: '/dashboard/admin', label: 'Admin Dashboard', icon: 'Shield' },
  { path: '/dashboard/admin/users', label: 'User Management', icon: 'Users' },
  { path: '/dashboard/admin/moderation', label: 'Content Moderation', icon: 'AlertTriangle' },
  { path: '/dashboard/admin/events', label: 'Event Approval', icon: 'Calendar' },
  { path: '/dashboard/admin/analytics', label: 'Platform Analytics', icon: 'BarChart3' },
] as const;

export const allRoutes = {
  public: publicRoutes,
  dashboard: dashboardRoutes,
  organizer: organizerRoutes,
  admin: adminRoutes,
} as const;
