import { createBrowserRouter } from "react-router";
import { RootLayout } from "./layouts/root-layout";
import { AuthLayout } from "./layouts/auth-layout";
import { DashboardLayout } from "./layouts/dashboard-layout";

// Public pages
import { HomePage } from "./pages/home-page";
import { ExploreEventsPage } from "./pages/explore-events-page";
import { ExploreProjectsPage } from "./pages/explore-projects-page";
import { LoginPage } from "./pages/login-page";

// Authenticated Developer/Student pages
import { DashboardPage } from "./pages/dashboard-page";
import { MyProfilePage } from "./pages/my-profile-page";
import { CreateProjectPage } from "./pages/create-project-page";
import { ProjectDetailPage } from "./pages/project-detail-page";
import { EventDetailPage } from "./pages/event-detail-page";
import { FindCollaboratorsPage } from "./pages/find-collaborators-page";
import { MyTeamsPage } from "./pages/my-teams-page";
import { CommunityGroupsPage } from "./pages/community-groups-page";
import { NotificationsPage } from "./pages/notifications-page";
import { MessagingPage } from "./pages/messaging-page";
import { SettingsPage } from "./pages/settings-page";

// Event Organizer pages
import { OrganizerDashboardPage } from "./pages/organizer-dashboard-page";
import { CreateEventPage } from "./pages/create-event-page";
import { ManageParticipantsPage } from "./pages/manage-participants-page";
import { TeamFormationPage } from "./pages/team-formation-page";
import { EventAnalyticsPage } from "./pages/event-analytics-page";

export const router = createBrowserRouter([
  {
    path: "/",
    Component: RootLayout,
    children: [
      // Public pages
      { index: true, Component: HomePage },
      { path: "explore/events", Component: ExploreEventsPage },
      { path: "explore/projects", Component: ExploreProjectsPage },
      { path: "events/:id", Component: EventDetailPage },
      { path: "projects/:id", Component: ProjectDetailPage },
      
      // Auth
      {
        path: "auth",
        Component: AuthLayout,
        children: [
          { path: "login", Component: LoginPage },
        ],
      },
      
      // Authenticated Developer/Student pages
      {
        path: "dashboard",
        Component: DashboardLayout,
        children: [
          { index: true, Component: DashboardPage },
          { path: "profile", Component: MyProfilePage },
          { path: "projects/create", Component: CreateProjectPage },
          { path: "collaborators", Component: FindCollaboratorsPage },
          { path: "teams", Component: MyTeamsPage },
          { path: "communities", Component: CommunityGroupsPage },
          { path: "notifications", Component: NotificationsPage },
          { path: "messages", Component: MessagingPage },
          { path: "settings", Component: SettingsPage },
          
          // Event Organizer pages
          { path: "organizer", Component: OrganizerDashboardPage },
          { path: "organizer/events/create", Component: CreateEventPage },
          { path: "organizer/events/:id/participants", Component: ManageParticipantsPage },
          { path: "organizer/events/:id/teams", Component: TeamFormationPage },
          { path: "organizer/events/:id/analytics", Component: EventAnalyticsPage },
        ],
      },
    ],
  },
]);
