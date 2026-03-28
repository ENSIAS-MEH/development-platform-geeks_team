import { Outlet, Link, useLocation } from "react-router";
import { 
  LayoutDashboard, 
  User, 
  FolderGit2, 
  Users, 
  UsersRound, 
  Globe, 
  Bell, 
  MessageSquare, 
  Settings,
  Calendar,
  BarChart3,
  Shield
} from "lucide-react";

export function DashboardLayout() {
  const location = useLocation();
  
  const isActive = (path: string) => location.pathname === path || location.pathname.startsWith(path + '/');
  
  const navItems = [
    { path: "/dashboard", icon: LayoutDashboard, label: "Dashboard" },
    { path: "/dashboard/profile", icon: User, label: "My Profile" },
    { path: "/dashboard/projects/create", icon: FolderGit2, label: "Create Project" },
    { path: "/dashboard/collaborators", icon: Users, label: "Find Collaborators" },
    { path: "/dashboard/teams", icon: UsersRound, label: "My Teams" },
    { path: "/dashboard/communities", icon: Globe, label: "Communities" },
    { path: "/dashboard/notifications", icon: Bell, label: "Notifications" },
    { path: "/dashboard/messages", icon: MessageSquare, label: "Messages" },
    { path: "/dashboard/settings", icon: Settings, label: "Settings" },
  ];
  
  const organizerItems = [
    { path: "/dashboard/organizer", icon: Calendar, label: "Organizer Dashboard" },
  ];
  
  const adminItems = [
    { path: "/dashboard/admin", icon: Shield, label: "Admin Dashboard" },
    { path: "/dashboard/admin/analytics", icon: BarChart3, label: "Platform Analytics" },
  ];
  
  return (
    <div className="min-h-screen flex bg-[#F0F4F8]">
      {/* Sidebar */}
      <aside className="w-64 bg-[#1D2233] text-[#F0F4F8] flex flex-col">
        <div className="p-6">
          <Link to="/" className="flex items-center gap-2">
            <div className="w-8 h-8 bg-[#56B2BB] rounded-lg flex items-center justify-center">
              <span className="text-white font-bold">TH</span>
            </div>
            <span className="text-xl font-bold">TechHub</span>
          </Link>
        </div>
        
        <nav className="flex-1 px-3 space-y-1">
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`flex items-center gap-3 px-3 py-2.5 rounded-lg transition-colors ${
                  isActive(item.path)
                    ? "bg-[#56B2BB] text-white"
                    : "text-[#BAC7CC] hover:bg-[#0A0F22] hover:text-white"
                }`}
              >
                <Icon className="w-5 h-5" />
                <span>{item.label}</span>
              </Link>
            );
          })}
          
          <div className="pt-4 pb-2 px-3">
            <div className="text-xs text-[#BAC7CC] uppercase tracking-wider">Organizer</div>
          </div>
          {organizerItems.map((item) => {
            const Icon = item.icon;
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`flex items-center gap-3 px-3 py-2.5 rounded-lg transition-colors ${
                  isActive(item.path)
                    ? "bg-[#56B2BB] text-white"
                    : "text-[#BAC7CC] hover:bg-[#0A0F22] hover:text-white"
                }`}
              >
                <Icon className="w-5 h-5" />
                <span>{item.label}</span>
              </Link>
            );
          })}
          
          <div className="pt-4 pb-2 px-3">
            <div className="text-xs text-[#BAC7CC] uppercase tracking-wider">Admin</div>
          </div>
          {adminItems.map((item) => {
            const Icon = item.icon;
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`flex items-center gap-3 px-3 py-2.5 rounded-lg transition-colors ${
                  isActive(item.path)
                    ? "bg-[#56B2BB] text-white"
                    : "text-[#BAC7CC] hover:bg-[#0A0F22] hover:text-white"
                }`}
              >
                <Icon className="w-5 h-5" />
                <span>{item.label}</span>
              </Link>
            );
          })}
        </nav>
        
        <div className="p-4 border-t border-[#BAC7CC]/20">
          <Link to="/" className="text-sm text-[#BAC7CC] hover:text-white transition-colors">
            ← Back to Homepage
          </Link>
        </div>
      </aside>
      
      {/* Main Content */}
      <main className="flex-1 overflow-auto">
        <Outlet />
      </main>
    </div>
  );
}
