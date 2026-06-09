Here's everything you need — the Figma prompt, followed by the complete page breakdown.

---

## 🎨 Figma Prompt

> Design a modern, sophisticated web platform called **TechHub** — a centralized community hub for student developers and tech enthusiasts. The platform connects events, collaborative projects, and communities in one space.
>
> **Color palette:**
> - Background light: `#F0F4F8`
> - Accent muted: `#BAC7CC`
> - Primary brand teal: `#56B2BB`
> - Dark navy: `#1D2233`
> - Deep background / dark mode: `#0A0F22`
>
> **Design language:** Clean, professional, tech-forward. Use the dark navy `#1D2233` as the primary navbar and hero background. Use `#56B2BB` as the CTA and highlight color. Cards use `#F0F4F8` backgrounds with `#BAC7CC` borders. Typography is modern sans-serif (e.g. Inter or Sora). Generous white space, subtle rounded corners (8–12px), soft shadows on cards.
>
> **Pages to design (listed below):** Design each page at 1440px desktop width. Include responsive mobile breakpoints at 375px. Provide components in a design system panel (buttons, cards, nav, badges, inputs, modals).

---

Now here's the full page architecture:---

## 📄 All Pages — Full Description

### 🌐 Public pages (no login required)

**1. Homepage**
Hero section with platform value proposition, animated stats (events, projects, members), featured hackathons, and CTAs to register or explore. Navbar with logo, links, and Login/Register button.

**2. Explore events**
Filterable grid of upcoming hackathons, workshops, conferences, and meetups. Filters by type, date, location, and technology stack. Each card shows: title, organizer, date, tags, and participant count.

**3. Explore projects**
Browse open-source and student projects. Filter by tech stack, status (open to contributors / in progress), and skills needed. Cards show project name, description snippet, tech badges, and team size.

**4. Login / Register**
Two-tab form: sign in with email/password or OAuth (Google, GitHub). Registration collects name, role (student / developer / organizer), and primary skills.

---

### 👨‍💻 Developer / Student pages (authenticated)

**5. Dashboard**
Personalized home: upcoming events the user registered for, projects they're part of, suggested collaborators based on skills, and a community activity feed.

**6. My profile**
Public-facing profile with avatar, bio, skills tags, GitHub/portfolio links, project cards, and event participation history. Includes an edit mode.

**7. Create project**
Form to publish an idea or project: title, description, tech stack used, skills needed, project status, GitHub link, and team size sought. Rich text editor for description.

**8. Project detail**
Full project page: description, team members, tech stack badges, GitHub link, comments thread, and a "Join / propose contribution" CTA. Shows open roles needed.

**9. Event detail**
Full event page: description, schedule, organizer info, location/link, registration CTA, and a team formation panel (if the event is a hackathon).

**10. Find collaborators**
Skill-matching interface: user enters what they need (e.g. "React developer for EdTech hackathon"), and the platform suggests matching profiles. Can also browse all public profiles with filters.

**11. My teams**
List of teams the user belongs to (for events and projects). Each card shows team name, members with avatars, associated event or project, and a messaging shortcut.

**12. Community groups**
Browse and join thematic groups (e.g. "AI builders", "Web3 Casablanca", "Open Source Africa"). Each group has a description, member count, and activity feed.

**13. Notifications**
Chronological list of all notifications: event registrations confirmed, team invites, project join requests, new comments, and system alerts. Mark all as read, filter by type.

**14. Messaging**
Direct messages and group chats (team channels). Sidebar with conversation list, message thread, and file/link sharing. Real-time feel.

**15. Settings**
Tabs for: account info, password change, notification preferences (email / push), privacy (public/private profile), and linked accounts (GitHub, Google).

---

### 🎟️ Event Organizer pages

**16. Organizer dashboard**
Overview of all published events: status (draft / live / ended), registration counts, and quick actions. KPI cards: total registrations, active events, team count.

**17. Create / edit event**
Multi-step form: (1) basic info (title, type, date, location), (2) description and agenda, (3) registration settings (capacity, deadline, team size), (4) publish or save draft.

**18. Manage participants**
Table of registered participants with columns: name, skills, team assignment, registration date, status. Bulk actions: send message, export CSV, remove.

**19. Team formation**
Drag-and-drop interface to assign participants into teams for hackathons. Auto-suggest balanced teams based on skill diversity. Shows skill coverage per team.

**20. Event analytics**
Charts showing registration over time, skill distribution of participants, geographic breakdown, and engagement stats (views, shares). Exportable report.

---

### 🛡️ Administrator pages

**21. Admin dashboard**
Global platform KPIs: total users, events, projects, active communities. Alert panel for flagged content and pending approvals. Quick-action shortcuts.

**22. User management**
Searchable table of all users with role, join date, activity level, and status. Actions: view profile, change role, suspend/ban, send warning.

**23. Content moderation**
Queue of reported content (projects, comments, profiles) with reporter info, reason, and original content preview. Actions: dismiss, remove, warn user, escalate.

**24. Event approval**
List of submitted events pending review. Shows organizer name, event details, and flags (if any). Approve, reject with reason, or request changes.

**25. Platform analytics**
Advanced analytics: user growth curves, event creation trends, most active communities, top skills on the platform, and retention metrics. Date range filters.