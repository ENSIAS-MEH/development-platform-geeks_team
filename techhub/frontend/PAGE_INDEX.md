# TechHub - Complete Page Index

Index complet de toutes les pages et routes de la plateforme TechHub Version 1.

## 📄 Pages Publiques (4 pages)

### 1. Homepage
- **Route**: `/`
- **Fichier**: `/src/app/pages/home-page.tsx`
- **Description**: Page d'accueil avec hero animé, statistiques de la plateforme, événements en vedette, et CTA
- **Composants**: Navbar, EventCard, Button, Motion animations
- **Fonctionnalités**: 
  - Hero section avec animation fade-in
  - Stats cards animées (150+ événements, 500+ projets, 10K+ membres)
  - 3 événements en vedette
  - Sections Features et CTA
  - Footer complet

### 2. Explore Events Page
- **Route**: `/explore/events`
- **Fichier**: `/src/app/pages/explore-events-page.tsx`
- **Description**: Grille d'événements avec filtres avancés
- **Composants**: Navbar, EventCard, Input, Select, Badge
- **Fonctionnalités**:
  - Recherche par mot-clé
  - Filtres par type (Hackathon, Workshop, Conference, Meetup)
  - Filtres par date et localisation
  - Filtres par stack technique
  - 6 événements affichés

### 3. Explore Projects Page
- **Route**: `/explore/projects`
- **Fichier**: `/src/app/pages/explore-projects-page.tsx`
- **Description**: Navigation de projets open-source
- **Composants**: Navbar, ProjectCard, Input, Select, Badge
- **Fonctionnalités**:
  - Recherche de projets
  - Filtre par statut (Open, In Progress)
  - Filtre par tech stack
  - Filtres par compétences recherchées
  - 6 projets affichés

### 4. Login/Register Page
- **Route**: `/auth/login`
- **Fichier**: `/src/app/pages/login-page.tsx`
- **Description**: Authentification avec tabs Sign In/Register
- **Composants**: Tabs, Input, Label, Button
- **Fonctionnalités**:
  - Formulaire de connexion
  - Formulaire d'inscription
  - OAuth buttons (Google, GitHub)
  - Sélection de rôle (Student, Developer, Organizer)

---

## 👨‍💻 Pages Développeur/Étudiant (11 pages)

### 5. Dashboard Page
- **Route**: `/dashboard`
- **Fichier**: `/src/app/pages/dashboard-page.tsx`
- **Layout**: DashboardLayout (avec sidebar)
- **Description**: Tableau de bord personnalisé
- **Composants**: StatCard, EventCard, ProjectCard, Badge
- **Fonctionnalités**:
  - 4 KPI cards (Events, Projects, Team Members, Profile Views)
  - 2 événements à venir
  - 1 projet actif
  - Notifications récentes
  - Feed d'activité
  - Suggestions de collaborateurs

### 6. My Profile Page
- **Route**: `/dashboard/profile`
- **Fichier**: `/src/app/pages/my-profile-page.tsx`
- **Description**: Profil utilisateur public et éditable
- **Composants**: Badge, Button, Avatar
- **Fonctionnalités**:
  - Avatar et bio
  - Liste de compétences (8 skills)
  - Liens GitHub, LinkedIn, Portfolio
  - Localisation et date d'inscription
  - Projets contributés
  - Historique d'événements
  - Mode édition

### 7. Create Project Page
- **Route**: `/dashboard/projects/create`
- **Fichier**: `/src/app/pages/create-project-page.tsx`
- **Description**: Formulaire de création de projet
- **Composants**: Input, Textarea, Badge, Select, Button
- **Fonctionnalités**:
  - Titre et description
  - Sélection tech stack (avec badges dynamiques)
  - Compétences recherchées
  - Taille d'équipe
  - Statut du projet
  - Lien GitHub
  - Validation de formulaire

