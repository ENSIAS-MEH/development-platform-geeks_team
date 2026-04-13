# TechHub Platform 🚀

<div align="center">
  <h3>Connecting developers and tech enthusiasts worldwide</h3>
  <p>A modern, sophisticated web platform for student developers and tech enthusiasts to discover events, collaborate on projects, and build communities.</p>
</div>

---

## ✨ Features

### 🌐 Public Access
- **Homepage** with animated hero section and featured events
- **Event Explorer** with advanced filtering and search
- **Project Browser** to discover open-source opportunities
- **Authentication** with OAuth support (Google, GitHub)

### 👨‍💻 Developer Dashboard
- **Personalized Dashboard** with upcoming events and project activity
- **Profile Management** with skills, portfolio, and achievements
- **Project Creation** with tech stack and team requirements
- **Collaborator Matching** based on skills and interests
- **Team Management** for events and projects
- **Community Groups** to connect with like-minded developers
- **Real-time Notifications** and messaging

### 🎟️ Event Organizer Tools
- **Organizer Dashboard** with event analytics
- **Event Creation** with multi-step form
- **Participant Management** with bulk actions
- **Team Formation** with drag-and-drop interface
- **Event Analytics** with charts and insights

### 🛡️ Administrator Panel
- **Admin Dashboard** with platform KPIs
- **User Management** with role assignment
- **Content Moderation** queue
- **Event Approval** workflow
- **Platform Analytics** with advanced metrics

## 🎨 Design System

### Color Palette
- **Background Light**: `#F0F4F8`
- **Accent Muted**: `#BAC7CC`
- **Primary Teal**: `#56B2BB`
- **Dark Navy**: `#1D2233`
- **Deep Background**: `#0A0F22`

### Typography
- **Font**: Inter (Google Fonts)
- **Style**: Modern, clean, tech-forward

## 🛠️ Tech Stack

- **React 18.3** - UI Framework
- **TypeScript** - Type Safety
- **React Router 7** - Navigation & Routing
- **Tailwind CSS 4** - Styling
- **Motion** (Framer Motion) - Animations
- **Recharts** - Charts & Analytics
- **Radix UI** - Accessible Components
- **Lucide React** - Icon Library
- **Vite** - Build Tool

## 📦 Installation

```bash
# Install dependencies
pnpm install

# Start development server
pnpm run dev

# Build for production
pnpm run build
```

## 📂 Project Structure

```
/src/
├── app/
│   ├── components/
│   │   ├── ui/              # shadcn/ui components
│   │   ├── navbar.tsx       # Main navigation
│   │   ├── footer.tsx       # Global footer
│   │   ├── event-card.tsx   # Event card component
│   │   ├── project-card.tsx # Project card component
│   │   └── stat-card.tsx    # Statistics card
│   ├── config/
│   │   ├── colors.ts        # Color palette
│   │   ├── features.ts      # Feature flags
│   │   └── navigation.ts    # Route configuration
│   ├── data/
│   │   └── mock-data.ts     # Mock data (events, projects, users)
│   ├── layouts/
│   │   ├── root-layout.tsx
│   │   ├── auth-layout.tsx
│   │   └── dashboard-layout.tsx
│   ├── pages/               # 26 complete pages
│   ├── utils/
│   │   └── helpers.ts       # Utility functions
│   ├── App.tsx              # Main app component
│   └── routes.tsx           # React Router configuration
├── styles/
│   ├── fonts.css            # Font imports
│   ├── theme.css            # CSS variables & theme
│   ├── tailwind.css         # Tailwind configuration
│   └── index.css            # Global styles
└── main.tsx                 # Entry point
```

## 🗺️ Routes

### Public Routes
- `/` - Homepage
- `/explore/events` - Browse events
- `/explore/projects` - Browse projects
- `/auth/login` - Login/Register
- `/events/:id` - Event details
- `/projects/:id` - Project details

### Dashboard Routes (Authenticated)
- `/dashboard` - Main dashboard
- `/dashboard/profile` - User profile
- `/dashboard/projects/create` - Create project
- `/dashboard/collaborators` - Find collaborators
- `/dashboard/teams` - My teams
- `/dashboard/communities` - Community groups
- `/dashboard/notifications` - Notifications
- `/dashboard/messages` - Messaging
- `/dashboard/settings` - Settings

### Organizer Routes
- `/dashboard/organizer` - Organizer dashboard
- `/dashboard/organizer/events/create` - Create event
- `/dashboard/organizer/events/:id/participants` - Manage participants
- `/dashboard/organizer/events/:id/teams` - Team formation
- `/dashboard/organizer/events/:id/analytics` - Event analytics

### Admin Routes
- `/dashboard/admin` - Admin dashboard
- `/dashboard/admin/users` - User management
- `/dashboard/admin/moderation` - Content moderation
- `/dashboard/admin/events` - Event approval
- `/dashboard/admin/analytics` - Platform analytics

## 📊 Current Status

**Version**: 1.0.0  
**Status**: ✅ Complete - Ready for demonstration  
**Pages**: 26/26 (100%)  
**Components**: Complete  

### ✅ Version 1 Features (Complete)
- [x] Public pages with events and projects exploration
- [x] Complete authentication flow UI
- [x] Developer dashboard with personalized content
- [x] Event organizer tools and analytics
- [x] Administrator panel with platform management
- [x] Responsive design for all screen sizes
- [x] Smooth animations and transitions
- [x] Interactive charts and data visualization
- [x] Mock data for demonstration

### 🔮 Planned for Version 2+
- [ ] Supabase backend integration
- [ ] Real authentication with OAuth
- [ ] Persistent database
- [ ] Real-time messaging
- [ ] File uploads
- [ ] Push notifications
- [ ] Advanced search
- [ ] Recommendation engine
- [ ] GitHub/LinkedIn integration
- [ ] Payment processing for premium events

## 📖 Documentation

- [**Complete Documentation**](TECHHUB_VERSION1.md) - Full platform overview
- [**Development Guide**](DEVELOPMENT_GUIDE.md) - Developer guidelines
- [**Design Specifications**](src/imports/pasted_text/techhub-platform-design.md) - Original design document

## 🎯 Key Features Highlights

### Animations
Smooth, professional animations powered by Motion throughout the application.

### Analytics
Rich data visualization with Recharts for events, projects, and platform metrics.

### Responsive Design
Fully responsive layout optimized for desktop, tablet, and mobile devices.

### Type Safety
Full TypeScript coverage for better developer experience and fewer bugs.

### Accessibility
Built with Radix UI primitives ensuring WCAG compliance.

### Modern Stack
Latest versions of React, Tailwind CSS, and other modern web technologies.

## 🤝 Contributing

This is Version 1 of the TechHub platform. Future versions will include:
- Backend integration
- Real-time features
- User authentication
- Database persistence
- And much more!

## 📄 License

This project is part of a prototype demonstration.

---

<div align="center">
  <p><strong>Built with ❤️ for the developer community</strong></p>
  <p>TechHub - Connect. Collaborate. Create.</p>
</div>
