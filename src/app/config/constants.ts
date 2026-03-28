// Constants for TechHub platform

// Event Types
export const EVENT_TYPES = [
  'Hackathon',
  'Workshop',
  'Conference',
  'Meetup',
] as const;

// Project Status Options
export const PROJECT_STATUS = [
  'Open to contributors',
  'In progress',
  'Completed',
  'On hold',
] as const;

// User Roles
export const USER_ROLES = [
  'Student',
  'Developer',
  'Organizer',
  'Admin',
] as const;

// Popular Tech Stacks
export const TECH_STACKS = [
  'React',
  'Vue',
  'Angular',
  'Node.js',
  'Python',
  'Java',
  'TypeScript',
  'JavaScript',
  'Go',
  'Rust',
  'PHP',
  'Ruby',
  'C++',
  'C#',
  'Swift',
  'Kotlin',
  'Flutter',
  'React Native',
  'Next.js',
  'Django',
  'FastAPI',
  'Express',
  'Nest.js',
  'Spring Boot',
  'PostgreSQL',
  'MongoDB',
  'MySQL',
  'Redis',
  'GraphQL',
  'REST API',
  'Docker',
  'Kubernetes',
  'AWS',
  'Azure',
  'GCP',
  'Tailwind CSS',
  'Bootstrap',
  'Material-UI',
  'TensorFlow',
  'PyTorch',
  'Blockchain',
  'Solidity',
  'Web3',
  'IPFS',
] as const;

// Skill Categories
export const SKILL_CATEGORIES = {
  frontend: [
    'React',
    'Vue',
    'Angular',
    'HTML/CSS',
    'JavaScript',
    'TypeScript',
    'Tailwind CSS',
    'UI/UX Design',
  ],
  backend: [
    'Node.js',
    'Python',
    'Java',
    'Go',
    'PHP',
    'Ruby',
    'C#',
    'REST API',
    'GraphQL',
  ],
  mobile: [
    'React Native',
    'Flutter',
    'iOS',
    'Android',
    'Swift',
    'Kotlin',
  ],
  data: [
    'Machine Learning',
    'Data Science',
    'TensorFlow',
    'PyTorch',
    'SQL',
    'Data Analysis',
  ],
  devops: [
    'Docker',
    'Kubernetes',
    'AWS',
    'Azure',
    'GCP',
    'CI/CD',
    'DevOps',
  ],
  blockchain: [
    'Blockchain',
    'Solidity',
    'Web3',
    'Smart Contracts',
    'Ethereum',
  ],
} as const;

// Time Ranges for Analytics
export const TIME_RANGES = [
  { label: 'Last 7 days', value: '7d' },
  { label: 'Last 30 days', value: '30d' },
  { label: 'Last 3 months', value: '3m' },
  { label: 'Last 6 months', value: '6m' },
  { label: 'Last year', value: '1y' },
  { label: 'All time', value: 'all' },
] as const;

// Notification Types
export const NOTIFICATION_TYPES = [
  'Team Invite',
  'Event Reminder',
  'Project Update',
  'Comment',
  'System',
  'Message',
] as const;

// Activity Levels
export const ACTIVITY_LEVELS = [
  'Very Active',
  'Active',
  'Moderate',
  'Low',
] as const;

// Sort Options
export const SORT_OPTIONS = {
  events: [
    { label: 'Date (Newest)', value: 'date-desc' },
    { label: 'Date (Oldest)', value: 'date-asc' },
    { label: 'Participants (Most)', value: 'participants-desc' },
    { label: 'Participants (Least)', value: 'participants-asc' },
    { label: 'Name (A-Z)', value: 'name-asc' },
    { label: 'Name (Z-A)', value: 'name-desc' },
  ],
  projects: [
    { label: 'Recently Updated', value: 'updated-desc' },
    { label: 'Name (A-Z)', value: 'name-asc' },
    { label: 'Name (Z-A)', value: 'name-desc' },
    { label: 'Team Size (Largest)', value: 'team-desc' },
    { label: 'Team Size (Smallest)', value: 'team-asc' },
  ],
} as const;

// Pagination
export const ITEMS_PER_PAGE = {
  events: 9,
  projects: 9,
  users: 12,
  notifications: 20,
  messages: 50,
} as const;

// Date Formats
export const DATE_FORMATS = {
  short: 'MMM DD, YYYY',
  long: 'MMMM DD, YYYY',
  full: 'dddd, MMMM DD, YYYY',
  time: 'HH:mm',
  datetime: 'MMM DD, YYYY HH:mm',
} as const;

// External Links
export const EXTERNAL_LINKS = {
  github: 'https://github.com',
  linkedin: 'https://linkedin.com',
  twitter: 'https://twitter.com',
  documentation: '#',
  support: '#',
  blog: '#',
} as const;

// Regular Expressions
export const REGEX = {
  email: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  url: /^https?:\/\/.+/,
  githubUrl: /^https?:\/\/(www\.)?github\.com\/.+/,
  linkedinUrl: /^https?:\/\/(www\.)?linkedin\.com\/.+/,
} as const;

// File Upload Limits
export const FILE_LIMITS = {
  maxSize: 5 * 1024 * 1024, // 5MB
  allowedTypes: ['image/jpeg', 'image/png', 'image/gif', 'image/webp'],
  allowedExtensions: ['.jpg', '.jpeg', '.png', '.gif', '.webp'],
} as const;

// Error Messages
export const ERROR_MESSAGES = {
  required: 'This field is required',
  invalidEmail: 'Please enter a valid email address',
  invalidUrl: 'Please enter a valid URL',
  passwordTooShort: 'Password must be at least 8 characters',
  passwordMismatch: 'Passwords do not match',
  fileTooLarge: 'File size must be less than 5MB',
  invalidFileType: 'Invalid file type',
  networkError: 'Network error. Please try again.',
  unauthorized: 'You are not authorized to perform this action',
  notFound: 'Resource not found',
} as const;

// Success Messages
export const SUCCESS_MESSAGES = {
  eventCreated: 'Event created successfully!',
  projectCreated: 'Project created successfully!',
  profileUpdated: 'Profile updated successfully!',
  settingsSaved: 'Settings saved successfully!',
  messageSent: 'Message sent successfully!',
  inviteSent: 'Invitation sent successfully!',
} as const;

// Default Values
export const DEFAULTS = {
  avatarUrl: '/default-avatar.png',
  eventImageUrl: '/default-event.jpg',
  projectImageUrl: '/default-project.jpg',
  pagination: {
    page: 1,
    limit: 10,
  },
} as const;