### 8. Project Detail Page
- **Route**: `/projects/:id`
- **Fichier**: `/src/app/pages/project-detail-page.tsx`
- **Description**: Page détaillée d'un projet
- **Composants**: Badge, Button, Avatar
- **Fonctionnalités**:
  - Description complète
  - Tech stack avec badges
  - Membres de l'équipe
  - Compétences recherchées
  - Bouton "Join Project"
  - Section commentaires
  - Lien GitHub

### 9. Event Detail Page
- **Route**: `/events/:id`
- **Fichier**: `/src/app/pages/event-detail-page.tsx`
- **Description**: Page détaillée d'un événement
- **Composants**: Badge, Button, Icons (Calendar, MapPin)
- **Fonctionnalités**:
  - Description de l'événement
  - Détails (date, lieu, participants)
  - Programme/Schedule
  - Bouton "Register Now"
  - Tags de technologies
  - Organisateur info

### 10. Find Collaborators Page
- **Route**: `/dashboard/collaborators`
- **Fichier**: `/src/app/pages/find-collaborators-page.tsx`
- **Description**: Recherche de collaborateurs par compétences
- **Composants**: Input, Badge, Button, Avatar
- **Fonctionnalités**:
  - Recherche par compétences
  - Filtres par rôle
  - Profils suggérés (3 affichés)
  - Badges de compétences
  - Localisation
  - Bouton "Contact"

### 11. My Teams Page
- **Route**: `/dashboard/teams`
- **Fichier**: `/src/app/pages/my-teams-page.tsx`
- **Description**: Liste des équipes de l'utilisateur
- **Composants**: Badge, Button, Avatar
- **Fonctionnalités**:
  - 3 équipes affichées
  - Type d'équipe (Project/Event)
  - Nombre de membres
  - Statut d'activité
  - Bouton "View Messages"

### 12. Community Groups Page
- **Route**: `/dashboard/communities`
- **Fichier**: `/src/app/pages/community-groups-page.tsx`
- **Description**: Groupes communautaires thématiques
- **Composants**: Badge, Button
- **Fonctionnalités**:
  - 6 groupes affichés
  - Nombre de membres
  - Tags de technologies
  - Niveau d'activité
  - Description
  - Bouton "Join"

### 13. Notifications Page
- **Route**: `/dashboard/notifications`
- **Fichier**: `/src/app/pages/notifications-page.tsx`
- **Description**: Fil de notifications chronologique
- **Composants**: Badge, Button
- **Fonctionnalités**:
  - Liste de notifications (8 affichées)
  - Types variés (Team Invite, Event Reminder, etc.)
  - Indicateur non lu
  - Horodatage relatif
  - Bouton "Mark all as read"
  - Filtres par type

### 14. Messaging Page
- **Route**: `/dashboard/messages`
- **Fichier**: `/src/app/pages/messaging-page.tsx`
- **Description**: Interface de messagerie
- **Composants**: Input, Button, ScrollArea
- **Fonctionnalités**:
  - Liste de conversations (4 affichées)
  - Badge de messages non lus
  - Thread de messages
  - Zone de saisie
  - Indicateur d'heure

### 15. Settings Page
- **Route**: `/dashboard/settings`
- **Fichier**: `/src/app/pages/settings-page.tsx`
- **Description**: Paramètres du compte
- **Composants**: Tabs, Input, Switch, Button
- **Fonctionnalités**:
  - 4 onglets (Account, Notifications, Privacy, Security)
  - Modification email/password
  - Préférences de notifications
  - Paramètres de confidentialité
  - Comptes liés (GitHub, Google)

---

## 🎟️ Pages Organisateur d'Événements (5 pages)

### 16. Organizer Dashboard Page
- **Route**: `/dashboard/organizer`
- **Fichier**: `/src/app/pages/organizer-dashboard-page.tsx`
- **Description**: Tableau de bord organisateur
- **Composants**: StatCard, Badge, Button, Table
- **Fonctionnalités**:
  - 4 KPI cards (Active Events, Registrations, Attendance, Completed)
  - Liste de 3 événements (Live, Draft, Ended)
  - Actions rapides (Edit, Analytics, Participants)
  - Bouton "Create Event"

