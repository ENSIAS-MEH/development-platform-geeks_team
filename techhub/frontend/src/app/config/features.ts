// Feature flags and configuration for TechHub platform

export const features = {
  // Version 1 - Completed Features
  v1: {
    publicPages: true,
    dashboardPages: true,
    organizerPages: true,
    adminPages: true,
    mockData: true,
    animations: true,
    charts: true,
    responsive: true,
  },
  
  // Version 2+ - Planned Features
  v2: {
    supabaseAuth: false,
    realTimeMessaging: false,
    fileUpload: false,
    notifications: false,
    payments: false,
    search: false,
    recommendations: false,
    githubIntegration: false,
    linkedinIntegration: false,
  },
} as const;

export const config = {
  app: {
    name: 'TechHub',
    version: '1.0.0',
    description: 'Connecting developers and tech enthusiasts worldwide',
  },
  
  branding: {
    logo: 'TH',
    tagline: 'Connect. Collaborate. Create.',
  },
  
  pagination: {
    eventsPerPage: 9,
    projectsPerPage: 9,
    usersPerPage: 12,
  },
  
  limits: {
    maxTeamSize: 10,
    maxProjectTechStack: 8,
    maxSkillsPerUser: 15,
    maxNotifications: 50,
  },
} as const;

// Social Media Links
export const socialLinks = {
  twitter: '#',
  github: '#',
  linkedin: '#',
} as const;

// API Endpoints (for future backend integration)
export const apiEndpoints = {
  auth: {
    login: '/api/auth/login',
    register: '/api/auth/register',
    logout: '/api/auth/logout',
  },
  events: {
    list: '/api/events',
    detail: '/api/events/:id',
    create: '/api/events',
    update: '/api/events/:id',
    delete: '/api/events/:id',
  },
  projects: {
    list: '/api/projects',
    detail: '/api/projects/:id',
    create: '/api/projects',
    update: '/api/projects/:id',
    delete: '/api/projects/:id',
  },
  users: {
    profile: '/api/users/me',
    update: '/api/users/me',
    list: '/api/users',
  },
  // ── Community Service (port 8085) ────────────────────────────────────
  community: {
    groups: '/api/groups',
    groupDetail: '/api/groups/:groupId',
    groupSearch: '/api/groups/search',
    joinGroup: '/api/groups/:groupId/join',
    leaveGroup: '/api/groups/:groupId/leave',
    groupMembers: '/api/groups/:groupId/members',
    groupPosts: '/api/groups/:groupId/posts',
    postDetail: '/api/groups/:groupId/posts/:postId',
    postUpvote: '/api/groups/:groupId/posts/:postId/upvote',
    postPin: '/api/groups/:groupId/posts/:postId/pin',
    postComments: '/api/groups/:groupId/posts/:postId/comments',
    commentUpvote: '/api/groups/:groupId/posts/:postId/comments/:commentId/upvote',
    popularPosts: '/api/posts/popular',
  },
} as const;