### 17. Create Event Page
- **Route**: `/dashboard/organizer/events/create`
- **Fichier**: `/src/app/pages/create-event-page.tsx`
- **Description**: Formulaire multi-étapes de création d'événement
- **Composants**: Input, Textarea, Select, Button, DatePicker
- **Fonctionnalités**:
  - Step 1: Informations de base
  - Step 2: Description et agenda
  - Step 3: Paramètres d'inscription
  - Validation progressive
  - Sauvegarde brouillon

### 18. Manage Participants Page
- **Route**: `/dashboard/organizer/events/:id/participants`
- **Fichier**: `/src/app/pages/manage-participants-page.tsx`
- **Description**: Gestion des participants inscrits
- **Composants**: Table, Input, Badge, Button
- **Fonctionnalités**:
  - Table de 5 participants
  - Colonnes: Name, Email, Skills, Team, Date
  - Recherche
  - Actions bulk (Send message, Export CSV)
  - Filtres

### 19. Team Formation Page
- **Route**: `/dashboard/organizer/events/:id/teams`
- **Fichier**: `/src/app/pages/team-formation-page.tsx`
- **Description**: Interface de formation d'équipes
- **Composants**: Badge, Button
- **Fonctionnalités**:
  - Participants non assignés
  - Équipes formées
  - Compétences affichées
  - Bouton "Auto-assign teams"
  - Drag-and-drop (UI préparée)

### 20. Event Analytics Page
- **Route**: `/dashboard/organizer/events/:id/analytics`
- **Fichier**: `/src/app/pages/event-analytics-page.tsx`
- **Description**: Analytics de l'événement
- **Composants**: StatCard, LineChart, BarChart
- **Fonctionnalités**:
  - 3 KPI cards
  - Graphique d'inscriptions dans le temps
  - Distribution des compétences (bar chart)
  - Breakdown géographique

---

## 🛡️ Pages Administrateur (5 pages)

### 21. Admin Dashboard Page
- **Route**: `/dashboard/admin`
- **Fichier**: `/src/app/pages/admin-dashboard-page.tsx`
- **Description**: Tableau de bord administrateur global
- **Composants**: StatCard, Badge, Table
- **Fonctionnalités**:
  - 6 KPI cards (Users, Events, Projects, Active Users, etc.)
  - Panel d'alertes (3 alertes affichées)
  - Approbations en attente (2 affichées)
  - Actions rapides
  - Activité récente

### 22. User Management Page
- **Route**: `/dashboard/admin/users`
- **Fichier**: `/src/app/pages/user-management-page.tsx`
- **Description**: Gestion complète des utilisateurs
- **Composants**: Table, Input, Select, DropdownMenu, Badge
- **Fonctionnalités**:
  - Table de 10 utilisateurs
  - Recherche
  - Filtres par rôle et statut
  - Actions: View, Edit, Suspend, Delete
  - Pagination

### 23. Content Moderation Page
- **Route**: `/dashboard/admin/moderation`
- **Fichier**: `/src/app/pages/content-moderation-page.tsx`
- **Description**: File de modération de contenu
- **Composants**: Tabs, Badge, Button
- **Fonctionnalités**:
  - Tabs: Pending, Reviewed, Dismissed
  - 3 contenus signalés affichés
  - Type de contenu (Project, Comment, Profile)
  - Raison du signalement
  - Actions: Approve, Reject, Warn User

### 24. Event Approval Page
- **Route**: `/dashboard/admin/events`
- **Fichier**: `/src/app/pages/event-approval-page.tsx`
- **Description**: Approbation d'événements en attente
- **Composants**: Badge, Button
- **Fonctionnalités**:
  - 3 événements en attente
  - Détails de l'événement
  - Organisateur
  - Date de soumission
  - Actions: Approve, Reject, Request Changes

### 25. Platform Analytics Page
- **Route**: `/dashboard/admin/analytics`
- **Fichier**: `/src/app/pages/platform-analytics-page.tsx`
- **Description**: Analytics avancées de la plateforme
- **Composants**: StatCard, LineChart, BarChart, PieChart
- **Fonctionnalités**:
  - 4 KPI cards globaux
  - Croissance utilisateurs (line chart)
  - Création d'événements (bar chart)
  - Distribution des compétences (pie chart)
  - Top communautés (table)
  - Filtres de plage de dates

---

## 🚫 Page Système (1 page)

### 26. 404 Not Found Page
- **Route**: `*` (catch-all)
- **Fichier**: `/src/app/pages/not-found-page.tsx`
- **Description**: Page d'erreur 404 élégante
- **Composants**: Button
- **Fonctionnalités**:
  - Grand "404" stylisé
  - Message d'erreur convivial
  - Boutons "Go Home" et "Explore Events"

---

## 🧩 Composants Réutilisables

### Layout Components
- **Navbar** (`/src/app/components/navbar.tsx`)
  - Logo TechHub
  - Navigation principale (Events, Projects, Communities)
  - Boutons Login/Register

- **Footer** (`/src/app/components/footer.tsx`)
  - Logo et description
  - 4 colonnes de liens
  - Icônes réseaux sociaux
  - Copyright

### Card Components
- **EventCard** (`/src/app/components/event-card.tsx`)
  - Props: id, title, organizer, date, location, participants, tags, type
  - Hover effect avec translation
  - Badge de type d'événement

- **ProjectCard** (`/src/app/components/project-card.tsx`)
  - Props: id, name, description, techStack, status, teamSize, skillsNeeded
  - Badge de statut avec couleur dynamique
  - Tech stack et skills en badges

- **StatCard** (`/src/app/components/stat-card.tsx`)
  - Props: icon, label, value, trend, trendUp
  - Icône colorée
  - Indicateur de tendance (optionnel)

### Layouts
- **RootLayout** (`/src/app/layouts/root-layout.tsx`)
  - Layout de base pour toutes les pages

- **AuthLayout** (`/src/app/layouts/auth-layout.tsx`)
  - Layout centré pour pages d'authentification

- **DashboardLayout** (`/src/app/layouts/dashboard-layout.tsx`)
  - Layout avec sidebar pour pages dashboard
  - Navigation organisée par sections (Developer, Organizer, Admin)
  - Indicateur de route active

---

## 📊 Résumé

- **Total Pages**: 26
- **Pages Publiques**: 4
- **Pages Développeur**: 11
- **Pages Organisateur**: 5
- **Pages Admin**: 5
- **Pages Système**: 1
- **Composants Personnalisés**: 7
- **Composants UI**: 40+

---

## 🔗 Navigation Complète

```
/                                           → Homepage
/explore/events                              → Explore Events
/explore/projects                            → Explore Projects
/auth/login                                  → Login/Register
/events/:id                                  → Event Detail
/projects/:id                                → Project Detail
/dashboard                                   → Dashboard
/dashboard/profile                           → My Profile
/dashboard/projects/create                   → Create Project
/dashboard/collaborators                     → Find Collaborators
/dashboard/teams                             → My Teams
/dashboard/communities                       → Community Groups
/dashboard/notifications                     → Notifications
/dashboard/messages                          → Messaging
/dashboard/settings                          → Settings
/dashboard/organizer                         → Organizer Dashboard
/dashboard/organizer/events/create          → Create Event
/dashboard/organizer/events/:id/participants → Manage Participants
/dashboard/organizer/events/:id/teams       → Team Formation
/dashboard/organizer/events/:id/analytics   → Event Analytics
/dashboard/admin                             → Admin Dashboard
/dashboard/admin/users                       → User Management
/dashboard/admin/moderation                  → Content Moderation
/dashboard/admin/events                      → Event Approval
/dashboard/admin/analytics                   → Platform Analytics
*                                            → 404 Not Found
```

---

**TechHub Version 1.0.0** - Toutes les pages sont complètes et fonctionnelles ✅
